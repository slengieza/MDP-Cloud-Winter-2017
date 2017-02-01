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

public class InfluxConsumer implements ConsumerListener {

    private InfluxDB influxDB;
    private BatchPoints batchPoints;
    private String measurementName;
    private Logger logger;
    private Long cycleStartTimeStamp;
    private boolean cycleState;
    private boolean ON = true;
    private boolean OFF = false;

    public InfluxConsumer(InfluxDB influxdb, String dbName, String measurementName) {
        this.influxDB = influxdb;
        this.logger = LoggerFactory.getLogger(InfluxConsumer.class.getName());
        this.measurementName = measurementName;
        this.batchPoints = BatchPoints
                    .database(dbName)
                    .retentionPolicy("default")
                    .consistency(ConsistencyLevel.ALL)
                    .build();
    }

    public void onShutdown(){
        System.out.println("Shutting down");
    }

    //TODO add logging file to show each message getting received
    public void onReceiveMessage(String message){
        String parts[] = message.split("\t");
        System.out.println("Received message for InfluxConsumer " + message);
        addTestBedData(parts);
        // if (parts[0] == "TestBed") {
        //     addTestBedData(parts);
        // }
        // else if(parts[0] == "Simulation"){
        //     addSimulationData(parts);
        // }
    }

    public void addTestBedData(String[] parts){
        System.out.println("Adding TestBed data");
        Long timeStamp= Long.parseLong(parts[1]);
        Long fanucFreq = Long.parseLong(parts[2]);
        Long fanucCurrent = Long.parseLong(parts[3]);
        Long fanucVoltage = Long.parseLong(parts[4]);
        Long abbFreq = Long.parseLong(parts[5]);
        Long abbCurrent = Long.parseLong(parts[6]);
        Long abbVoltage = Long.parseLong(parts[7]);
        boolean rfid56 = Boolean.valueOf(parts[8]);
        boolean rfid57 = Boolean.valueOf(parts[9]);
        boolean rfid54 = Boolean.valueOf(parts[10]);
        boolean rfid55 = Boolean.valueOf(parts[11]);

        Point point1=Point.measurement(measurementName)
        .time(timeStamp, TimeUnit.MILLISECONDS)
        .addField("timeStamp", parts[1])
        .addField("tagName", parts[2])
        .addField("tagValue", Integer.parseInt(parts[2]))
        .addField("fanucFreq", fanucFreq)
        .addField("fanucCurrent", fanucCurrent)
        .addField("fanucVoltage", fanucVoltage)
        .addField("abbFreq", abbFreq)
        .addField("abbCurrent", abbCurrent)
        .addField("abbVoltage", abbVoltage)
        .addField("RFID54", rfid54)
        .addField("RFID55", rfid55)
        .addField("RFID56", rfid56)
        .addField("RFID57", rfid57)
        .build();
        this.batchPoints.point(point1);
        logger.info("Received Message " + parts.toString());
        influxDB.write(this.batchPoints);
        System.out.println("Wrote TestBed data");
    }

    public void addSimulationData(String[] parts){

    }
}
