set terminal png font ',8'
set xlabel "TimeStamp"
set xrange [1.464126e12 : 1.464126018e12]


voltage(x) = m*x + b

fit voltage(x) "VoltageData.txt" using 1:2 via m, b

set output "output.png"
set title "Data vs. Time"
set ylabel "Data"
set yrange [1000 : 1260]
plot "voltage.txt" using 1:2 title "Voltage" with points, voltage(x)