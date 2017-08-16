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
            Process display = Runtime.getRuntime().exec()
        }
        Scanner scans = new Scanner(System.in);
        logFile += scans;

        SparkConf conf = new SparkConf().setAppName("Spark Client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

        JavaRDD<String> logData = sc.textFile(logFile).cache();

        JSONObject  = logData.map(new Function<String, JSONObject>() {
            public JSONObject call(String s) { return JSONObject(s); }
        }).count();

        long numBs = logData.filter(new Function<String, Integer>() {
            public Integer call(String s) { return s.contains("b"); }
        }).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
    }
}
