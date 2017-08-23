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
        System.out.println("Current HDFS directory :");
        try{
            String line;
            Process display = Runtime.getRuntime().exec("hdfs dfs -ls /var/mdp-cloud/");
            BufferedReader input = new BufferedReader(new InputStreamReader(display.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Please Enter Which File To Work On (file name not path) :");
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
