package com.mdp.sparkstreaming;

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
        String logFile = "hdfs:///var/mdp-cloud/";
        System.out.println("Please Enter Which File To Work On: ");
        try{
            Process display = Runtime.getRuntime().exec("hdfs dfs -ls ");
            display.waitFor();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        SparkConf conf = new SparkConf().setMaster("hdfs:///var/mdp-cloud/").setAppName("Spark Client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
        DataFrame df = sqlContext.read().json("hdfs:///var/mdp-cloup/test1.txt");
        df.show();
    }
}
