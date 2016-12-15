hive --hiveconf mapreduce.job.queuename=nsf-cloud -f ../SQL/readFrequency.sql > frequencyData.txt
hive --hiveconf mapreduce.job.queuename=nsf-cloud -f ../SQL/readCurrent.sql > CurrentData.txt
hive --hiveconf mapreduce.job.queuename=nsf-cloud -f ../SQL/readVoltage.sql > VoltageData.txt

echo "Plotting Voltage Frequency and Current Data"
plotScript="plotscript.plt"
gnuplot $plotScript