'''
*  An ad hoc query script for InfluxDB
*
*
*
'''

import os, sys
import subprocess

#
query = raw_input("Please Enter Your Query : ")
series = query.lower().split(" from ")[1].translate(None, '\"') + "_data.csv" # String manipulation to remove the series name from the query
print(os.getcwd() + series)
if os.path.isfile(os.getcwd() + "/" + series): # Check to see if a file already exists
    ins = raw_input("File already exists: Press Y to append, or N to overwrite : ")
    while ins.lower() != "y" and ins.lower() !="n":
        print (ins.lower())
        ins = raw_input("Please Enter Y to append or N to overwrite")
    if ins.lower() == "n":
        os.system("rm " + series) # Remove file if it already exists
        os.system("touch " + series)
        command = "influx -host 'migsae-influx.arc-ts.umich.edu' -ssl -execute \'" + query + "\' -database=\"test\" -format=csv -precision rfc3339 > " + series
        os.system(command)
    elif ins.lower() == "y":
        command = "influx -host 'migsae-influx.arc-ts.umich.edu' -ssl -execute \'" + query + "\' -database=\"test\" -format=csv -precision rfc3339 >> " + series
        os.system(command)
else:
    os.system("touch " + series)
    command = "influx -host 'migsae-influx.arc-ts.umich.edu' -ssl -execute \'" + query + "\' -database=\"test\" -format=csv -precision rfc3339 > " + series
    os.system(command)
