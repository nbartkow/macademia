/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.articleClustering;
import edu.umd.cloud9.collection.wikipedia.WikipediaPage;
import edu.umd.cloud9.collection.wikipedia.WikipediaPageInputFormat;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author Nathaniel Miller
 */
public class ArticleClusterMapReduce extends Configured implements Tool{
 
    /**
     * Input: Wikipedia pages
     *
     * Output: "developed   anarchism|development"
     * Key is article name and value is list of linked article names
     */
    
    private static class MyMapper extends MapReduceBase implements Mapper<LongWritable, WikipediaPage, Text, Text> {

        /*public void configure(JobConf conf) {
            // read in the id mapping.
           //Mapping doesn't exist for this job, and I don't know what the equivalent method for mapper would be anyways...
         * try {

                  PATH_WORDS = conf.get("PATH_WORDS");
                  if (PATH_WORDS != null) {
                      Path wordPath = new Path(PATH_WORDS);
                      FileSystem hdfs = FileSystem.get(wordPath.toUri(), conf);
                      BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(wordPath), "UTF-8"));
                      while (true) {
                          String line = reader.readLine();
                          if (line == null) {
                              break;
                          }
                          String tokens[] = line.split("\t");
                          pageRanks.put(tokens[0].trim(), Integer.valueOf(tokens[1].trim()));

                      }
                  }

              } catch (IOException ex) {
                  Logger.getLogger(WordCounter.class.getName()).log(Level.SEVERE, null, ex);
              }
        }*/
        
          @Override
        public void map(LongWritable key, WikipediaPage p,
                OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
            for (String l : p.extractLinkDestinations()) {
                output.collect(new Text(p.getTitle()), new Text(l));
                output.collect(new Text(l), new Text(p.getTitle()));
            }
        }

    }


    private static class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
            Set<String> links = new HashSet<String>();
            while (values.hasNext()) {
                links.add(values.next().toString());
            }
            StringBuilder sb = new StringBuilder();
            for (String s : links) {
                sb.append(s).append("\\|");
            }
            sb.deleteCharAt(sb.length());
            output.collect(key, new Text(sb.toString()));
        }
    }


    /**
     * Runs this tool.
     */
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: [input output]");
            ToolRunner.printGenericCommandUsage(System.out);
            return -1;
        }
        Path inputPath = new Path(args[0]);
        Path outputPath = new Path(args[1]);

        JobConf job = new JobConf(getConf(),this.getClass());
        
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setInputFormat(WikipediaPageInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        
        FileSystem hdfs = FileSystem.get(outputPath.toUri(), job);
        if (hdfs.exists(outputPath)) {
            hdfs.delete(outputPath, true);
        }

        
        JobClient.runJob(job);

        return 0;
    }

    /**
     * Dispatches command-line arguments to the tool via the
     * <code>ToolRunner</code>.
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ArticleClusterMapReduce(), args);
        System.exit(res);
        return;
    }
}
