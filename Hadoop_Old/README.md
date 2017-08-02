# Hadoop Consumer

This code consumes messages from the KafkaConsumer.java class. When it receives a message it will reformat the data (making the values tab-seperated) and then appends that data to the latest file in the data subdirectory. Once a day this will then post all files in the data subdirectory to Hadoop.

## Prerequisites

This code must be running on a flux account in order for it to actually work. To apply for a flux account go to the following link.

http://arc-ts.umich.edu/flux-user-guide/

Make sure you request Hadoop access when applying for an account

## Deployment

To start listening for messages run the following commands from the main Consumer directory:

```
java -jar ./build/libs/Consumer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Consumer.jar &
```