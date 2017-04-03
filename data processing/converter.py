import re
import glob
import numpy
# how to use:
# 	1. create a folder called "data"
#	2. put all the raw data files into this folder
#	3. run the converter file with command "python converter.py"
#	4. all the output will be stored into a file called "data.txt"



data = []
for filename in glob.glob('./data/*'):
	with open(filename,'r') as inF:
		num = []
		for line in inF:
			if '[ ].Average' in line and 'Part' in line:
				num.append(map(float, re.findall(r'(\d+.\d+)',line)))
		if len(num) > 0:
			data.append(numpy.asarray(num))
numpy.savetxt('data.txt',data)