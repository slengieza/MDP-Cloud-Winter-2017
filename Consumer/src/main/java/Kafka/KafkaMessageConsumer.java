package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;

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
    private String username = "cloud_data";
    private String password = "2016SummerProj";
    private String database = "https://migsae-influx.arc-ts.umich.edu:8086";
    private String dbName = "test";
    private String continuousDataTable = "OldValues";
    private String cycleTimeTable = "CycleTimes";

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

        InfluxClient influx_client = new InfluxClient(username, password, database, dbName, continuousDataTable);
        StreamingClient streaming_client = new StreamingClient(username, password, database, dbName, cycleTimeTable);

        this.listeners = new ArrayList<ConsumerListener>();
        this.listeners.add((ConsumerListener)influx_client.listener);
        this.listeners.add((ConsumerListener)streaming_client.listener);
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
        String groupId = "1";
        // List<String> topics = Arrays.asList("test_schema");
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

// public class KafkaMessageConsumer {

//     // private ConsumerConnector consumer;

//     private String topic;

//     private KafkaConsumer<String, String> consumer;
//     private ExecutorService executor;

//     private int threads;
//     private List<ConsumerListener> listeners;

//     public KafkaMessageConsumer(String zookeeper, String groupId, String topic, int threads, List<ConsumerListener> listeners) {
//         Properties props = new Properties();
//         props.put("zookeeper.connect", zookeeper);
//         props.put("group.id", groupId);
//         props.put("zookeeper.session.timeout.ms", "10000");//400
//         props.put("zookeeper.sync.time.ms", "10000");//200
//         props.put("auto.commit.interval.ms", "1000");
//         props.put("key.deserializer", StringDeserializer.class.getName());
//         props.put("value.deserializer", StringDeserializer.class.getName());
//         this.consumer = new KafkaConsumer<>(props);
//         this.topic = topic;
//         this.threads = threads;
//         this.listeners = listeners;
//     }

//     private class MessageConsumer implements Runnable {
//         private KafkaStreams stream;
//         private int threadNumber;
//         private List<ConsumerListener> listeners;

//         public MessageConsumer(KafkaStreams stream, int threadNumber, List<ConsumerListener> listeners) {
//             this.stream = stream;
//             this.threadNumber = threadNumber;
//             this.listeners = listeners;
//         }

//         @Override
//         public void run() {
//             try {
//                 consumer.subscribe(topics);

//                 while (true) {
//                     ConsumerRecords<String, String> records = consumer.poll(10000.0);
//                     for (ConsumerRecord<String, String> record : records) {
//                         System.out.println(this.id + ": " + record.value());
//                         for (ConsumerListener listener : this.listeners) {
//                             listener.onReceiveMessage(record.value());   
//                         }
//                     }
//                 }
//             } 
//             catch (WakeupException e) {
//               // ignore for shutdown 
//             } 
//             finally {
//                 consumer.close();
//             }
//         }

//         public void shutdown() {
//             consumer.wakeup();
//         }
//     }
//     public static void main(String[] args) {
//         String zookeeper = "migsae-kafka.aura.arc-ts.umich.edu:2181/kafka";
//         String groupId = "1";
//         String topic = "test2";
//         String username = "cloud_data";
//         String password = "2016SummerProj";
//         String database = "https://migsae-influx.arc-ts.umich.edu:8086";
//         String dbName = "test";
//         String continuousDataTable = "TrainingData_3_6_2017";
//         String cycleTimeTable = "CycleTimes";
//         int threads = 1;
    
//         InfluxClient influx_client = new InfluxClient(username, password, database, dbName, continuousDataTable);
//         StreamingClient streaming_client = new StreamingClient(username, password, database, dbName, cycleTimeTable);
//         // HadoopClient hadoop_client = new HadoopClient();

//         List<ConsumerListener> listeners = new ArrayList<ConsumerListener>();
//         listeners.add((ConsumerListener)influx_client.listener);
//         listeners.add((ConsumerListener)streaming_client.listener);
//         // listeners.add((ConsumerListener)hadoop_client.listener);

//         KafkaMessageConsumer consumer = new KafkaMessageConsumer(zookeeper, groupId, topic, threads, listeners);

//         int numConsumers = 1;
//         String groupId = "1"
//         List<String> topics = Arrays.asList("test1");
//         ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

//         final List<ConsumerLoop> consumers = new ArrayList<>();
//         for (int i = 0; i < numConsumers; i++) {
//             ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics);
//             consumers.add(consumer);
//             executor.submit(consumer);
//         }



//         Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//         topicCountMap.put(topic, new Integer(this.threads));
//         Map<String, List<KafkaStreams<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
//         List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

//         // launch all the threads
//         executor = Executors.newFixedThreadPool(this.threads);

//         // create an object to consume the messages
//         int threadNumber = 0;
//         System.out.println(streams.size());
//         for (final KafkaStream stream : streams) {
//             executor.submit(new MessageConsumer(stream, threadNumber, this.listeners));
//             threadNumber++;
//         }

//         Runtime.getRuntime().addShutdownHook(new Thread() {
//             @Override
//             public void run() {
//                 for (ConsumerLoop consumer : consumers) {
//                     consumer.shutdown();
//                 } 
//                 executor.shutdown();
//                 try {
//                     executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//         });
//     }

//     public void stop(){
//         for (ConsumerListener listener : this.listeners) {
//             listener.onShutdown();   
//         }
//         this.shutdown();
//     }
// }