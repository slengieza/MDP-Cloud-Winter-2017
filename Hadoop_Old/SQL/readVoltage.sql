SELECT timeStamp, tagValue FROM OldValues 
	WHERE tagName="::[New_Shortcut]FanucLoopVFD_N045:I.OutputVoltage"
	OR tagName="::[New_Shortcut]ABBLoopVFD_N046:I.OutputVoltage"
	ORDER BY timeStamp;
