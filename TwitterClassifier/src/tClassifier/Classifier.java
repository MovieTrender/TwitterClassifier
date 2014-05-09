package tClassifier;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

public class Classifier {

	
	private static AbstractVectorClassifier classifier;
	private static NaiveBayesModel naiveBayesModel;
	public final static String MODEL_PATH_CONF = "modelPath";
	
	public Classifier(Configuration configuration) throws IOException {
		String modelPath = configuration.getStrings(MODEL_PATH_CONF)[0];
		
		naiveBayesModel= NaiveBayesModel.materialize(new Path(modelPath),configuration);
		classifier = new StandardNaiveBayesClassifier(naiveBayesModel);

	
	}

	public int classify(VectorWritable text) throws IOException {
		
		
		 Vector result = classifier.classifyFull(text.get());
	     
	     double bestScore = -Double.MAX_VALUE;
	     int bestCategoryId = -1;
	        for(Element element: result) {
	            int categoryId = element.index();
	            double score = element.get();
	            if (score > bestScore) {
	                bestScore = score;
	                bestCategoryId = categoryId;
	            }
	        }
	 
	     return bestCategoryId;
	     

		
	}


}