package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import com.mdp.consumer.KafkaMessageConsumer;
import com.mdp.consumer.ConsumerListener;

public class InfluxConsumer implements ConsumerListener {

	private InfluxDB influxDB;
	private BatchPoints batchPoints;
	private String series;
	private Logger logger;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * Establishes our InfluxConsumer. Called From InfluxClient.java From KafkaMessageConsumer.java
	 *
	 * @param influxdb
	 *							the influxDB object that we created in our Kafka Message ConsumerListener
	 *
	 * @param dbName
	 *							Refers to the specific database we are going to use. Hardcoded to
	 *							test, but could also be 'Production'
	 * @param seriesIn
	 *							The identifying tag for which experiment we're running(i.e. 'Experiment1');
	 *							Will most likely be removed if used for production
	 **/

	public InfluxConsumer(InfluxDB influxdb, String dbName, String seriesIn) {
		this.influxDB = influxdb;
		this.logger = LoggerFactory.getLogger(InfluxConsumer.class.getName());
		this.series = seriesIn;
		this.batchPoints = BatchPoints
					.database(dbName)
					.retentionPolicy("autogen")
					.consistency(ConsistencyLevel.ALL)
					.build();
	}
	/**
	 * Nice little shutdown message
	 */
	public void onShutdown(){
		System.out.println("Shutting down");
	}

	//TODO add logging file to show each message getting received
	/**
	 * Gets and splits the message from our producer. Called from run() in KafkaMessageConsumer.java
	 *
	 * @param message
	 *							A full string received from our producer. Constitutes either one
	 *							Or many parts
	 **/
	public void onReceiveMessage(String message){
		String parts[] = message.split("\t");
		if (parts[0].equals("TestBed")) {
			addTestBedData(parts);
		}
		// Simulation data has not been figured out yet, but will likely be similarto TestBed data
		else if(parts[0].equals("Simulation")){
			addSimulationData(parts);
		}
	}

	/**
	 * Breaks the test bed data into parts, each with their own unique timestamp
	 *
	 * @param parts
	 * 					the full message received; parts is likely more than one full point;
	 *					parts is in the form of [type, key, value, key, value, ...]
	 **/

	public void addTestBedData(String[] parts){
		int i = 1;
		while(i < parts.length){
			if(parts[i].equals("TimeStamp")){ /* If parts[i] equals TimeStamp, this
																				indicates the beginning of a new point*/
				ArrayList<String> stringSend = new ArrayList<String>();
				stringSend.add(parts[i]);
				i++;
				// While we're still reading from the same point; this can be a variable amount
				while((i < parts.length) && !(parts[i].equals("TimeStamp"))){
					stringSend.add(parts[i]);
					i++;
				}
				sendTestBedData(stringSend);
			}
			i++;
		}
	}

	/**
	 * Takes split up parts of message and sends them to InfluxDB
	 *
	 * @param parts
	 * 					the full message received; parts represents one full data collection point
	 **/

	public void sendTestBedData(ArrayList<String> parts){
			Long timeStamp = Long.parseLong(parts.get(1)); // Our TimeStamp value
			Map<String, Object> fields_list = new HashMap<String, Object>(); // stores all (key, value) pairs
			int i = 2; // Don't need to put "TimeStamp" key (parts[0]) or value (parts[1])
								 // into our fields list; InfluxDB has a defined field for time values
			while(i < parts.size() - 1){
				fields_list.put(parts.get(i), Double.parseDouble(parts.get(i + 1)));
				i += 2; // Advance one (key, value) pair
			}
			Point fullPoint = Point.measurement(this.series)
					.time(timeStamp, TimeUnit.MILLISECONDS)
					.fields(fields_list)
					.build();
			this.batchPoints.point(fullPoint);
			influxDB.write(this.batchPoints);
			System.out.println("Message with timestamp : " + parts.get(1) + " Sent to InfluxDB\n"); // Status update
	}
	/**
	 * TODO: Write this function when we're sure how we're going to do Simulation data
	 **/
	public void addSimulationData(String[] parts){

	}
}
