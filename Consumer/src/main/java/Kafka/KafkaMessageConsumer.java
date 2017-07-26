package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.mdp.consumer.ConsumerListener;
import com.mdp.consumer.InfluxClient;
import com.mdp.consumer.StreamingClient;
import com.mdp.consumer.HadoopClient;



public class KafkaMessageConsumer implements Runnable{
    private KafkaConsumer<String, String> consumer;
    private List<ConsumerListener> listeners;
    private int id;
    private String zookeeper = "migsae-kafka.aura.arc-ts.umich.edu:2181/kafka";
    private String groupId = "1";
    private List<String> topics = Arrays.asList("test1");
    private String username = "hkardos";
    private String password = "Migffn##567";
    private String database = "https://migsae-influx.arc-ts.umich.edu:8086";
    //private String database = "https://localhost:8086";
    private String dbName = "test";
    private String series;
    private InfluxDB influxDB;

    public KafkaMessageConsumer(int id){
        this.id = id;
        Properties props = new Properties();
        props.put("zookeeper.connect", this.zookeeper);
        props.put("group.id", this.groupId);
        props.put("zookeeper.session.timeout.ms", "400");//400
        props.put("metadata.broker.list", "migsae-kafka.aura.arc-ts.umich.edu:9092");
        props.put("bootstrap.servers", "migsae-kafka.aura.arc-ts.umich.edu:9092");
        props.put("zookeeper.sync.time.ms", "200");//200
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
        this.influxDB = InfluxDBFactory.connect(database, username, password);
        seriesSelect();
        InfluxClient influx_client = new InfluxClient(this.influxDB, this.dbName, this.series);
        StreamingClient streaming_client = new StreamingClient(this.influxDB, this.dbName, this.series);
        this.listeners = new ArrayList<ConsumerListener>();
        this.listeners.add((ConsumerListener)influx_client.listener);
        this.listeners.add((ConsumerListener)streaming_client.listener);
    }


    private void seriesSelect(){
      Scanner scans = new Scanner(System.in);
      System.out.println("Current Series :");
      Query seriesQuery = new Query("SHOW SERIES", "test");
      QueryResult seriesResult = this.influxDB.query(seriesQuery);
      List<List<Object>> values = seriesResult.getResults().get(0).getSeries().get(0).getValues();
      for (Object value : values) {
          System.out.println(value.toString());
      }
      System.out.println("--------------------------------------------------");
      System.out.print("Please enter the name of which series you'd like to use : ");
      String seriesNameIn = scans.nextLine();
      // Replace any Quotation Marks and Single Quotes
      seriesNameIn = seriesNameIn.replace("\"", "").replace("\'", "");
      this.series = seriesNameIn;
    }

    @Override
    public void run() {
        System.out.println("run");
        try {
            consumer.subscribe(this.topics);
            System.out.println("subscribed to topic " + this.topics.toString());
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    //System.out.println(record);
                    System.out.println("Got Message: " + record.value());
                    for (ConsumerListener listener : this.listeners) {
                        listener.onReceiveMessage(record.value());
                    }
                }
            }
        }
        catch (WakeupException e) {
          // ignore for shutdown
            System.out.println("WakeupException");
            e.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Exception");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }

    public static void main(String[] args) {
        int numConsumers = 1;

        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
        final List<KafkaMessageConsumer> consumers = new ArrayList<>();

        for (int i = 0; i < numConsumers; i++) {
            System.out.println("Adding consumer");
            KafkaMessageConsumer consumer = new KafkaMessageConsumer(i);
            consumers.add(consumer);
            System.out.println("Submitting consumer");
            executor.submit(consumer);
            System.out.println("After Submitting consumer");
        }

    }//main
}
