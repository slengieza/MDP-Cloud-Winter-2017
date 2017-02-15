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

import com.mdp.consumer.KafkaConsumer;
import com.mdp.consumer.ConsumerListener;

public class InfluxConsumer implements ConsumerListener {

    private InfluxDB influxDB;
    private BatchPoints batchPoints;
    private String measurementName;
    private Logger logger;
  
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
        if (parts[0].equals("TestBed")) {
            addTestBedData(parts);
        }
        else if(parts[0].equals("Simulation")){
            addSimulationData(parts);
        }
    }

    public void addTestBedData(String[] parts){
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
        boolean rfid1 = Boolean.valueOf(parts[12]);
        boolean rfid2 = Boolean.valueOf(parts[13]);
        boolean rfid3 = Boolean.valueOf(parts[14]);
        boolean rfid4 = Boolean.valueOf(parts[15]);
        boolean rfid5 = Boolean.valueOf(parts[16]);
        boolean rfid6 = Boolean.valueOf(parts[17]);

        Point point1=Point.measurement(measurementName)
        .time(timeStamp, TimeUnit.MILLISECONDS)
        .addField("fanucFreq", fanucFreq)
        .addField("fanucCurrent", fanucCurrent)
        .addField("fanucVoltage", fanucVoltage)
        .addField("abbFreq", abbFreq)
        .addField("abbCurrent", abbCurrent)
        .addField("abbVoltage", abbVoltage)
        .addField("RFID1", rfid1)
        .addField("RFID2", rfid2)
        .addField("RFID3", rfid3)
        .addField("RFID4", rfid4)
        .addField("RFID5", rfid5)
        .addField("RFID6", rfid6)
        .build();
        this.batchPoints.point(point1);
        logger.error(format.format(new Date(timeStamp)) + " Received TestBed Message: " +
                                    "TimeStamp: " + parts[1] + " fanucFreq: " + parts[2] +
                                    " fanucCurrent: " + parts[3] + " fanucVoltage: " + parts[4] + 
                                    " abbFreq: " + parts[5] + " abbCurrent: " + parts[6] + " abbVoltage: " + parts[7] +
                                    " RFID54: " + parts[10] + " RFID55: " + parts[11] + " RFID56: " + parts[8] + " RFID57: " + parts[9]);
        influxDB.write(this.batchPoints);
    }

    public void addSimulationData(String[] parts){

    }
}
