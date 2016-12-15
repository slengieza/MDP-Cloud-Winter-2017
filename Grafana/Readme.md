Download Grafana from the following link:
http://grafana.org/download/

Then use http://docs.grafana.org/installation/ and install it for your system using the custom.ini included in the repo

This is an additional guide on how to install Grafana as a windows service:
https://www.youtube.com/watch?v=QJxk0V5i6qM

Add the data sources from influxDB using the information below:

Read access account
username: graphana_acc
pass: testBed_read16

The names of the databases we use are "test" and "production"
There is no additional information needed to add InfluxDB as a data source

After you install the datasources you can build dashboards using graphs and queries.

There is a test dashboard in the "dashboards" directory which will display a times series plot of each input field

TODO: add details to configure Grafana 
