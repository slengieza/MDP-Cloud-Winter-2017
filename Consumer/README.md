# Consumers

This code is what consumes Kafka messages. The KafkaConsumer (in the Kafka directory) actually consumes the messages and then sends them to both Hadoop and InfluxDB consumers. 

## Prerequisites

If you are running the code locally you must start zookeeper and Kafka server before listening for messages.You can go to https://kafka.apache.org/082/documentation.html and download Kafka. Then run the following code from that main directory to start the servers. If you are running this code from the computer in the IMSL, your working directory you call this from should be:
```
C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka
```
```
./bin/zookeeper-server-start.sh ./config/zookeeper.properties &
./bin/kafka-server-start.sh ./config/server.properties &
```

To stop the servers use the following commands:

```
./bin/zookeeper-server-stop.sh
./bin/kafka-server-stop.sh
```

## Building

Run the following commands from the main Consumer directory

To build:

```
gradle build
```

## Deployment

To start listening for messages:

```
java -jar ./build/libs/Consumer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Consumer.jar &
```
