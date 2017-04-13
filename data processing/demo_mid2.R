library(marima)
library(forecast)
fanuc.cur <- read.table("curr_output.txt")
fanuc.vol <- read.table("vol_output.txt")
fanuc.frq <- read.table("freq_output.txt")
cur = t(fanuc.cur[,7]*0.01)
cur = cur[which(fanuc.cur[,7] > 0)];
vol = t(fanuc.vol[,7]*0.1)
vol = vol[which(fanuc.vol[,7] > 0)];
frq = t(fanuc.frq[,7]/60)
frq = frq[which(fanuc.frq[,7] > 0)];

cur_20 = cur[which(frq== 20)];
cur_30 = cur[which(frq== 30)];
cur_45 = cur[which(frq== 45)];
cur_60 = cur[which(frq== 60)];

vol_20 = vol[which(frq== 20)];
vol_30 = vol[which(frq== 30)];
vol_45 = vol[which(frq== 45)];
vol_60 = vol[which(frq== 60)];

data_20 = data.frame(cur_20,vol_20)
data_30 = data.frame(cur_30,vol_30)
data_45 = data.frame(cur_45,vol_45)
data_60 = data.frame(cur_60,vol_60)

auto.arima(cur_45)
auto.arima(vol_45)

cur = cur_60

begin = 0
nstart = 300
nstep = 100

series = data_60[begin:nstart,];
series[,1] = smooth(series[,1])
series[,2] = smooth(series[,2])

series[(nstart+1):(nstart+nstep),] = NA
tmp = auto.arima(cur)
jpeg('acf.jpeg')
acf(tmp$residuals)
dev.off()
jpeg('pacg.jpeg')
pacf(tmp$residuals)
dev.off()
model45 = define.model(kvar=2, ar=c(0,6,7,13),ma=c(5,6,7,13,25));
model30 = define.model(kvar=2, ar = c(0,8,21,26),ma=c(8,9,11,21,26))
model60 = define.model(kvar=2, ar = c(0,23),ma=c(10,12,13,23,25))
model = model45
marima1 = marima(ts(series),model$ar.pattern, model$ma.pattern, penalty=1);
Forecasts = arma.forecast(series=ts(series), marima=marima1, nstart=nstart, nstep=nstep, check=F);
Predict = Forecasts$forecasts[,(nstart+1):(nstart+nstep)];

diff =  data_60[(nstart+1):(nstart+nstep),1]- Predict[1,];

ulc = NA
llc = NA
ulc[1:length(diff)] =  3*sd(diff)
llc[1:length(diff)] =  - 3*sd(diff)
jpeg('marima1.jpg')
plot((nstart+1-begin):(nstart+nstep-begin),Predict[1,], xlim = c(0,(nstart+nstep)-begin),ylim = c(min(cur),max(cur)), main = "current prediction at 60Hz",type='l',col = "blue",xlab= "index",ylab = "current data [Amps]")
par(new = TRUE)
plot(cur[begin:(nstart+nstep)],type = 'l',col = "red",xlim = c(0,(nstart+nstep)-begin), ylim=c(min(cur),max(cur)),xlab= "index",ylab = "current data [Amps]")
legend('topright',legend = c('actual data','prediction'),lty = c(1,1),col=c("red","blue"))
dev.off()
jpeg('marima2.jpg')
plot(diff,type = 'l',col ="blue",ylim = c( -5*sd(diff), 5*sd(diff)),ylab = "residual error [Amps]")
par(new = TRUE)
plot(ulc,type = 'l',col = "red",ylim = c( -5*sd(diff), 5*sd(diff)),ylab = "residual error [Amps]")
par(new = TRUE)
plot(llc,type = 'l',col = "red",ylim = c( -5*sd(diff), 5*sd(diff)),ylab = "residual error [Amps]")
legend('topright',legend = c('bound limit','residual'),lty = c(1,1),col=c("red","blue"))
dev.off()
