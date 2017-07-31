package com.mdp.hadoop;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.javax.json.*;

import org.influxdb.*;

public class HadoopWriteClient{
    private String series = "";
    private InfluxDB influxdb;
    private List<JsonObject> WriteData;

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

    public HadoopWriteClient(InfluxBD influxIn, final String seriesIn){
        this.influxdb = influxIn;
        this.series = seriesIn;


    }

   /**
    * This initializer is called from HadoopWriter in HadoopClient, if the option
    * to add all data from the selected database is chosen
    *
    * @param influxIn
    *                An open connection to our InfluxDB database
    **/

    public HadoopWriteClient(InfluxBD influxIn){

    }

    private void addSeriesData(final String seriesIn){
        // Select all values in our series passed in
        String queryCommand = "SELECT * FROM " + seriesIn;
        Query queryIn = Query(queryCommand, "test");
        QueryResult query = influxdb.query(queryIn);
        List<Result> qResults = query.getResults();
        for(Result res : qResults){
            List<Series> seriesValues = res.getSeries();
            for(Series ser : seriesValues){
                System.out.println(Arrays.toString(ser.getColumns().toArray()));
                System.out.println(Arrays.toString(ser.getValues().toArray()));
            }
        }
    }


}
