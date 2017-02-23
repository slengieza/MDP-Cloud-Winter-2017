# to start with, have to install the following packges
# install.packages("devtools")
# dev::install_github("robjhyndman/forecast")
# try following
library(forecast)
library(MASS)

#LOAD DATA
data <- read.table("fanucCurr_output.txt")
time.year <- data[,1]
time.month <- data[,2]
time.day <- data[,3]
time.hour <- data[,4]
time.minutes <- data[,5]
time.sec <- data[,6]
curr <- data[,7]*0.01
rm(data);
data <- read.table("fanucVol_output.txt")
vol <- data[,7]*0.1
rm(data);
data <- read.table("fanucFreq_output.txt")
freq <-data[,7]/10
rm(data);
data.freq = freq
data.vol = vol
data.curr = curr
rm(freq);
rm(curr);
rm(vol);

# ASSIGN DATA ATTRIBUTES
data.freq.mean = mean(data.freq)
data.freq.min = min(data.freq)
data.freq.max = max(data.freq)
data.freq.std = sd(data.freq)
data.freq.max_idx = which(data.freq %in% data.freq.max)
data.vol.mean = mean(data.vol)
data.vol.min = min(data.vol)
data.vol.max = max(data.vol)
data.vol.std = sd(data.vol)
data.vol.max_idx = which(data.vol %in% data.vol.max)
data.vol.idx = which(data.vol > data.vol.min)
data.curr.mean = mean(data.curr)
data.curr.min = min(data.curr)
data.curr.max = max(data.curr)
data.curr.std = sd(data.curr)
data.curr.max_idx = which(data.curr %in% data.curr.max)
data.curr.idx = which(data.curr > data.curr.min)

#PRINT STATS
print(c("mean volatge is:",data.vol.mean))
print(c("max voltage is:",data.vol.max))
print(c("voltage std is:",data.vol.std))
print(c("mean current is:",data.curr.mean))
print(c("max current is:",data.curr.max))
print(c("current std is:",data.curr.std))

#PLOT 
hist(data.curr[data.curr.idx], breaks = seq(data.curr.min,data.curr.max,by=0.01), main ="Histogram of Observed Current Data", xlab = "current (Amp)", ylab = "frequency ()", xlim = c(0.35,0.55), plot= TRUE)
plot(density(data.curr[data.curr.idx]), main = "Density Estimate of Current Data")
fitdistr(data.curr[data.curr.idx],"normal")
hist(data.freq[which(data.freq > 0)],main ="Histogram of Observed Frequency Data",xlab = "frequency (Hz)", ylab = "frequency ()")
x11()
hist(data.vol[data.vol.idx], breaks = seq(data.vol.min,data.vol.max,by=0.1), main = "Histogram of Observed Voltage Data", xlab = "voltage (Volt)", ylab = "frequency ()", xlim = c(100,108), plot= TRUE)
plot(density(data.vol[data.vol.idx]), main = "Density Estimate of Voltage Data")
fitdistr(data.vol[data.vol.idx],"normal")


# Deal with data
dec = which(time.month == 12)
decidx = which(data.freq[dec] < 200)
dec.cur = data.curr[which(data.curr[decidx] > 0)]
dec.vol = data.vol[which(data.vol[decidx] > 0)]
cur_fit <- auto.arima(dec.cur[1:5000])
vol_fit <- auto.arima(dec.vol[1:5000])

par(mfrow = c(2,1))
plot(forecast(cur_fit, h = length(dec.cur)-5000), xlab = "index of current data point", ylab = "actual current data (Amp)",xlim = c(0,length(dec.cur))) 
par(new = TRUE)
plot(dec.cur,type = "l", xlab = "index of current data point", ylab = "actual current data (Amp)",xlim = c(0,length(dec.cur)))
err.cur = 0
err.cur[1:5000] = 0.0;
err.cur[5001:length(dec.cur)] = dec.cur[5001:length(dec.cur)]-as.numeric(unlist(forecast(cur_fit, h = length(dec.cur)-5000)[4]))
plot(err.cur, type = "h", xlab = "index of current data point")

x11()
par(mfrow = c(2,1))
plot(forecast(vol_fit, h = length(dec.vol)-5000), xlab = "index of voltage data point", ylab = "actual voltage data (Volt)",xlim = c(0,length(dec.vol))) 
par(new = TRUE)
plot(dec.vol,type = "l", xlab = "index of voltage data point", ylab = " ",xlim = c(0,length(dec.vol)))
err.vol = 0
err.vol[1:5000] = 0.0;
err.vol[5001:length(dec.vol)] = dec.vol[5001:length(dec.vol)]-as.numeric(unlist(forecast(vol_fit, h = length(dec.vol)-5000)[4]))
plot(err.vol, main = "prediction model vs. actual data error",type = "h", xlab = "index of voltage data point", ylab = "error between model and test data (Volt)")


#x11()
