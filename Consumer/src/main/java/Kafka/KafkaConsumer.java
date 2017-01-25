package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.List;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.ConsumerIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mdp.consumer.ConsumerListener;
import com.mdp.consumer.InfluxClient;
import com.mdp.consumer.HadoopClient;

/***
    To use the consumer:
        - Create an object of this class, pass the right config through parameters
            + zookeeper address (usually is localhost:2181)
            + groupId (can be anything, but different programs must use different id)
            + topic name (pretty obvious, name of the Kafka topic to consume)
            + number of topic partitions (all of them is 1 for now, when you are
                        Kafka-fluent enough to add more partitions, you'll know what to do)
            + a ConsumerListener, ok this is the shit, you tell it what to do when the
                consumer receives messages, also what to do when the consumer is shutdown.
                PLEASE, look at the ConsumerListener interface for more info
        - Call .run()
        - The consumer will run forever until you call .shutdown()
***/
public class KafkaConsumer {
	/***
		The consumer?
	***/
	private ConsumerConnector consumer;
	/***
		The topic name of the topic we want to consume from
			Refer to Kafka's topics for more info
	***/
    private String topic;
    /***
    	An executor service manages a multi-threaded program, which is
    		*supposedly* this one, but let's not get too far here, our
    		Kafka stream only has 1 partition so we only need 1 thread
    	This thing is here just for future use, when we actually have
    		a multi-partition Kafka stream
    ***/
    private ExecutorService executor;
    /***
		Consumers can be multi-threaded
		The number of threads should match the number of partitions in the
			Kafka stream, but because we *probably* have only 1 partition
			on the stream now, so it's better off to pass thread as 1
    ***/
    private int threads;
    private List<ConsumerListener> listeners;

	public KafkaConsumer(String zookeeper, String groupId, String topic, int threads, List<ConsumerListener> listeners) {
		this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConfig(zookeeper, groupId));
		this.topic = topic;
		this.threads = threads;
        this.listeners = listeners;
	}

    /*public KafkaConsumer(ConsumerConfig consumerConfig,
                String topic, int threads, ConsumerListener listener) {
        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                consumerConfig);
        this.topic = topic;
        this.threads = threads;
        this.listener = listener;
    }*/

	public void run() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(this.threads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        // launch all the threads
        executor = Executors.newFixedThreadPool(this.threads);

        // create an object to consume the messages
        int threadNumber = 0;
        System.out.println(streams.size());
        for (final KafkaStream stream : streams) {
            executor.submit(new MessageConsumer(stream, threadNumber, this.listeners));
            threadNumber++;
        }
    }

	public void shutdown() {
		if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
	}

	public static ConsumerConfig createConfig(String zookeeper, String groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
        /***
            These values are default, don't change unless you know
                what you're doing with these parameters
            - Info: http://kafka.apache.org/08/documentation#configuration
            If you do know what you're doing, change the parameters for this
                function, and use the KafkaConsumer constructor with
                a Config parameter, it'll be better than passing tons of
                parameters to the Consumer constructor
        ***/
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
	}

	/***
		This is the 'job' that will be submitted to the
			Executor, which manages the threads of this program,
			and the Executor will run it
	***/
	private class MessageConsumer implements Runnable {
		private KafkaStream stream;
		private int threadNumber;
        private List<ConsumerListener> listeners;

		public MessageConsumer(KafkaStream stream, int threadNumber, List<ConsumerListener> listeners) {
	        this.stream = stream;
	        this.threadNumber = threadNumber;
            this.listeners = listeners;
	    }

	    public void run() {
	    	ConsumerIterator<String, String> it = this.stream.iterator();

            // Todo still now sure how to handle interruption
	        while (!Thread.currentThread().isInterrupted()) {
	        	if (it.hasNext()) {
                    String message = new String(it.next().message());
                    // This println is here for debugging purpose, feel free to comment out
	            	//System.out.println("Thread " + this.threadNumber + ": " + message);
                    System.out.println("KafkaConsumer received " + message);
                    for (ConsumerListener listener : this.listeners) {
                        listener.onReceiveMessage(message);   
                    }
                }
	        }
            for (ConsumerListener listener : this.listeners) {
                listener.onShutdown();   
            }
	    }
	}

	public static void main(String[] args) {
        //PRODUCTION
        String zookeeper = "migsae-kafka.aura.arc-ts.umich.edu:2181/kafka";

        //LOCAL
		// String zookeeper = "localhost:2181";
        String groupId = "1";
        String topic = "test1";
        String username = "cloud_data";
        String password = "2016SummerProj";
        String database = "https://migsae-influx.arc-ts.umich.edu:8086";
        String dbName = "test";
        String table = "NewValues";
        int threads = 1;
    
        InfluxClient influx_client = new InfluxClient(username, password, database, dbName, table);
        HadoopClient hadoop_client = new HadoopClient();

        List<ConsumerListener> listeners = new ArrayList<ConsumerListener>();
        listeners.add((ConsumerListener)influx_client.listener);
        listeners.add((ConsumerListener)hadoop_client.listener);

        KafkaConsumer consumer = new KafkaConsumer(zookeeper, groupId, topic, threads, listeners);
        consumer.run();
    }
}