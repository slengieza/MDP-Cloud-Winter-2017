package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;


import java.util.*;

import com.mdp.consumer.KafkaConsumer;
import com.mdp.consumer.ConsumerListener;

public class HadoopClient{

	ConsumerListener listener;

    public HadoopClient(){
    	this.listener = new HadoopConsumer("test", "test", "./data/data.dat");
    }
}