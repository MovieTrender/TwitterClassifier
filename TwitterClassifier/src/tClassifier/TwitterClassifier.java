package tClassifier;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.mahout.math.VectorWritable;

public class TwitterClassifier {

	public static class ClassifierMap extends Mapper<Text, VectorWritable, Text, IntWritable> {
		
		private static Classifier classifier;
		
		@Override
		protected void setup(Context context) throws IOException {
			initClassifier(context);
		}

		private static void initClassifier(Context context) throws IOException {
			if (classifier == null) {
				synchronized (ClassifierMap.class) {
					if (classifier == null) {
						classifier = new Classifier(context.getConfiguration());
					}
				}
			}
		}

		 

		public void map(Text key, VectorWritable value, Context context) throws IOException, InterruptedException {
		

			int bestCategoryId = classifier.classify(value);
			IntWritable category = new IntWritable(bestCategoryId);
			context.write(key, category);
		

		}
			
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Arguments: [model] [tweet file] [output directory]");
			return;
		}
		String modelPath = args[0];
		//String dictionaryPath = args[1];
		//String documentFrequencyPath = args[2];
		String tweetsPath = args[1];
		String outputPath = args[2];

		Configuration conf = new Configuration();

		conf.setStrings(Classifier.MODEL_PATH_CONF, modelPath);
		
		
		Job job = new Job(conf, "TwitterClassifier");

		job.setJarByClass(TwitterClassifier.class);
		
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(VectorWritable.class);
		job.setMapperClass(ClassifierMap.class);

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(tweetsPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);
	}
}