# -*- coding: utf-8 -*-
"""
Created on Mon Jan  2 16:47:53 2017

@author: LUXIAOTIAN
"""

import xlrd
import xlwt


fname = "vol_freq.xlsx"
bk = xlrd.open_workbook(fname)
sh = bk.sheet_by_name("Sheet1")

f=xlwt.Workbook()
ws = f.add_sheet("Sheet1",cell_overwrite_ok=True)

nrows = sh.nrows
j = 0
time = 0
for i in range(nrows-1):
    if int(sh.cell_value(i,0))!=0 and int(sh.cell_value(i+1,0))!=0:
        time=time+1
    if int(sh.cell_value(i,0))!=0 and int(sh.cell_value(i+1,0))==0:
        ws.write(j,0,sh.cell_value(i,0))
        j = j+1;
        time = 0;         
f.save("frequen_time.xls")
