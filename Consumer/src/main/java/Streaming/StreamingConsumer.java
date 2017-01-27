package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

import com.mdp.consumer.KafkaConsumer;
import com.mdp.consumer.ConsumerListener;

public class StreamingConsumer implements ConsumerListener {

    private InfluxDB influxDB;
    private BatchPoints batchPoints;
    private String measurementName;
    private Logger logger;
    private boolean ON = true;
    private boolean OFF = false;

    private Long cycleStartTimeStamp;
    private boolean cycleState;
    private boolean inTransit;

    public StreamingConsumer(InfluxDB influxdb, String dbName, String measurementName) {
        this.influxDB = influxdb;
        this.logger = LoggerFactory.getLogger(StreamingConsumer.class.getName());
        this.measurementName = measurementName;
        this.batchPoints = BatchPoints
                    .database(dbName)
                    .retentionPolicy("default")
                    .consistency(ConsistencyLevel.ALL)
                    .build();

        inTransit = false;
        cycleState = OFF;
    }

    public void onShutdown(){
        System.out.println("Shutting down");
    }

    //TODO add logging file to show each message getting received
    public void onReceiveMessage(String message){
        System.out.println("Received message for StreamingConsumer " + message);

        String parts[] = message.split("\t");
        String typeOfData = parts[0];
        Long timeStamp= Long.parseLong(parts[1]);
        Long fanucFreq = Long.parseLong(parts[2]);
        Long fanucCurrent = Long.parseLong(parts[3]);
        Long fanucVoltage = Long.parseLong(parts[4]);
        Long abbFreq = Long.parseLong(parts[5]);
        Long abbCurrent = Long.parseLong(parts[6]);
        Long abbVoltage = Long.parseLong(parts[7]);
        boolean rfid56 = stringToBoolean(parts[8]);
        boolean rfid57 = stringToBoolean(parts[9]);
        boolean rfid54 = stringToBoolean(parts[10]);
        boolean rfid55 = stringToBoolean(parts[11]);

        monitorCycle(timeStamp, rfid54, rfid55);

    }

    public void monitorCycle(Long timeStamp, boolean rfid54, boolean rfid55){
        if (cycleState == OFF && rfid54 == true){ //pallet enters rfid 54
            cycleState = ON;
        }
        else if(!inTransit && cycleState == ON && rfid54 == false){ //pallet leaves rfid54 and is on its way to rfid55
            inTransit = true;
            cycleStartTimeStamp = timeStamp;
        }
        else if(inTransit == true && cycleState == ON && rfid55 == true){ // pallet enters rfid55 (end of cycle)
            inTransit = false;
            cycleState = OFF;
            Long cycleTime = timeStamp - cycleStartTimeStamp/1000; //end of RFID54 to beginning of RFID55
            Point point = Point.measurement(measurementName)
            .time(cycleStartTimeStamp, TimeUnit.MILLISECONDS)
            .addField("cycle", "RFID54 to RFID55") //TODO find name of cycle
            .addField("CylceTime", cycleTime)
            .build();
            this.batchPoints.point(point);
            influxDB.write(this.batchPoints);
        }
    }

    public boolean stringToBoolean(String in){
        if (in == "True") {
            return true;
        }
        else {
            return false;
        }
    }
}
