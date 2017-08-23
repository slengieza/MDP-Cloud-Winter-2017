package com.mdp.sparkbatching;

import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;

import org.json.JSONObject;

public class SparkClient {
    public static void main(String[] args) {
        System.out.println("Please Enter Which File To Work On: ");
        try{
            //Process display = Runtime.getRuntime().exec("hdfs dfs -ls /var/mdp-cloud/");
            //display.waitFor();
            ProcessBuilder pb = new ProcessBuilder("hdfs dfs -ls /var/mdp-cloud/");
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Scanner scans = new Scanner(System.in);
        String fileIn;
        fileIn = scans.nextLine();
        SparkConf conf = new SparkConf().setAppName("Spark Client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
        DataFrame df = sqlContext.read().json("hdfs:///var/mdp-cloud/" + fileIn);
        df.show();
    }
}
