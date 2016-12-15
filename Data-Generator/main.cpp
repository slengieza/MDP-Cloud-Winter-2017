//
//  main.cpp
//  MDP-Cloud
//
//  Created by Marco Schwiebert on 10/5/16.
//  Copyright © 2016 mschwiebert. All rights reserved.
//
//After compiling run: <./program.exe 20> where the argument is the 
//number of files you want to generate and program.exe is whatever 
//you name the program after compiling. The program will then generate
//1 file a second in the working directory. The data is JSON formatted 
//just like the data from the testbed.
//


#include <iostream>
#include <unistd.h>
#include <ctime>
#include <string>
#include <cmath>
#include <fstream>
#include <sstream>
#include "helper_functions.hpp"

using namespace std;



int main(int argc, const char * argv[]) {
    stringstream ss;
    string filename;
    double output;
    prompt_user();
        for(int i = 0; i < atoi(argv[1]); ++i){
            filename =  "test_data_" + to_string(i+1) + ".dat";
            fstream ofs(filename, fstream::out);
            output = Linear_Function(i); //options are Quadratic_Function, Logistic_function, Linear_Function
            ofs << "{\"Status\":0,\"TagName\":\"::[New_Shortcut]FanucLoopVFD_N045:I.OutputFreq\",\"TagValue\":\"" << output << "\",\"TimeStamp\":\"\/Date(1464126015417-0400)\/\"},{\"Status\":0,\"TagName\":\"::[New_Shortcut]FanucLoopVFD_N045:I.OutputCurrent\",\"TagValue\":\"" << output << "\",\"TimeStamp\":\"\/Date(1464126015417-0400)\/\"},{\"Status\":0,\"TagName\":\"::[New_Shortcut]FanucLoopVFD_N045:I.OutputVoltage\",\"TagValue\":\"" << output << "\",\"TimeStamp\":\"\/Date(1464126015417-0400)\/\"}";
            ofs.close();
            usleep(1000000);
            
    }
}

//usleep(1000000);


/*{"Status":0,"TagName":"::[New_Shortcut]FanucLoopVFD_N045:I.OutputFreq","TagValue":"1800","TimeStamp":"\/Date(1464126015417-0400)\/"},
{"Status":0,"TagName":"::[New_Shortcut]FanucLoopVFD_N045:I.OutputCurrent","TagValue":"50","TimeStamp":"\/Date(1464126015417-0400)\/"},
{"Status":0,"TagName":"::[New_Shortcut]FanucLoopVFD_N045:I.OutputVoltage","TagValue":"1077","TimeStamp":"\/Date(1464126015417-0400)\/"},
{"Status":0,"TagName":"::[New_Shortcut]ABBLoopVFD_N046:I.OutputFreq","TagValue":"3600","TimeStamp":"\/Date(1464126015417-0400)\/"},
{"Status":0,"TagName":"::[New_Shortcut]ABBLoopVFD_N046:I.OutputCurrent","TagValue":"39","TimeStamp":"\/Date(1464126015417-0400)\/"},
{"Status":0,"TagName":"::[New_Shortcut]ABBLoopVFD_N046:I.OutputVoltage","TagValue":"1201","TimeStamp":"\/Date(1464126015417-0400)\/"}*/