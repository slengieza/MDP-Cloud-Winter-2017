# Data

This code is used to query InfluxDB from the command line. 

## Building

Run the following commands from the main Consumer directory

To build:

```
gradle build
```

## Deployment

To start:

```
java -jar ./build/libs/data.jar 
```

Once started you will be prompted for a method, either read, delete or quit. After selecting a method you will be prompted for a Query. This query must be terminated by a ";". 

The data is in OldValues and the fields that we will currently be using are as follows:

1. fanucFreq
	* This is the frequency data for the Fanuc Robot Arm
2. fanucVoltage
	* This is the voltage data for the Fanuc Robot Arm
3. fanucCurrent
	* This is the current data for the Fanuc Robot Arm
4. abbFreq
	* This is the frequency data for the ABB Robot Arm
5. abbVoltage
	* This is the voltage data for the ABB Robot Arm
6. abbCurrent
	* This is the current data for the ABB Robot Arm
