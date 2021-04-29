# Shell脚本汇总

## 0.windows开机启动程序`start.bat`

```bash
@echo off
start "" "D:\Dev\JetBrains\IntelliJ IDEA 2019.3.4\bin\idea64.exe"
start "" "D:\Dev\Microsoft VSCode\Code.exe"
start "" "D:\Program Files (x86)\Notepad++\notepad++.exe"
start "" "D:\Program Files (x86)\Typora\Typora.exe"
start "" "D:\Program Files (x86)\NetSarang\Xshell.exe"
start "" "D:\Program Files (x86)\FastStone Capture\FSCapture.exe"
start "" "D:\Program Files (x86)\Navicat Premium 12\navicat.exe"
```



## 1.重启当前dev环境的jar包

输入 ： ./manual_restart.sh apollo-request-parser-1.2.0-SNAPSHOT.jar

```sh
#!/bin/sh

NAME=$1
echo $NAME

if [ ! -n "$1" ] ;then
    echo "you have not input a word!"
	exit $?
fi



echo "chmod u+x $NAME"

chmod u+x $NAME
 

ID=`ps -ef | grep "$NAME" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
echo $ID
echo "---------------"
for id in $ID
do
kill -9 $id
echo "killed $id"
done
echo "---------------"


nohup java -jar $NAME > /dev/null 2>&1 &
echo "exec $NAME"



tail -f logs/app/*.log
```





## 2.执行Server Worker-agent的服务

- mvn clean/ mvn install 
- start server
- wait for hello response
- start worker-agent

```shell
#!/bin/bash

echo "============GIT============"
git pull

echo "============COMPILE & INSTALL : BEGIN============"
cd /app/hadoop/small-bang
mvn clean install
echo "============COMPILE & INSTALL : BEGIN============"


echo "============STOP SERVER============"
ps -ef | egrep "bigbang-server" | grep -v "grep" | awk '{print $2}' | xargs kill -9

echo "============START SERVER============"
java -jar  /app/hadoop/small-bang/bigbang-server/target/bigbang-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --akka.host=10.1.8.138   >>  /app/hadoop/small-bang/bigbang-server/bigbang-server.log  &

# curl  -s  安静模式

while :
do
	sleep 2 
	json="$(curl  -s "http://10.1.8.138:7707/server/hello" )"
	result=$(echo $json | grep "hello")
	if [ "$result" == "" ]
    then
       echo "the result is NOT ready"
       continue
    fi
    break
done 

echo "the result is ready"

echo "============START WORKER-AGENT============"
ps -ef | egrep "bigbang-worker-agent" | grep -v "grep" | awk '{print $2}' | xargs kill -9

echo "============START WORKER-AGENT============"
    java -jar  /app/hadoop/small-bang/bigbang-worker-agent/target/bigbang-worker-agent-0.0.1-SNAPSHOT.jar -i 10.1.8.138 -p 25000 -s 10.1.8.138:7707 -n bigdata  >>  /app/hadoop/small-bang/bigbang-worker-agent/bigbang-worker-agent.log  &

```

