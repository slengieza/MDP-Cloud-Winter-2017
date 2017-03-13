package com.mdp.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.ConsumerIterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import org.clapper.util.io.RollingFileWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.List;

import com.mdp.consumer.KafkaMessageConsumer;
import com.mdp.consumer.ConsumerListener;

/*
This class listens for messages and puts them into Hadoop
*/

public class HadoopConsumer implements ConsumerListener { 

    private SparkConf conf;
    private JavaSparkContext sparkContext;
    private PrintWriter writer;

    public HadoopConsumer(String appName, String master, String file){
        // this.conf = new SparkConf().setAppName(appName).setMaster(master);
        // this.sparkContext = new JavaSparkContext(conf);
        try{
            //PRODUCTION   
            String path = "C:\\Rockwell Automation\\WorkingDirectory\\Hadoop_data\\data${n}.dat";
            String javaPath = path.replace("\\", "/");
            //LOCAL
            // String path = "/Users/stevenlengieza/Research/MDP-Cloud-Fall-2016/Consumer/src/main/java/Hadoop/data/data${n}.dat";
            // String javaPath = path;
            this.writer = new RollingFileWriter(javaPath, null, 1048576, 0); //max file size is 1 MB
        }
        catch(Exception e){
            System.out.println("Exception " + e.getMessage());
        }

        (new Thread(new LoadData())).start();
    }

    public void onReceiveMessage(String message){
        try{
            writer.println(message);
        }
        catch(Exception e){
            System.out.println("Couldnt write message " + message);
        }
    }

    public void onShutdown(){
        System.out.println("Shutting down");
    }

    public class LoadData implements Runnable {

        private String moveData;
        private String loadData;
        private String removeData;
        private String removeHDFSData;

        public void LoadData(){
            this.moveData = "hdfs dfs -put ./data/*.dat data";
            this.loadData = "hive --hiveconf mapreduce.job.queuename=nsf-cloud -f ./SQL/runExample.sql";
            this.removeData = "rm ./data/*.dat";
            this.removeHDFSData = "hdfs dfs -rm data/*.dat";
        }

        public void run(){
            // while(true){
            //     try
            //     {
            //         Process moveProcess = Runtime.getRuntime().exec(moveData);//move data into fladoop
            //         Process loadProcess = Runtime.getRuntime().exec(loadData);//load data into database
            //         Process removeFiles = Runtime.getRuntime().exec(removeData);//remove data files
            //         Process removeHDFSFiles = Runtime.getRuntime().exec(removeHDFSData);//remove hdfs data files
            //         Thread.sleep(86400000); //Sleep for 1 day
            //     } 
            //     catch (Exception e)
            //     {
            //         e.printStackTrace();
            //     }
            // }
        }
    }
}