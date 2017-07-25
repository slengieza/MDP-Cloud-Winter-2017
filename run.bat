cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer"
cmd /c gradle build
cd "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer"
cmd /c gradle build
START java -jar "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer\build\libs\Producer.jar"
START java -jar "C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer\build\libs\Consumer.jar"
EXIT
