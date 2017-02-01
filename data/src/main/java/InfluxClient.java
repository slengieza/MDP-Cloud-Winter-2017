package com.mdp.data;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;

import java.util.*;

import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import com.mdp.data.InfluxReader;

/*
This class is used to interact with influxDB. There are three 
options, read, listen and quit. To run, first start zookeeper
 and then kafka. 

Next build and run this class using "gradle build" and 
"java -jar ./build/libs/InfluxDB.jar" from the main
InfluxDB directory

Once all three are running, select a method either listen,
read or quit. If you selexct listen, you will then need to go
to the Kafka directory and run "./gradlew run" from the main
directory.
*/

public class InfluxClient{

    private String username;
    private String password;
    private String database;
    private String dbName;
    private String measurementName;
    private InfluxDB influxDB;
    private InfluxReader reader;

    public InfluxClient(String username, String password, String database, String dbName){
        this.username = username;
        this.password = password;
        this.database = database;
        this.dbName = dbName;
        this.influxDB = InfluxDBFactory.connect(database, username, password);
        this.reader = new InfluxReader(this.influxDB);   
    }

    public static void main(String[] args) throws Exception{
        String groupId = "1";
        String topic = "test1";
        String username = "cloud_data";
        String password = "2016SummerProj";
        String database = "https://migsae-influx.arc-ts.umich.edu:8086";
        String dbName = "test";
        int threads = 1;
        
        InfluxClient client = new InfluxClient(username, password, database, dbName);
        
        while(true){ 
            //  prompt for the method and get input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your method (read/delete/quit): ");
            String method = scanner.next();
            if(method.equals("quit") || method.equals("q")){
                break;
            }
            System.out.print("Please enter your Query: ");
            String query = "";
            String word = scanner.next();
            while(!word.endsWith(";")){
                query += word + " ";
                word = scanner.next();
            }
            query += word + " ";
            
            client.reader.execute(query);
        }
    }
}