package com.mdp.data;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.List;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

import java.util.*;

import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

//changes
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class InfluxReader {

    private String username = "cloud_data";
    private String password = "2016SummerProj";
    private String database = "https://migsae-influx.arc-ts.umich.edu:8086";
    private String dbName = "test";
    private InfluxDB influxDB;

    public InfluxReader(InfluxDB influxdb){
        this.influxDB = influxdb;
    }

    public InfluxReader(String username, String password, String database){
        this.influxDB = InfluxDBFactory.connect(database, username, password);
    }

    public void execute(String queryString){    
        try {    
            File file = new File("example.txt");
            FileWriter output = new FileWriter(file);

            Query query = new Query(queryString, dbName);
            QueryResult result = influxDB.query(query);
            List<List<Object>> values = result.getResults().get(0).getSeries().get(0).getValues();
            for (Object value : values) {
                String tmp = value.toString();
                output.write(tmp);
                output.write("\n");

                //System.out.println(value.toString());
            }
            output.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}