#!/sw/lsa/centos7/python-anaconda2/201607/bin/python

from numpy import array
from pyspark import SparkContext
from pyspark.mllib.clustering import GaussianMixture, GaussianMixtureModel

if __name__ == "__main__":
    sc = SparkContext(appName="GaussianMixtureExample")  # SparkContext

    ### Local default options
    k=2 # "k" (int) Set the number of Gaussians in the mixture model.  Default: 2
    convergenceTol=0.001 # "convergenceTol" (double) Set the largest change in log-likelihood at which convergence is considered to have occurred.
    maxIterations=150 # "maxIterations" (int) Set the maximum number of iterations to run. Default: 100
    seed=None # "seed" (long) Set the random seed

    # Load and parse the data    
    data = sc.textFile("/var/mdp-cloud/gmm_data.txt")
    parsedData = data.map(lambda line: array([float(x) for x in line.strip().split(' ')])) 
    # filteredData = data.filter(lambda arr: int(arr[1]) != 0)	

    # Build and save the model (cluster the data)
    gmm = GaussianMixture.train(parsedData, k, convergenceTol=0.001, maxIterations=150, seed=None)
    # gmm.save(sc, "target/org/apache/spark/PythonGaussianMixtureExample/GaussianMixtureModel")
    # gmm.save(sc, "GaussianMixtureModel_CV")
    # The following line would load the model
    # sameModel = GaussianMixtureModel.load(sc, "target/org/apache/spark/PythonGaussianMixtureExample/GaussianMixtureModel")

    # output parameters of model
    for i in range(k):
        print("weight = ", gmm.weights[i], "mu = ", gmm.gaussians[i].mu,
              "sigma = ", gmm.gaussians[i].sigma.toArray())

    sc.stop()
