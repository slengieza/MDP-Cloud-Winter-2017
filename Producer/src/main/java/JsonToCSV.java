package com.mdp.simulation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.nio.file.*;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonArray; //added this
import javax.json.JsonObject; //added this


import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.System;

// import org.json.JsonValue;


public class JsonToCSV {

	private static HashMap<Long, List<String>> kafkaMessages;

	public JsonToCSV(){
		kafkaMessages = new HashMap<Long, List<String>>();
	}

	public static void main(String[] args){
        
		JsonToCSV converter = new JsonToCSV();

        //LOCAL
        Path dir = Paths.get("/Users/stevenlengieza/Documents/college/Research/MDP-Cloud-Winter-2017/data/New_Data"); //path to .dat files
        //send files that are already there
        File folder = new File(dir.toString()); 
        File[] listOfFiles = folder.listFiles();
        
        for (File file : listOfFiles) {
            if (file.toString().toLowerCase().endsWith(".dat")){
                converter.getCSV(file);
            }
        }
        try{
		    PrintWriter writer = new PrintWriter("/Users/stevenlengieza/Documents/college/Research/MDP-Cloud-Winter-2017/data/New_Data/test.csv", "UTF-8");
		    writer.println("TimeStamp, FanucFreq, FanucCurrent, FanucVoltage, ABBFreq, ABBCurrent, ABBVoltage, RFID56, RFID57, RFID54, RFID55");
		    Iterator it = kafkaMessages.entrySet().iterator();
		    while (it.hasNext()) {
		    	Map.Entry pair = (Map.Entry)it.next();
		    	List<String> values = (List)pair.getValue();
		    	String v = "";
		    	for(String val : values){
		    		v += val + ",";
		    	}
		    	v = v.substring(0, v.length()-1);
			    writer.println(v);
			    it.remove();
			}
		    writer.close();
		} 
		catch (IOException e) {
		   // do something
		}
	}

	public static void getCSV(File jsonfile) {
		// Initialize this array to check later
		//JSONArray array = null;
		JsonArray array = null; 

		int numDataPoints = 6; //TODO must change when we add more data (its number of data points * 2 because we have one for each loop)
		int total_values = 0;

		// Use try-catch to avoid RunTimeError
		// when the file doesn't exist
		// or somehow the program cannot open the file
		  try {
		   	InputStream fis = new FileInputStream(jsonfile);
			JsonReader jsonReader = Json.createReader(fis);
			//array = new JSONArray(jsonReader.readArray());
			array = jsonReader.readArray(); //this is the error
			jsonReader.close();
			fis.close();
		  } 
		  catch (Exception e) {
		  	System.out.println("Error reading file");
		  }

		  int x=0;
		  total_values = array.size();
		  while(x<total_values){
		 	
			try {
				JsonObject object = array.getJsonObject(x);
				String tagName = object.getString("TagName");
				String tagValue = object.getString("TagValue");
				
				Long timeStamp = Long.parseLong(object.getString("TimeStamp").substring(6,19));
				if (kafkaMessages.get(timeStamp) != null) { //timestamp already exists
					//addValue(kafkaMessages, tagName, tagValue, timeStamp);
					(kafkaMessages.get(timeStamp)).add(tagValue);
				}
				else{ //new timestamp
					kafkaMessages.put(timeStamp, new ArrayList<String>());
					//addValue(kafkaMessages, tagName, tagValue, timeStamp);
					(kafkaMessages.get(timeStamp)).add(timeStamp.toString());
					(kafkaMessages.get(timeStamp)).add(tagValue);
				}
	        }
	        catch (JSONException e) {
	            e.printStackTrace();
	        }
	        catch (Exception e){
	        	e.printStackTrace();
	        }  
			x++;
		  }
	  	return;
   }
}