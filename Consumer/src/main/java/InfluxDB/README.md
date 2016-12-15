# InfluxDB Consumer

This code consumes messages from the KafkaConsumer.java class. When it receives a message it will post the data directly into InfluxDB.

## Deployment

To start listening for messages run the following commands from the main Consumer directory:

```
java -jar ./build/libs/Consumer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Consumer.jar &
```