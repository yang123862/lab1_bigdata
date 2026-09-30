import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrandRelation {

    public static class RelationMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            //跳过表头 child parent
            if(line.startsWith("child") || line.isEmpty()){
                return;
            }
            //按任意空白分割
            String[] parts = line.split("\\s+");
            if(parts.length < 2){
                return;
            }
            String child  = parts[0];
            String parent = parts[1];

            // P:xxx 代表这是【孩子】
            context.write(new Text(parent), new Text("P:" + child));
            // C:xxx 代表这是【父辈（潜在祖辈）】
            context.write(new Text(child),  new Text("C:" + parent));
        }
    }

    public static class RelationReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<String> grandChildList  = new ArrayList<>();
            List<String> grandParentList = new ArrayList<>();

            for(Text val : values){
                String s = val.toString();
                if(s.startsWith("P:")){
                    grandChildList.add(s.substring(2));
                }else if(s.startsWith("C:")){
                    grandParentList.add(s.substring(2));
                }
            }
            //笛卡尔积输出 孙辈  祖辈
            for(String gc : grandChildList){
                for(String gp : grandParentList){
                    context.write(new Text(gc), new Text(gp));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf,"grand_relation");
        job.setJarByClass(GrandRelation.class);

        job.setMapperClass(RelationMapper.class);
        job.setReducerClass(RelationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
