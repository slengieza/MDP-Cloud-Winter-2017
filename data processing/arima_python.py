import warnings
import numpy as np
from numpy import *
import sys
import statsmodels.api as sm
from statsmodels.tsa.arima_model import ARIMA
import matplotlib.pyplot as plt
import sh

# using c++ program to format string
run = sh.Command("./format_string")
run("curr.txt")

# load data
data = loadtxt("curr_output.txt")
data = np.array(data)
X = data[:,6]*0.001
training_num = int(len(X)*0.8)

model = ''
best_val = 0.0
order = ''

# did brute force search to find best model
for p in range(6):
    for d in range(1):
        for q in range(4):
            try:
                arima_mod=sm.tsa.ARIMA(X[:training_num],(p,d,q)).fit(transparams=True)
                x=arima_mod.aic
                x1= p,d,q
                #print (x1,x)
                if  x<best_val:
               		model = arima_mod
               		order = x1
            except:
                pass


# prediction and plot
fit=model.predict(0,len(X))
plt.plot(X, 'b-', label='data')
plt.plot(range(training_num), model.fittedvalues, 'g--', label='fit')
plt.plot(range(len(fit)), fit, 'r-', label='predict')
plt.legend(loc=4)
plt.savefig("arima_predict.png")

# save model info to file
f = open('arima_output.txt','w')
sys.stdout = f
print x1
print model.summary().tables[1]
f.close()
