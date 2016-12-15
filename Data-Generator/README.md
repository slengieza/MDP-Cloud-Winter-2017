# Data-Generator 

This code outputs JSON formatted files in the exact format the testbed computer outputs data files every second. 

## Prerequisites 

To build the C++ program, you need a C++ compiler such as g++ (you already have it if you have a mac)

## Building

To compile:

```
g++ helper_functions.cpp main.cpp -o main.exe
```

## Deployment

```
./main.exe <number of files>
``` 

For example:

```
./main.exe 10
```
will create 1 .dat file in the current directory every second for 10 seconds. 


