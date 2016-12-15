# MDP-Cloud-Fall-2016

There are two main portions to this code base. The first is the Consumer and the second is the Producer. Each is broken into their own subdirectory.

## Prerequisites

Before starting, you must download and install the following software.

1. Kafka (This is found at https://kafka.apache.org/082/documentation.html)
2. Flux (This is found at http://arc-ts.umich.edu/flux-user-guide/)
	* You must apply for this account and request Hadoop access
3. Grafana (This is found at http://grafana.org/download/)
	* There are dashboards located in the Grafana subdirectory
4. You must install gradle on your flux account to run the Hadoop code. Use the following commands

```
curl -s https://get.sdkman.io | bash
source "/home/lengieza/.sdkman/bin/sdkman-init.sh"
sdk install gradle 3.2.1
```

## Building

To build the Consumer and Producer run the following command from the Consumer and Producer subdirectories respectively.

```
gradle build
```

## Deployment

#### Consumer
To start listening for messages:

```
java -jar ./build/libs/Consumer.jar 
```

If you want to be able to listen in the background use:

```
java -jar ./build/libs/Consumer.jar &
```

#### Producer
To build and run the consumer use the following commands from the Producer subdirectory.

To build:

```
gradle build
```

To start listening for new files:

```
java -jar ./build/libs/Producer.jar 
```

If you want to be able to run in the background use:

```
java -jar ./build/libs/Producer.jar &
```

## Other information

If you are running the code locally use the code on the master branch, however if you are running on the testbed computer then use the code on the Production branch

Kafka Server can only be accessed from the flux-hadoop cluster or from the Windows computer connected with the testbed machine. Zookeeper and Kafka host is now migsae-kafka.aura.arc-ts.umich.edu and zookeeper port is 2181 and kafka port is 9092

## Questions

Send questions to hpc-support@umich.edu