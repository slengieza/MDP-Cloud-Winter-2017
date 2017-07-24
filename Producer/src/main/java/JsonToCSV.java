package com.mdp.producer;

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

        //PRODUCTION
        String path = "C:\\Rockwell Automation\\WorkingDirectory";
        Path dir = Paths.get(path.replace("\\", "/"));
        //send files that are already there
        File folder = new File(dir.toString());
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.toString().toLowerCase().endsWith(".dat")){
            	System.out.println("Converting file " + file.toString());
                converter.getCSV(file);
            }
        }
        try{
        	String path1 = "C:\\Rockwell Automation\\WorkingDirectory\\SimulationData\\test.csv";
        	path1 = path1.replace("\\", "/");
        	System.out.println(path1);
		    PrintWriter writer = new PrintWriter(path1, "UTF-8");
		    //PrintWriter writer = new PrintWriter(file, "UTF-8");
		    System.out.println("Writing headers to file");
		    //writer.println("TimeStamp,  Fanuc1, Fanuc2, Fanuc3, ABB1, ABB2, ABB3, RFID56, RFID57, RFID54, RFID55, RFID1, RFID2, RFID3, RFID4, RFID5, RFID6");
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
				tagName = tagName.replace("::[New_Shortcut]", "");
				String tagValue = object.getString("TagValue");

				Long timeStamp = Long.parseLong(object.getString("TimeStamp").substring(6,19));
				if (kafkaMessages.get(timeStamp) != null) { //timestamp already exists
					// Added for automatic Data writing
					(kafkaMessages.get(timeStamp)).add(tagName);
					(kafkaMessages.get(timeStamp)).add(tagValue);
				}
				else{ //new timestamp
					kafkaMessages.put(timeStamp, new ArrayList<String>());
					(kafkaMessages.get(timeStamp)).add("TimeStamp");
					(kafkaMessages.get(timeStamp)).add(timeStamp.toString());
					// Added for automatic Data writing
					(kafkaMessages.get(timeStamp)).add(tagName);
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
