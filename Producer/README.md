# Producers

This code listens for new files in a specified directory and sends messages to the Kafka server.

## Prerequisites

If you are running the code locally you must start zookeeper and Kafka server before listening for messages.You can go to https://kafka.apache.org/082/documentation.html and download Kafka. Then run the following code from that main directory to start the servers.

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

Run the following commands from the main Producer directory

To build:

```
gradle build
```

## Deployment

To start listening for new files:

```
java -jar ./build/libs/Producer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Producer.jar &
```