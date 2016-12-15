SELECT timeStamp, tagValue FROM OldValues 
	WHERE tagName="::[New_Shortcut]FanucLoopVFD_N045:I.OutputFreq"
	OR tagName="::[New_Shortcut]ABBLoopVFD_N046:I.OutputFreq"
	ORDER BY timeStamp;
