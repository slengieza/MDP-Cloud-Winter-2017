package hadoop;

import java.io.*;
import java.util.*;
import java.lang.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

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

    public HadoopWriteClient(InfluxDB influxIn, final String seriesIn){
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

    public HadoopWriteClient(InfluxDB influxIn){

    }

    private void addSeriesData(final String seriesIn){
        // Select all values in our series passed in
        String queryCommand = "SELECT * FROM " + seriesIn;
        Query queryIn = new Query(queryCommand, "test");
        QueryResult query = influxdb.query(queryIn);
        List<QueryResult.Result> qResults = query.getResults();
        for(QueryResult.Result res : qResults){
            List<QueryResult.Series> seriesValues = res.getSeries();
            for(QueryResult.Series ser : seriesValues){
                System.out.println(Arrays.toString(ser.getColumns().toArray()));
                System.out.println(Arrays.toString(ser.getValues().toArray()));
            }
        }
    }


}
