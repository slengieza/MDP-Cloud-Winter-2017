package hadoop;

import java.io.*;
import java.lang.*;
import java.util.*;

import org.influxdb.*;
import org.influxdb.impl.*;
import org.influxdb.dto.*;

import hadoop.HadoopWriteClient;

public class HadoopClient{

    private String username = "hkardos";
    private String password = "Migffn##567";
    private String database = "https://migsae-influx.arc-ts.umich.edu:8086";
    private String dbName = "test";
    private String method = "";
    private String series = "";
    private InfluxDB influxdb;

    public HadoopClient(){
        this.influxdb = InfluxDBFactory.connect(this.database, this.username, this.password);
        this.method = HadoopMethod(); // Determine how much data we want to write to Hadoop (all or just one series)
        //HadoopWriter();
    }

   /**
    * Hadoop Writer, steers our flow between either writing to Hadoopa specific
    * series, or writing entire contents of our InfluxDB database
    **/
    private void HadoopWriter(){
        if(this.method.equals("series")){
            seriesSelect();
            HadoopWriteClient h1 = new HadoopWriteClient(this.influxdb, this.series); // Partial write constructor
        }
        else if (this.method.equals("all")) {
            HadoopWriteClient h2 = new HadoopWriteClient(this.influxdb); // Full write constructor
        }
    }

  /**
   *  TODO: Querying from Hadoop
   **/
    private void HadoopReader(){

    }

   /**
    * During testing, we will be adding data to Hadoop ad-hoc, therefore using this
    * label it's easier to keep our Hadoop file system as we actually want it
    **/
    private void seriesSelect(){
        Scanner scans = new Scanner(System.in);
        System.out.println("Current Series :");
        Query seriesQuery = new Query("SHOW SERIES", "test"); // Returns a Query object containing all measurements (series) in database test
        QueryResult seriesResult = this.influxdb.query(seriesQuery); // Conforming to the API
        List<List<Object>> values = seriesResult.getResults().get(0).getSeries().get(0).getValues();
        ArrayList<String> seriesAvail = new ArrayList<String>();
        for (Object value : values) {
            System.out.println(value.toString()); // Prints out all of the different series options
            seriesAvail.add(value.toString()); // Add Possible Series to check for later
        }
        System.out.println("--------------------------------------------------");
        System.out.print("Please enter the name of which series you'd like to add to Hadoop : ");
        String seriesNameIn = scans.nextLine();
        while(!(seriesAvail.contains(seriesNameIn))){
          System.out.println("Invalid series entered! Please enter a valid series :");
          seriesNameIn = scans.nextLine();
        }
        this.series = seriesNameIn;
    }

   /**
    * During testing, we need to be flexible with how we add data to Hadoop. For
    * production, we would likely have a script that works as follows:
    *       1) Query InfluxDB every three days for all data stored in database
    *       2) Write all data from InfluxDB to Hadoop
    *       3) Wipe all data from InfluxDB (USE PRODUCTION; DROP SERIES FROM *)
    * However, for testing, we want to be add specific data at will
    **/
    private String HadoopMethod(){
        System.out.println("Would you like to add all available data in Influx to Hadoop, or would you like to add a specific series? Enter Y to add all data, or N to add an individual series <Y / N> :");
        Scanner scans = new Scanner(System.in);
        String methodIn = scans.next();
        // While input mismatch between expected and received
        while((!(methodIn.equalsIgnoreCase("y"))) && (!(methodIn.equalsIgnoreCase("n")))){
          System.out.println("Invalid Option, please enter Y to add all data, or N to add an individual series <Y / N> :");
          methodIn = scans.next();
        }
        if(methodIn.equalsIgnoreCase("y")){
          return "all"; // Used to determine how much data we write to Hadoop
        }
        else{
          return "series"; // Could've used a boolean  ¯¯\_(ツ)_/¯¯
        }
    }

    public static void main(String[] args) {
        HadoopClient writer = new HadoopClient();
        writer.HadoopWriter();
    }
}
