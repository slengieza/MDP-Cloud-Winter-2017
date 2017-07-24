# Instructions For Setting Up Test Environment


1. Open up Windows PowerShell and copy and paste `cd C:\"Rockwell Automation"\MDP-Cloud-Winter-2017\Consumer ; gradle build ; cd ../Producer ; gradle build ; cd ../kafka ; ./bin/zookeeper-server-start.sh ./config/zookeeper.properties "&" ; ./bin/kafka-server-start.sh ./config/server.properties "&" ;`


2. Now your environment is setup,

3.

# Instructions For InfluxDB

1. login is `ssh hkardos@migsae-influx.arc-ts.umich.edu` password is Migffn##567

2. Type `influx -host 'migsae-influx.arc-ts.umich.edu' -ssl`

3. Documentation at `https://docs.influxdata.com/influxdb/v1.3/tools/shell/`
