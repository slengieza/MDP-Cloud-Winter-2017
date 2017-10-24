# Instructions For Setting Up Test Environment


1. Open up Windows PowerShell and copy and paste `cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\kafka ;  ./bin/zookeeper-server-start.sh ./config/zookeeper.properties ; ./bin/kafka-server-start.sh ./config/server.properties ; cd ..`

1.1 Next enter `cmd /c run` to build and run both the producer and consumer

1.2 It will prompt you for the name of the series you want to run in the Consumer, enter the name of your experiment: general naming rules no spaces, no special characters [", ', \ ] 


# Instructions For InfluxDB

1. login is `ssh hkardos@migsae-influx.arc-ts.umich.edu` password is Migffn##567

2. Type `influx -host 'migsae-influx.arc-ts.umich.edu' -ssl`

3. Documentation at `https://docs.influxdata.com/influxdb/v1.3/tools/shell/`
