package com.mdp.hdfs;

import java.io.*;
import java.util.*;
import java.lang.*;

import org.json.JSONObject;

import org.influxdb.*;
import org.influxdb.impl.*;
import org.influxdb.dto.*;
import org.influxdb.dto.QueryResult;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

public class HadoopWriteClient{
    private String series = "";
    private InfluxDB influxdb;
    //private List<JSONObject> WriteData;

  /**
   * This initializer is called from HadoopWriter in HadoopClient, if the option
   * to only add one series is selected
   *
   * @param influxIn
   *                An open connection to our InfluxDB database
   *
   * @param seriesIn
   *                The name of the series we intend to write into Hadoop
   **/
    public HadoopWriteClient(InfluxDB influxIn, final String seriesIn){
        this.influxdb = influxIn;
        this.series = seriesIn;
        addSeriesData(this.series);

    }

   /**
    * This initializer is called from HadoopWriter in HadoopClient, if the option
    * to add all data from the selected database is chosen
    *
    * @param influxIn
    *                An open connection to our InfluxDB database
    **/
    public HadoopWriteClient(InfluxDB influxIn){

    }

   /**
    * This Function adds all the data from a specific series to Hadoop. If we
    * intend to write all of our data to Hadoop, we will call this function once
    * for every series
    *
    * @param seriesIn
    *                The name of the series we intend to write to Hadoop
    **/
    private void addSeriesData(final String seriesIn){
        // Select all values in our series passed in
        String queryCommand = "SELECT * FROM " + seriesIn;
        Query queryIn = new Query(queryCommand, "test"); // Influx's API is grabage, but this is how it works
        QueryResult query = influxdb.query(queryIn); // Bunch of different steps, it really should be easier
        List<QueryResult.Result> qResults = query.getResults(); // Really? But this will return only one result
        for(QueryResult.Result res : qResults){
            List<QueryResult.Series> seriesValues = res.getSeries(); // This will return only one series, as a result of how we set up our addSeriesData method
            for(QueryResult.Series ser : seriesValues){
                // getColumns returns the labels for our JSON, and getValues returns the values
                List<String> JsonKeys = ser.getColumns();
                List<List<Object>> JsonVals = ser.getValues();
                csvToJson(JsonKeys, JsonVals); // Call our function which contains a list of JSON Objects
            }
        }
    }

    private void csvToJson(List<String> keys, List<List<Object>> values){
        Collection<JSONObject> JSON_Objects = new ArrayList<JSONObject>();
        int i = 0;
        System.out.println(values.size());
        while(i < values.size()){
            JSONObject jo = lineJson(keys, values.get(i));
            System.out.println(jo.toString());
            JSON_Objects.add(jo);
            i++;
        }
    }

    private JSONObject lineJson(List<String> keys, List<Object> values){
        JSONObject full = new JSONObject();
        full.put("Timestamp", rfc3339ToEpoch(values.get(0).toString()));
        int i = 1;
        JSONObject vals = new JSONObject();
        while(i < values.size()){
            vals.put(keys.get(i).toString(), (double)values.get(i));
            i++;
        }
        full.put("Values", vals);
        return full;
    }

    private Long rfc3339ToEpoch(String lineIn){
        String[] splits = lineIn.replaceAll("-|T|Z|:|\\.", " ").split(" ");
        Calendar calends = Calendar.getInstance();
        calends.set(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]), Integer.parseInt(splits[3]), Integer.parseInt(splits[4]), Integer.parseInt(splits[5]));
        Date dat = calends.getTime();
        Long epochTime = dat.getTime() + Long.parseLong(splits[6]);
        return epochTime;
    }


}
