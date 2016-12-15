# Kafka Consumer

This code actually consumes messages from the Kafka server. If you want to run it locally, the servers should all be localhost (use the code from the master branch). If you want to run it on the testbed computer run the code from the production branch. The only differences are the settings for connecting to the servers.

## Deployment

To start listening for messages run the following commands from the main Consumer directory:

```
java -jar ./build/libs/Consumer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Consumer.jar &
```