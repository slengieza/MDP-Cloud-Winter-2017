#!/bin/sh
####  PBS preamble

#PBS -N sample_job

# Change "bjensen" to your uniqname:
#PBS -M yanchenj@umich.edu
#PBS -m abe

# Change the number of cores (ppn=1), amount of memory, and walltime:
#PBS -l nodes=1:ppn=1,mem=2000mb,walltime=01:00:00
#PBS -j oe
#PBS -V

# Change "example_flux" to the name of your Flux allocation:
#PBS -A example_flux
#PBS -q flux
#PBS -l qos=flux

####  End PBS preamble

#  Show list of CPUs you ran on, if you're running under PBS
if [ -n "$PBS_NODEFILE" ]; then cat $PBS_NODEFILE; fi

#  Change to the directory you submitted from
if [ -n "$PBS_O_WORKDIR" ]; then cd $PBS_O_WORKDIR; fi

#  Put your job commands here:
./ou
Rscript demo_mid2.R
Rscript arima_demo.R