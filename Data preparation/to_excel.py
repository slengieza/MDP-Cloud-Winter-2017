# -*- coding: utf-8 -*-
"""
Created on Wed Mar  8 21:17:10 2017

@author: LUXIAOTIAN
"""
import re
import json
import os
import xlwt


f=xlwt.Workbook()
ws = f.add_sheet("Sheet1",cell_overwrite_ok=True)



path = 'E:/UMICH_2016_winter/MDP_PROJECT/data/mix/'
files = ''
file_count = 0
for root, dirs, files in os.walk(path):
    file_count = 0;
count = 0;
for i in files:
    print (i)
    str_data = open(path + i, 'r')
    json_data = json.load(str_data)
    
    for entry in json_data:
        if(re.findall('(\S*)(Fanuc)(\S*)(Freq)(\S*)', entry['TagName'])):
            print(entry['TagName'])
            if(int(entry['TagValue'])!=0):
                print(entry['TagValue'])
                ws.write(count, 0, int(entry['TagValue']))
        if(re.findall('(\S*)(Fanuc)(\S*)(Cur)(\S*)', entry['TagName'])):
            print(entry['TagName'])
            if(int(entry['TagValue'])!=0):
                print(entry['TagValue'])
                ws.write(count, 1, int(entry['TagValue']))
        if(re.findall('(\S*)(Fanuc)(\S*)(Vol)(\S*)', entry['TagName'])):
            print(entry['TagName'])
            if(int(entry['TagValue'])!=0):
                print(entry['TagValue'])
                ws.write(count, 2, int(entry['TagValue']))
                count = count + 1;
f.save("frequen_mix.xls")