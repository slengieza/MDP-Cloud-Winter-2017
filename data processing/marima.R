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
fanuc = data.frame(cur,vol,frq)

# find arima model for every data attributes
auto.arima(cur) # (5,1,3)
gglagplot(cur)
acf(cur)
pacf(cur)
auto.arima(vol) # (1,1,2)
gglagplot(vol)
acf(vol)
pacf(vol)
auto.arima(frq) # (0,1,1)
gglagplot(frq)
acf(frq)
pacf(frq)

# three variant 
kvar = 3

# find difference
fanuc.dif = define.dif(fanuc,difference = c(1,1 ,2,100 ,3,1))
fanuc.dif.analysis = fanuc.dif$y.dif

ar = c(1,100,1)
ma = c(1)

mod = define.model(kvar=3, ar=ar, ma=ma, reg.var=3)
arp = mod$ar.pattern
map = mod$ma.pattern

# print out model
short.form(arp)
short.form(map)

model = marima(fanuc.dif.analysis, ar.pattern=arp, ma.pattern=map, penalty=1)
resid = arma.filter(fanuc, model$ar.estimates, model$ma.estimates)
plot(model$residuals[2,1:10000],type = "l", col = "red" ,ylab = "residuals")
par(new = T)
plot(resid$residuals[2,1:10000], type = "l", col = "blue", ylab = "" )

ar.model = model$ar.estimates
ma.model = model$ma.estimates
dif.poly = fanuc.dif$dif.poly
ar.aggregated = pol.mul(ar.model, dif.poly, L=12)

# print out model
short.form(ar.aggregated)
short.form(ma.model)

# forcast 
nstart = 4000
nstep = 1000
Forecasts = arma.forecast(series=ts(fanuc[1:5000,]), marima=model, nstart=nstart, nstep=nstep, dif.poly = dif.poly, check=FALSE)
Predict = Forecasts$forecasts[,(nstart+1):(nstart+nstep)]
jpeg('marima_plot.jpg')
plot((nstart+1):(nstart+nstep),Predict[2,], xlim = c(0,5000),ylim = c(30,130), type='l',col = "blue",xlab= "index",ylab = "data")
par(new = TRUE)
plot(vol[1:5000],type = 'l',col = "red",xlim = c(0,5000), ylim = c(30,130),xlab= "index",ylab = "data")
dev.off()