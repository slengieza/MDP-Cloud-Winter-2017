package com.mdp.consumer;

import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.List;

/***
	Implement this interface to use the Consumer
		- onReceiveMessage(string message):
			This function will be called everytime the consumer
			got a new message, you handle/process the message
			in this function
		- onShutdown():
			When you call shutdown on the Consumer, this function
			will be called for every thread. If you need to,
			handle some exit cleanups, etc. in here.
***/
public interface ConsumerListener {
/*

	message format
	[
		timeStamp,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputFreq,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputCurrent,
		[New_Shortcut]FanucLoopVFD_N045:I.OutputVoltage,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputFreq,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputCurrent,
		[New_Shortcut]ABBLoopVFD_N046:I.OutputVoltage
	]
	*/
	public void onReceiveMessage(String message);
	public void onShutdown();
}