# MDP-Cloud-Winter-2017

This is part of the Secure Cloud Manufacturing Research Group at the University of Michigan. 

The intention of this code is to be able to move data from a test bed computer into various databases hosted by ARC-TS. Data files are added to a working directory on a test bed computer approximately every second (this could increase to every 10ms).With the Producer class running, it will be alerted of each new file added and then it will send the data to a Kafka server hosted by ARC-TS. With the Consumer class running, it will notice a new message on the Kafka server queue and then either insert the data into InlfuxDB instantly, however for Hadoop, it will append the data to a new file and write the data into HDFS once a day. Once data is in InfluxDB, it can be visualized in real time using Grafana (instructions to install are below). 

There are several subdirectories that comprise this project. Each one has their own functionality, as well as instructions for running. All of these will be detailed below.

#### Prerequisites

Before starting, you must download and install the following software.

1. Kafka (This is found at https://kafka.apache.org/082/documentation.html)
2. Flux (This is found at http://arc-ts.umich.edu/flux-user-guide/)
	* You must apply for this account and request Hadoop access
3. Grafana (This is found at http://grafana.org/download/)
	* There are dashboards located in the Grafana subdirectory
4. You must install gradle on your flux account to run the Hadoop code. Use the following commands

```
curl -s https://get.sdkman.io | bash
source "/home/<your username>/.sdkman/bin/sdkman-init.sh"
sdk install gradle 3.2.1
```

#### Test Bed Computer

The test bed computer is in the Integrated Manufacturing System Laboratory, and is the primary computer for taking data. Data collection is automatic, and can be started by running the Rockwell Cloud Elastic Agent program. In order to use the Producer and Consumer, we must configure the test bed computer. To do this type or copy and paste the following commands:

```
cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\kafka
./bin/zookeeper-server-start.sh ./config/zookeeper.properties "&"
./bin/kafka-server-start.sh ./config/server.properties "&"
```

This sets up the Zookeeper server which lets us run Kafka.

## Producer

The Producer works by creating a process that watches a target folder on the test bed computer and then creating a Kafka message that can be picked up by the consumer. This code must be run on the test bed computer, as it is already configured for that test bed. If you wish to run the Producer on its own, enter the following commands

```
cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\Producer
gradle build
java -jar ./build/libs/Producer.jar
```

However, if you'd like to run it and the Consumer at the same time (as you most likely do), you can run both with the following command:

```
start-process "cmd.exe" "/c C:\Rockwell Automation\MDP-Cloud-Winter-2017\run.bat"
```

## Consumer

Like the Producer, the Consumer must be run from the test bed computer. The Consumer works by receiving messages from Kafka and then parsing the message, creating points from them, and then writing those points to InfluxDB. This program will also prompt the user for input before running, so it is important to enter that before the program can run. This input will be the name of the series used when writing to InfluxDB (this is a design choice made to make running multiple tests easier). For example, if you enter "test1", all the points generated while the program runs will be grouped together in InfluxDB under the "test1" series. Like the Producer, the Consumer can be run independently (for testing t.ex.) of the producer by entering the following commands:

```
cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\Consumer
gradle build
java -jar ./build/libs/Consumer.jar
```

Or, the Consumer can be run concurrently with the Proudcer using the following command:

```
start-process "cmd.exe" "/c C:\Rockwell Automation\MDP-Cloud-Winter-2017\run.bat"
```

## Data

The Data program is a command line program used to manually query InfluxDB. It can be run from any computer as long as you are connected to the UM Wireless network (either through MWireless or through the Michigan VPN, more information [here](http://its.umich.edu/enterprise/wifi-networks/vpn)). This program is useful for if you do not have, nor can install the InfluxDB command line interface (documentation for this is [here](https://docs.influxdata.com/influxdb/v1.2.4/tools/shell/)). Although the preferrable way to access InfluxDB is through the Influx provided command line interface, this program works if you can not run that. To run this program, use the following commands:

```
cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\Data
gradle build
java -jar ./build/libs/Data.jar
```

## HDFS

The purpose of this program is to read data from InfluxDB and write it to our Hadoop Distributed File System. In production, we would set this program to run on a given interval (daily, weekly, monthly, etc.), but due to the ad hoc nature of our testing, we just run it manually. Currently, this program is hard-coded to operate on our "Test" database in InfluxDB. The program will prompt user input to determine whether they would like to add one or all of the series, as well as prompting for which series if the user chooses to add just one. Once this program is run, it will query the series data from InfluxDB and convert that data to JSON format (to utilize Spark SQL later). For each series, it will create a temporary local file, and then put that file into our HDFS. When this is complete, the program will drop that series from InfluxDB. To use this program, you must be in the ARC-TS Hadoop Flux cluster. Then, run the following commands:

```
cd /your/path/to/MDP-Cloud-Winter-2017/HDFS
gradle build
java -jar ./build/libs/HDFS.jar
```

## SparkBatching

The job of this program is to read data from our HDFS and do various operations on it. The main difference between this program and the Spark Streaming one is the method for reading in data. This program simply acts as the framework, for every operation we wish to run on this data we must write a new function here. To use this program, you must be in the ARC-TS Hadoop Flux cluster. Then, run the following commands:

```
cd /your/path/to/MDP-Cloud-Winter-2017/SparkBatching
gradle build
java -jar ./build/libs/SparkBatching.jar
```

## SparkStreaming

The job of this program is to read data straight from InfluxDB. [^1]:

## Softwares and Passwords

#### InfluxDB

InfluxDB is a time series database that we use to store our data points in the short term. To install InfluxDB on your local machine you can enter the following commands:

For OS X:

```
brew update
brew install influxdb
```

For Ubuntu/Debian:

```
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.3.2_amd64.deb
sudo dpkg -i influxdb_1.3.2_amd64.deb
```

For Linux:

```
wget https://dl.influxdata.com/influxdb/releases/influxdb-1.3.2_linux_amd64.tar.gz
tar xvfz influxdb-1.3.2_linux_amd64.tar.gz
```

For Windows:

```
wget https://dl.influxdata.com/influxdb/releases/influxdb-1.3.2_windows_amd64.zip
unzip influxdb-1.3.2_windows_amd64.zip
```

Once that is installed, and once you are connected to either MWireless or UMVPN, you can access the database using the following command line command:

```
influx -host 'migsae-influx.arc-ts.umich.edu' -ssl
```

If you are prompted for a username and password, you can add `-username="admin" -password="admin"`

Our influx cluster is at `migsae-influx.arc-ts.umich.edu`. If you'd like to be able to change configuration files, or have admin access to the Influx cluster, please email `hkardos@umich.edu`.

#### HDFS

You must have a Flux Hadoop account (if you don't have one see [here](http://arc-ts.umich.edu/systems-and-services/hadoop/)) to be able to run the HDFS and Spark code. To be able to interact with our data, you must ssh into your account. From here, the location for our data is `hdfs:///var/mdp-cloud/`. HDFS is not POSIX, so here are several useful commands:

Equivalent of ls

```
hdfs dfs -ls /var/mdp-cloud/ 
```

Equivalent to rm (pass -skipTrash to avoid storing meta data when deleting it):

```
hdfs dfs -rm -skipTrash /var/mdp-cloud/<filename>
```


#### Other information

If you are running the code locally use the code on the master branch, however if you are running on the testbed computer then use the code on the Production branch

Kafka Server can only be accessed from the flux-hadoop cluster or from the Windows computer connected with the testbed machine. Zookeeper and Kafka host is now migsae-kafka.aura.arc-ts.umich.edu and zookeeper port is 2181 and kafka port is 9092

## Questions

Send questions about codebase to hkardos@umich.edu, and questions about access and softwares to hpc-support@umich.edu

## Notes / TODO
[^1]: Note: Read Me to be appended to later
