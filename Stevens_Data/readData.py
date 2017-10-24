import argparse
import os
import json
import time
import datetime

from influxdb import InfluxDBClient

json_body = [
	{
		"measurement": "",
		"tags": {

		},
		"time": "",
		"fields": {

		}
	}
]

def readInFiles(host, port):
	user = 'root'
	password = 'root'
	dbname = 'test'
	client = InfluxDBClient(host, port, user, password, dbname)
	for filename in os.listdir(os.getcwd()):
		dataPoints = {}
		json_body = []
		if filename.endswith(".dat"):
			print("processing file " + filename)
			with open(filename) as f:
				text = f.read()
				values = json.loads(text)
				for val in values:
					timeStamp = val.get('TimeStamp').split('(')[1].split(')')[0]
					tagName = val.get('TagName')
					tagValue = float(val.get('TagValue'))
					if timeStamp not in dataPoints:
						dataPoints[timeStamp] = {}
					dataPoints[timeStamp][tagName] = tagValue
					# print(timeStamp)
				for key in dataPoints:
					json_body.append({"measurement" : "test",
									  "tags": { "timeStamp": timeStamp},
									  "time": datetime.datetime.now(),
									  "fields": dataPoints[key]
									  })
				# print(json_body)
		client.write_points(json_body)
		time.sleep(1)


			# Point fullPoint = Point.measurement(this.series)
			# 		.time(timeStamp, TimeUnit.MILLISECONDS)
			# 		.fields(fields_list)
			# 		.build();
			# this.batchPoints.point(fullPoint);
			# influxDB.write(this.batchPoints);


def main(host='localhost', port=8086):
	readInFiles(host, port)

def parse_args():
	"""Parse the args."""
	parser = argparse.ArgumentParser(
		description='example code to play with InfluxDB')
	parser.add_argument('--host', type=str, required=False,
						default='localhost',
						help='hostname of InfluxDB http API')
	parser.add_argument('--port', type=int, required=False, default=8086,
						help='port of InfluxDB http API')
	return parser.parse_args()


if __name__ == '__main__':
	args = parse_args()
	main(host=args.host, port=args.port)