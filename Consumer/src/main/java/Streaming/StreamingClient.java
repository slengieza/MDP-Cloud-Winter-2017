package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

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

import com.mdp.consumer.KafkaMessageConsumer;
import com.mdp.consumer.ConsumerListener;
import com.mdp.consumer.StreamingConsumer;

public class StreamingClient{

    private String dbName;
		private String series;
    private InfluxDB influxDB;
    ConsumerListener listener;

    public StreamingClient(InfluxDB influxIn, String dbNameIn, String seriesIn){
    		this.influxDB = influxIn;
				this.dbName = dbNameIn;
				this.series = seriesIn;
				this.listener = new StreamingConsumer(this.influxDB, this.dbName, this.series);
    }
}
