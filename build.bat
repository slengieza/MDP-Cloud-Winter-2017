cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer"
cmd /c gradle build
cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer"
cmd /c gradle build
cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka"
START "./bin/zookeeper-server-start.sh"
START "./config/zookeeper.properties"
START "./bin/kafka-server-start.sh" 
START "./config/server.properties"
EXIT