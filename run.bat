cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka"
cmd /c C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka/bin/zookeeper-server-start.sh C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka/config/zookeeper.properties "&"
cmd /c C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka/bin/kafka-server-start.sh C:\Rockwell Automation\MDP-Cloud-Winter-2017\kafka/config/server.properties "&"
cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer"
cmd /c gradle build
cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer"
cmd /c gradle build
START java -jar "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer\build\libs\Producer.jar"
START java -jar "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer\build\libs\Consumer.jar"
EXIT
