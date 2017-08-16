package com.mdp.spark;

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
            diplay.waitFor();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Scanner scans = new Scanner(System.in);
        logFile += scans;

        SparkConf conf = new SparkConf().setAppName("Spark Client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
        DataFrame df = sqlContext.read().json("hdfs:///var/mdp-cloup/test1.txt");
        df.show();
    }
}
