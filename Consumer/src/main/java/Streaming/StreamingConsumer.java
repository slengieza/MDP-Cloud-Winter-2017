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
    
    private int numCycles = 3;
    private int numRFID = 4;

    private boolean ON = true;
    private boolean OFF = false;

    private Long[] cycleStartTimeStamp;
    private String[] cycleName;
    private int[] cycleIDs;
    private boolean[] cycleState;
    private boolean[] inTransit;
    private boolean[] rfidState;

    public StreamingConsumer(InfluxDB influxdb, String dbName, String measurementName) {
        this.influxDB = influxdb;
        this.logger = LoggerFactory.getLogger(StreamingConsumer.class.getName());
        this.measurementName = measurementName;
        this.batchPoints = BatchPoints
                    .database(dbName)
                    .retentionPolicy("default")
                    .consistency(ConsistencyLevel.ALL)
                    .build();

        init();
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
        rfidState[0] = Boolean.valueOf(parts[10]); //RFID 54
        rfidState[1] = Boolean.valueOf(parts[11]); //RFID 55
        rfidState[2] = Boolean.valueOf(parts[8]);  //RFID 56
        rfidState[3] = Boolean.valueOf(parts[9]);  //RFID 57

        for (int i = 0; i < numCycles; ++i) {
            monitorCycle(cycleIDs[i], timeStamp, rfidState[i], rfidState[i+1]);   
        }
    }

    public void monitorCycle(int cycleID, Long timeStamp, boolean rfid1, boolean rfid2){
        if (cycleState[cycleID] == OFF && rfid1 == true){ //pallet enters rfid 54
            cycleState[cycleID] = ON;
        }
        else if(!inTransit[cycleID] && cycleState[cycleID] == ON && rfid1 == false){ //pallet leaves rfid54 and is on its way to rfid55
            inTransit[cycleID] = true;
            cycleStartTimeStamp[cycleID] = timeStamp;
        }
        else if(inTransit[cycleID] == true && cycleState[cycleID] == ON && rfid2 == true){ // pallet enters rfid55 (end of cycle)
            inTransit[cycleID] = false;
            cycleState[cycleID] = OFF;
            Long cycleTime = timeStamp - cycleStartTimeStamp[cycleID]/1000; //end of RFID54 to beginning of RFID55
            Point point = Point.measurement(measurementName)
            .time(cycleStartTimeStamp[cycleID], TimeUnit.MILLISECONDS)
            .addField("cycle", cycleName[cycleID]) //TODO find name of cycle
            .addField("CylceTime", cycleTime)
            .build();
            this.batchPoints.point(point);
            influxDB.write(this.batchPoints);
        }
    }

    public void init(){
        cycleStartTimeStamp = new Long[numCycles];
        cycleName = new String[numCycles];
        cycleIDs = new int[numCycles];
        inTransit = new boolean[numCycles];
        cycleState = new boolean[numCycles];
        rfidState = new boolean[numRFID];

        for (int i = 0; i < numCycles; ++i) {
            cycleStartTimeStamp[i] = java.lang.Long.MIN_VALUE;
            cycleIDs[i] = i;
            cycleName[i] = "";
            inTransit[i] = false;
            cycleState[i] = OFF;
        }
    }
}
