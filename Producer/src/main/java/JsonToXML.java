package com.mdp.producer;

//takes JSON data from input stream and turns data XML for mtconnect

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsonToXML {

	
	public static void GetMTCXML(File jsonfile) {
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
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("MTConnectStreams");
			doc.appendChild(rootElement);
			
			Element streams = doc.createElement("Streams");
			rootElement.appendChild(streams);
			
			//FANUC loop setup
			Element deviceStream = doc.createElement("DeviceStream");
			streams.appendChild(deviceStream);
			deviceStream.setAttribute("name", "FANUC");
			
			Element samples = doc.createElement("Samples");
			deviceStream.appendChild(samples);
			
			//Element current = doc.createElement("Current");
			//Element freq = doc.createElement("Freq");
			//Element volt = doc.createElement("Volt");
			//samples.appendChild(current);
			//samples.appendChild(freq);
			//samples.appendChild(volt);
			
			//ABB loop setup
			Element deviceStream2 = doc.createElement("DeviceStream");
			streams.appendChild(deviceStream2);
			deviceStream2.setAttribute("name", "ABB");
			
			Element samples2 = doc.createElement("Samples");
			deviceStream2.appendChild(samples2);
			
			//Element current2 = doc.createElement("Current");
			//Element freq2 = doc.createElement("Freq");
			//Element volt2 = doc.createElement("Volt");
			//samples2.appendChild(current2);
			//samples2.appendChild(freq2);
			//samples2.appendChild(volt2);
			

		} 
		catch (Exception e) {}

		// This happens when program fails to open file
		if (array == null) return null;

		// Get data from JSON array
		int total_values= 6;
		//change to total after bug is fixed
		//HashMap<Long, List<String>> kafkaMessages = new HashMap<Long, List<String>>();
		int x=0;
		Long timeStamp = 0;
		while(x<total_values){
		 	JsonObject object = array.getJsonObject(x);
			String tagName = object.getString("TagName");
			String tagValue = object.getString("TagValue");
			switch (x) {
				case 0:  Element freq = doc.createElement("Freq");
						 freq.setAttribute("is",tagName);
						 freq.setAttribute("type","Frequency");
						 freq.appendChild(doc.createTextNode(tagValue));
						 samples.appendChild(freq);
						 break;
				case 1:  Element current = doc.createElement("Current");
						 current.setAttribute("is",tagName);
						 current.setAttribute("type","Amperage");
						 current.appendChild(doc.createTextNode(tagValue));
						 samples.appendChild(current);
						 break;
				case 2:  Element volt = doc.createElement("Voltage");
						 volt.setAttribute("is",tagName);
						 volt.setAttribute("type","Voltage");
						 volt.appendChild(doc.createTextNode(tagValue));
						 samples.appendChild(volt);
						 break;
				case 3:  Element freq2 = doc.createElement("Freq");
						 freq2.setAttribute("is",tagName);
						 freq2.setAttribute("type","Frequency");
						 freq2.appendChild(doc.createTextNode(tagValue));
						 samples2.appendChild(freq2);
						 break;
				case 4:  Element current2 = doc.createElement("Current");
						 current2.setAttribute("is",tagName);
						 current2.setAttribute("type","Amperage");
						 current2.appendChild(doc.createTextNode(tagValue));
						 samples2.appendChild(current2);
						 break;
				case 5:  Element volt2 = doc.createElement("Voltage");
						 volt2.setAttribute("is",tagName);
						 volt2.setAttribute("type","Voltage");
						 volt2.appendChild(doc.createTextNode(tagValue));
						 samples2.appendChild(volt2);
						 break;
				default: Element a = doc.createElement("Other");
						 a.setAttribute("is",tagName);
						 freq.appendChild(doc.createTextNode(tagValue));
						 samples.appendChild(freq);
						 
			}
			timeStamp = Long.parseLong(object.getString("TimeStamp").substring(6,19));
			x++;
		}
		streams.setAttribute("TimeStamp",timeStamp);
		
		//output file to place
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		//change place
		StreamResult result = new StreamResult(new File("C:\Rockwell Automation\WorkingDirectory\MTConnect\devices.xml"));
   }
}
