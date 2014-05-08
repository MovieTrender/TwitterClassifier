package tClassifier;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class TwitterClassifier {

	public static class ClassifierMap extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static Text outputKey = new Text();
		private final static IntWritable outputValue = new IntWritable();
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



		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] tokens = line.split("\t", 2);
			if (tokens.length < 2) {
				return;
			}
			String tweetId = tokens[0];
			String tweet = tokens[1];

			int bestCategoryId = classifier.classify(tweet);
			outputValue.set(bestCategoryId);

			outputKey.set(tweetId);
			context.write(outputKey, outputValue);
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		if (args.length < 5) {
			System.out.println("Arguments: [model] [dictionnary] [document frequency] [tweet file] [output directory]");
			return;
		}
		String modelPath = args[0];
		String dictionaryPath = args[1];
		String documentFrequencyPath = args[2];
		String tweetsPath = args[3];
		String outputPath = args[4];

		Configuration conf = new Configuration();

		conf.setStrings(Classifier.MODEL_PATH_CONF, modelPath);
		conf.setStrings(Classifier.DICTIONARY_PATH_CONF, dictionaryPath);
		conf.setStrings(Classifier.DOCUMENT_FREQUENCY_PATH_CONF, documentFrequencyPath);


		
		//Increasing Heap size
		//conf.set("mapreduce.map.java.opts", "-Xmx2048m");
		//conf.set("mapreduce.task.io.sort.mb", "1024");
		//conf.set("mapreduce.map.memory.mb","2048");
		//conf.set("mapreduce.job.jvm.numtasks", "1");
		//conf.set("mapreduce.job.maps", "1");
		//conf.set("mapred.job.shuffle.input.buffer.percent", "0.20");
		
		Job job = new Job(conf, "TwitterClassifier");

		job.setJarByClass(TwitterClassifier.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapperClass(ClassifierMap.class);

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(tweetsPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);
	}
}