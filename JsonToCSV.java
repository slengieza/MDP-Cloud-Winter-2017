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

	public static void main(String[] args){
        //LOCAL
        Path dir = Paths.get("/Users/drewramacher/Desktop/MDPdata"); //path to .dat files
        //send files that are already there
        File folder = new File(dir.toString()); 
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.toString().toLowerCase().endsWith(".dat")){
                getCSV(file);
            }
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
			total_values = jsonReader.readArray().size();
			//array = new JSONArray(jsonReader.readArray());
			array = jsonReader.readArray();
			jsonReader.close();
			fis.close();
		  } 
		  catch (Exception e) {}

		// This happens when program fails to open file
		if (array == null) return;
		System.out.println("hello");

		// Get data from JSON array
		  HashMap<Long, List<String>> kafkaMessages = new HashMap<Long, List<String>>();
		  int x=0;
		  while(x<total_values){
		 	
			try {
	            //JSONObject object = array.getJSONObject(x);
	            JsonObject object = array.getJsonObject(x);

	            //reference http://stackoverflow.com/questions/7172158/converting-json-to-xls-csv-in-java
	            //JSONArray docs = object.getJSONArray("infile"); //TODO convert JSONObject to JSONArray
	            //JsonArray docs = object.getJsonArray("infile");

	            File file=new File("/Users/drewramacher/Desktop/csvStuff"); //TODO put file location here
	            //String csv = CDL.toString(JSONArray(docs));
	            String csv = object.getString("msg");
	            FileUtils.writeStringToFile(file, csv);
	        }
	        catch (JSONException e) {
	            e.printStackTrace();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }  
			x++;
		  }
	  	return;
   }
}