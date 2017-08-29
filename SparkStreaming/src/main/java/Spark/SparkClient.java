package com.mdp.sparkstreaming;

import java.io.*;
import java.lang.*;
import java.util.*;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;

import org.json.JSONObject;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class SparkClient {
    private String username = "hkardos";
    private String password = "Migffn##567";
    private String database = "https://migsae-influx.arc-ts.umich.edu:8086";
    private String dbName = "test";
    private String series;
    private InfluxDB influxDB;
    private String timestamp = "2017-08-28T12:51:25.701";

    public SparkClient(){
        this.influxDB = InfluxDBFactory.connect(database, username, password);
        seriesSelect();
        SparkConf conf = new SparkConf().setAppName("Spark Client").setMaster("yarn-client");
        JavaSparkContext sc = new JavaSparkContext(conf);
        try{
            while(true){
                addDataPoints();
                try {
                    Thread.sleep(6000);                 //6000 milliseconds is six seconds.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        catch(Exception ex){
            System.out.println("Function finished");
        }
    }

    /**
     * For testing, lets us decide which series we want to write our data to. Thus,
     * it's easier to distinguish between different experiments we run
     **/
      private void seriesSelect(){
        Scanner scans = new Scanner(System.in);
        try{
            System.out.println("Current Series :");
            Query seriesQuery = new Query("SHOW SERIES", "test");
            QueryResult seriesResult = this.influxDB.query(seriesQuery);
            List<List<Object>> values = seriesResult.getResults().get(0).getSeries().get(0).getValues();
            for (Object value : values) {
                System.out.println(value.toString()); // Prints out all of the different series options
            }
            System.out.println("--------------------------------------------------");
            System.out.print("Please enter the name of which series you'd like to use (if existing series, data will be appended to the end) : ");
            String seriesNameIn = scans.nextLine();
            seriesNameIn = seriesNameIn.replace("\"", "").replace("\'", ""); // Replace any Quotation Marks and Single Quotes
            this.series = seriesNameIn;
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.exit(9);
        }
      }

      public void addDataPoints(){
          try{
              Query seriesQuery = new Query("SELECT * FROM " + series + " WHERE time > \'" + timestamp + "Z\'", "test");
              QueryResult seriesResult = this.influxDB.query(seriesQuery);
              List<List<Object>> values = seriesResult.getResults().get(0).getSeries().get(0).getValues();
              Object HackAround = values.get(0).get(0);
              for (Object value : values) {
                  HackAround = value;
              }
              String hackString = HackAround.toString();
              int index = hackString.indexOf('Z');
              timestamp = hackString.substring(1,index);
              System.out.println(HackAround.toString());
          }
          catch(Exception ex){
              System.out.println(timestamp);
              ex.printStackTrace();
          }
      }

      public static void main(String[] args) {
          SparkClient sp = new SparkClient();
      }


}
