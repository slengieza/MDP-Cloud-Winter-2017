set Pathname1="C:\Rockwell Automation\MDP-Cloud-Winter-2017\Producer"
cd %Pathname1%
START gradle build
echo "Passed First Gradle"
set Pathname2="C:\Rockwell Automation\MDP-Cloud-Winter-2017\Consumer"
cd %Pathname2%
START gradle build
echo "Passed Second Gradle"
