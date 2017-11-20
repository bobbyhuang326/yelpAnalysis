
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
//import utils.MiscUtils;

//hadoop jar yelp.jar Q4 /yelp/review /user/hxy162130/Q4
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Q4 {

	public static class TopNMapper extends Mapper<Object, Text, Text, FloatWritable> {

        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        		String delims = "^";
			String[] businessData = StringUtils.split(value.toString(),delims);
			String businessID = businessData[2];
			float star = Float.parseFloat(businessData[3]);
            context.write(new Text(businessID),new FloatWritable (star) );
        }
    }

	public static class TopNReducer extends Reducer<Text, FloatWritable, Text, FloatWritable> {

        private Map<Text, FloatWritable> countMap = new HashMap<Text, FloatWritable>();

        @Override
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {

            // computes the number of occurrences of a single word
            float sumStar = 0;
            int sumUser = 0;
            for (FloatWritable val : values) {
                sumStar += val.get();
                sumUser++;
            }

            // puts the number of occurrences of this word into the map.
            // We need to create another Text object because the Text instance
            // we receive is the same for all the words
            float averageStar = sumStar/sumUser;
            countMap.put(new Text(key), new FloatWritable(averageStar));
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            Map<Text, FloatWritable> sortedMap = MiscUtils.sortByValues(countMap);

            int counter = 0;
            for (Text key : sortedMap.keySet()) {
                if (counter++ == 10) {
                    break;
                }
                context.write(key, sortedMap.get(key));
            }
        }
    }
	
//	public static class TopNCombiner extends Reducer<Text, FloatWritable, Text, FloatWritable> {
//
//        @Override
//        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
//
//            // computes the number of occurrences of a single word
//            int sumStar = 0;
//            for (FloatWritable val : values) {
//                sumStar += val.get();
//            }
//            context.write(key, new FloatWritable(sumStar));
//        }
//    }

  public static void main(String[] args) throws Exception {
	  Configuration conf = new Configuration();
      String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
      if (otherArgs.length != 2) {
          System.err.println("Usage: TopN <in> <out>");
          System.exit(2);
      }
      Job job = Job.getInstance(conf);
      job.setJobName("Top N");
      job.setJarByClass(Q4.class);
      job.setMapperClass(TopNMapper.class);
//      job.setCombinerClass(TopNCombiner.class);
      job.setReducerClass(TopNReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(FloatWritable.class);
      FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
      FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
      System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}