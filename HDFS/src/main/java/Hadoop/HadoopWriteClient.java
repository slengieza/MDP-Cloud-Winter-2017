package com.mdp.hdfs;


import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import java.lang.Integer;
import java.lang.Long;
import java.lang.Math;
import java.lang.String;

import java.nio.file.*;
import java.nio.charset.Charset;
import java.io.*;
import static java.nio.file.StandardOpenOption.*;

import org.json.JSONObject;

import org.apache.hadoop.fs.FileSystem;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.Query;
import org.influxdb.InfluxDB;

public class HadoopWriteClient{
    private InfluxDB influxdb;
    private ArrayList<JSONObject> WriteData = new ArrayList<JSONObject>();
    private ArrayList<String> SeriesList = new ArrayList<String>();

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
        addSeriesData(seriesIn);
        writeToFile();
        //fileToHadoop();
    }

   /**
    * This initializer is called from HadoopWriter in HadoopClient, if the option
    * to add all data from the selected database is chosen
    *
    * @param influxIn
    *                An open connection to our InfluxDB database
    **/
    public HadoopWriteClient(InfluxDB influxIn, ArrayList<String> seriesListIn){
        this.influxdb = influxIn;
        this.SeriesList = seriesListIn;
        for(String series : SeriesList){
            addSeriesData(series);
        }
        writeToFile();
        //fileToHadoop();
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
            // This will return only one series, as a result of how we set up our addSeriesData method
            List<QueryResult.Series> seriesValues = res.getSeries();
            for(QueryResult.Series ser : seriesValues){
                // getColumns returns the labels for our JSON, and getValues returns the values
                List<String> JsonKeys = ser.getColumns();
                List<List<Object>> JsonVals = ser.getValues();
                csvToJson(JsonKeys, JsonVals, seriesIn); // Call our function which contains a list of JSON Objects
            }
        }
    }

   /**
    * This function takes CSV data from a series and turns it into JSON Objects;
    * Each JSON Object represents one full point in the given series
    *
    * @param keys
    *                The list of different measurements (String) from a given series
    *
    * @param values
    *                The list of values associated with the different keys
    **/
    private void csvToJson(List<String> keys, List<List<Object>> values, String seriesIn){
        int i = 0;
        while(i < values.size()){ // Each line is one point -> while{...} is for each line in series
            pointToJSON(keys, values.get(i), seriesIn);
            i++;
        }
    }

   /**
    * This function takes in the keys and values for a single point and creates
    * a JSON Object and adds it to the collection of JSON Objects we are going
    * to write to Hadoop
    *
    * @param keys
    *                The list of different measurements (String) from series passed in
    * @param values
    *                The list of values associated with the different keys for this
    *                specific point
    **/
    private void pointToJSON(List<String> keys, List<Object> values, String seriesIn){
        JSONObject point = new JSONObject(); // JSONObject to be added to; in the form
                                            // {"Timestamp":timestamp,
                                            //  "Values": ["Measuremnt":measurement value, ...]}
        point.put("Timestamp", rfc3339ToEpoch(values.get(0).toString())); // Add Timestamp
        point.put("Series", seriesIn); // Add Series Information
        int i = 1; // Start at measurement after timestamp for second JSONObject
        JSONObject vals = new JSONObject(); // Make a second JSONObject, associated with Values
        while(i < values.size()){
            vals = vals.put(keys.get(i).toString(), (double)values.get(i)); // Add all measurements and values
            i++;
        }
        point = point.put("Values", vals); // Add all measurements to this JSONObject
        WriteData.add(point);
    }

   /**
    * This function takes in a timestamp in RFC3339 format (How InfluxDB returns
    * data) and turns it into epoch time to the precision of milliseconds;
    * RFC3339 is in the format: YYYY-MM-DDThh:mm:ss:nnnZ
    *
    *        Example: 2017-07-26T20:04:44.73Z (RFC3339) -> 1503792284730 (Epoch)
    *
    * @param lineIn
    *                The timestamp in RFC3339 format passed in from PointToJSON
    *
    * @return
    *                The Epoch time with precision in milliseconds of time passed in
    **/
    private Long rfc3339ToEpoch(String lineIn){
        String[] splits = lineIn.replaceAll("-|T|Z|:|\\.", " ").split(" "); // Regex for removing unnecessary characters
        Calendar calends = Calendar.getInstance(); // Java Interface for time
        // Splits[0] -> Year; Splits[1] -> Month; Splits[2] -> Day; Splits[3] -> Hours (24 Hour Format); Splits[4] -> Minutes; Splits[5] -> Seconds; Splits[6] -> milliseconds
        calends.set(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]), Integer.parseInt(splits[3]), Integer.parseInt(splits[4]), Integer.parseInt(splits[5]));
        Date dat = calends.getTime(); // Conform to interface
        // Milliseconds are important for our tests, so we must account for them accurately
        int milliseconds = 0;
        if(splits.length == 6){} // Don't need to add milliseconds
        else{ // Fix magnitude of value of milliseconds (i.e. if milliseconds is 100, the value of splits[6] is 1; if milliseconds is 10 then splits[6] is 01)
            if(splits[6].length() == 1){
                milliseconds = Integer.parseInt(splits[6]) * 100;
            }
            else{
                if(splits[6].length() == 2){
                    milliseconds = Integer.parseInt(splits[6]) * 10;
                }
                else{
                    milliseconds = Integer.parseInt(splits[6]);
                }
            }
        }
        Long epochTime = ((Long)dat.getTime() - ((Long)dat.getTime() % 1000) /* Subtract off error*/ ) + milliseconds;
        return epochTime;
    }

  /**
   * Takes all the JSON Objects we've already created before, and creates individual
   * files for each series in our directory /MDP-Cloud-Winter-2017/HDFS/files/
   *
   * NOTE: Only doing it this way for testing, will later merge to write to
   * Hadoop directly
   **/
    private void writeToFile(){
        for(JSONObject jo : WriteData){
            Path pathToFile = Paths.get(System.getProperty("user.dir"), "files", jo.get("Series") + ".txt");
            String s = jo.toString()+ "\n";
            Charset charset = Charset.forName("US-ASCII");
            String testFile = "test -e " + pathToFile.toString();
            String touchFile = "touch " + pathToFile.toString();
            try{
                Process testing = Runtime.getRuntime().exec(testFile);
                testing.waitFor();
                int returnVal = testing.exitValue();
                if(returnVal != 0){
                    Process touch = Runtime.getRuntime().exec(touchFile);
                    touch.waitFor();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try (BufferedWriter writer = Files.newBufferedWriter(pathToFile, charset, CREATE, APPEND)) {
                writer.write(s, 0, s.length());
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
        }
    }

    private void fileToHadoop(){
        File folder = new File(System.getProperty("user.dir") + "/files/");
        File [] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<String>();
        for(int i = 0; i < listOfFiles.length; ++i){
            String addToHadoop = "hdfs dfs -put " + listOfFiles[i].toString() + " /user/hkardos/" + listOfFiles[i].getName();
            String touchFile = "hdfs dfs -touchz /user/hkardos/" + listOfFiles[i].getName();
            String testFile = "hdfs dfs -test -e /user/hkardos/"+ listOfFiles[i].getName();
            try{
                Process testing = Runtime.getRuntime().exec(testFile);
                testing.waitFor();
                int returnVal = testing.exitValue();
                if(returnVal != 0){
                    Process touch = Runtime.getRuntime().exec(touchFile);
                    touch.waitFor();
                }
                Process moveToHadoop = Runtime.getRuntime().exec(addToHadoop);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            files.add(listOfFiles[i].toString());
        }
        for(int i = 0; i < files.size(); ++i){
            String removeLocal = "rm " + files.get(i);
            try{
                Process remove = Runtime.getRuntime().exec(removeLocal);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
