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

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONException;


public class JsonToCSV {

	public static void getCSV(File jsonfile) {
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
		 	
			try {
	            JsonObject object = array.getJsonObject(x);

	            //reference http://stackoverflow.com/questions/7172158/converting-json-to-xls-csv-in-java
	            JSONArray docs = object.getJSONArray("infile"); //TODO convert JSONObject to JSONArray

	            File file=new File(""); //TODO put file location here
	            String csv = CDL.toString(docs);
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