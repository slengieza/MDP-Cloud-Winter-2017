package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

import java.util.*;

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
        System.out.println("Received message for InfluxConsumer " + message);

        String parts[] = message.split("\t");
        System.out.println("Parts " + parts);
        Long timeStamp= Long.parseLong(parts[0]);
        Long fanucFreq = Long.parseLong(parts[1]);
        Long fanucCurrent = Long.parseLong(parts[2]);
        Long fanucVoltage = Long.parseLong(parts[3]);
        Long abbFreq = Long.parseLong(parts[4]);
        Long abbCurrent = Long.parseLong(parts[5]);
        Long abbVoltage = Long.parseLong(parts[6]);

        Point point1=Point.measurement(measurementName)
        .time(timeStamp, TimeUnit.MILLISECONDS)
        .addField("FanucFrequency", fanucFreq)
        .addField("FanucCurrent", fanucCurrent)
        .addField("FanucVoltage", fanucVoltage)
        .addField("ABBFrequency", abbFreq)
        .addField("ABBCurrent", abbCurrent)
        .addField("ABBVoltage", abbVoltage)
        .build();
        this.batchPoints.point(point1);
        logger.info("Received Message " + message);
        influxDB.write(this.batchPoints);
        System.out.println("Received Message " + message);
    }
}
