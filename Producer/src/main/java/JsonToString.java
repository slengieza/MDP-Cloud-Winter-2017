package com.mdp.producer;

//takes JSON data from input stream and turns data into a string for kafka producer

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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;


public class JsonToString {

	/* Kafka message format 

	Hashmap of timestamps where each timestamp has a list of values as follows

	[
		timeStamp,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputFreq,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputCurrent,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputVoltage,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputFreq,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputCurrent,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputVoltage
	]

	*/
	public static HashMap<Long, List<String>> GetKafkaMessage(File jsonfile) {
		// Initialize this array to check later
		JsonArray array = null;

		int numDataPoints = 6; //TODO must change when we add more data (its number of data points * 2 because we have one for each loop)

		// Use try-catch to avoid RunTimeError
		// when the file doesn't exist
		// or somehow the program cannot open the file
		  try {
		   	InputStream fis = new FileInputStream(jsonfile);
			JsonReader jsonReader = Json.createReader(fis);
			array = jsonReader.readArray();
			jsonReader.close();
			fis.close();
		  } 
		  catch (Exception e) {}

		// This happens when program fails to open file
		  if (array == null) return null;

		// Get data from JSON array
		  int total_values= array.size();
		  HashMap<Long, List<String>> kafkaMessages = new HashMap<Long, List<String>>();
		  int x=0;
		  while(x<total_values){
		 	JsonObject object = array.getJsonObject(x);
			String tagName = object.getString("TagName");
			String tagValue = object.getString("TagValue");
			
			Long timeStamp = Long.parseLong(object.getString("TimeStamp").substring(6,19));
			//Long time = Long.parseLong(timestamp.substring(6,19));
			if (kafkaMessages.get(timeStamp) != null) { //timestamp already exists
				//addValue(kafkaMessages, tagName, tagValue, timeStamp);
				(kafkaMessages.get(timeStamp)).add(tagValue);
			}
			else if (kafkaMessages.get(tagName) != null){ //tag already exists
				continue;
			}
			else{ //new timestamp
				kafkaMessages.put(timeStamp, new ArrayList<String>());
				//addValue(kafkaMessages, tagName, tagValue, timeStamp);
				(kafkaMessages.get(timeStamp)).add(timeStamp.toString());
				(kafkaMessages.get(timeStamp)).add(tagValue);
			}
			x++;
		  }

	  	return kafkaMessages;
   }
}