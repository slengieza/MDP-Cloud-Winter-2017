//
//  helper_functions.cpp
//  MDP-Cloud
//
//  Created by Marco Schwiebert on 10/12/16.
//  Copyright © 2016 mschwiebert. All rights reserved.
//

#include "helper_functions.hpp"
#include <iostream>
#include <unistd.h>
#include <ctime>
#include <string>
#include <cmath>

using namespace std;

double Linear_Function(double x){
    return 10*x;
}

double Quadratic_Function(double x){
    return x*x;
}

double Logistic_Function(double x, double max){
    return max/(1+ pow(2.71828, -x));
    
}

void Produce_Data_File(){
    
}

void prompt_user(){
    string y;
    cout << "Enter y to begin producing .dat files: ";
    cin >> y;
    while(y != "y"){
        cout << "Enter y to begin producing .dat files: ";
        cin >> y;
    }
    cout << endl;
}
