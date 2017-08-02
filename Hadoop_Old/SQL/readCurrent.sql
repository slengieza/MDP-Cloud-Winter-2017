SELECT timeStamp, tagValue FROM OldValues 
	WHERE tagName="::[New_Shortcut]FanucLoopVFD_N045:I.OutputCurrent"
	OR tagName="::[New_Shortcut]ABBLoopVFD_N046:I.OutputCurrent"
	ORDER BY timeStamp;
