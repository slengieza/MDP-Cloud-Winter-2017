import os
import json
import time
from datetime import datetime

last_query = "2017-01-01"

while True:
    now = datetime.now().strftime('%Y-%m-%d')
    print(now) 
    query="SELECT * FROM TrainingData WHERE TIME > \'" + last_query + "\' AND TIME < \'" + now + "\'"
    command = '''curl -G 'https://migsae-influx.arc-ts.umich.edu:8086/query?pretty=true' -u cloud_data:2016SummerProj --data-urlencode "db=test" --data-urlencode "epoch=s" --data-urlencode "q=''' + query + '''" > data.txt'''
    
    os.system(command)
    
    f = open("data.txt", "r")
    
    jsonString = ""
    for line in f:
        jsonString += line
   
    f.close() 
    jsonObj = json.loads(jsonString)
    
    arr = jsonObj["results"]
    if len(arr) != 0 and "series" in arr[0]:
	arr = arr[0]["series"]
	if len(arr) != 0 and "values" in arr[0]:
		arr = arr[0]["values"]
    
    newFile = open('hadoop_' + now + '.txt', 'w')
    
    for entry in arr:
        string = ""
        for i in range(0, len(entry)):
    	    if i != len(entry)-1:
    	        string += str(entry[i]) + "\t"
    	    else:
    	        string += str(entry[i]) + "\n"
        newFile.write(string)
    
    newFile.close()

    fileExists = "hadoop fs -test -e /var/mdp-cloud/hadoop_" + now + ".txt"
    if os.system(fileExists):
	 touchFile = "hadoop fs -touchz /var/mdp-cloud/hadoop_" + now + ".txt"
  
    
    
    #removeFile = "hadoop fs -rm /var/mdp-cloud/hadoop_" + now + ".txt"
    #os.system(removeFile)
    hadoopCommand = "hadoop fs -appendToFile  hadoop_" + now + ".txt /var/mdp-cloud/hadoop_" + now + ".txt"
    os.system(hadoopCommand)
    removeLocalFile = "rm hadoop_" + now + ".txt"
    os.system(removeLocalFile)
    
    last_query = now

    time.sleep(3)
    #time.sleep(86400)
