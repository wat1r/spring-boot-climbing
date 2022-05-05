# Shell脚本汇总

## 0.windows开机启动程序`start.bat`

```java
win10开机启动文件夹路径是什么：
1、路径：【C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp】；
2、快捷命令：按下【win+R】打开运行输入：【shell:Common Startup】；
```

```bash
@echo off
start "" "D:\Dev\JetBrains\IntelliJ IDEA 2019.3.4\bin\idea64.exe"
start "" "D:\Dev\Microsoft VSCode\Code.exe"
start "" "D:\Program Files (x86)\Notepad++\notepad++.exe"
start "" "D:\Program Files (x86)\Typora\Typora.exe"
start "" "D:\Program Files (x86)\NetSarang\Xshell.exe"
start "" "D:\Program Files (x86)\FastStone Capture\FSCapture.exe"
start "" "D:\Program Files (x86)\Navicat Premium 12\navicat.exe"
start "" "D:\Dev\DBeaver\dbeaver.exe"
start "" "D:\Dev\Git\git-bash.exe"
```

![image-20210602082432467](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\linux\Shell脚本汇总.assets\image-20210602082432467.png)

![image-20210602082849031](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\linux\Shell脚本汇总.assets\image-20210602082849031.png)

![image-20210602083630802](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\linux\Shell脚本汇总.assets\image-20210602083630802.png)

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





## 3.监控进程存活发送邮件

- 使用crontab :
  - `crontab -e `: `*/1 * * * *  sh /app/hadoop/scripts/detect_bigbang_server.sh`

```shell
#!/bin/sh
while true
do
ps -ef | egrep "bigbang-server" | grep -v "grep" 
if [ "$?" -eq 0 ] 
then
echo "[bigbang-server] ALIVE"
echo "[bigbang-server] ALIVE" >> /app/hadoop/scripts/test.txt
#crontab -r
else
echo "[bigbang-server] DEAD" >> /app/hadoop/scripts/test.txt
echo "mail -s 'test' xxx@163.com  <  /app/hadoop/scripts/test.txt"
crontab -r 
fi
sleep 10
done
```







## 4.修改dns

```shell
#!/bin/bash

states=$(echo 'list ".*DNS"' | scutil | awk '{print $NF}')

for state in $states
do
    dns_output=$(printf "d.init\nget ${state}\nd.show\nquit\n" | scutil)

    if echo "$dns_output" | grep -iq 210
    then
        echo -e "__INFO: $state NEED change:\n${dns_output}"
        echo "__INFO: $state UPDATED:"
        printf "d.init\nget ${state}\nd.remove ServerAddress\nd.add ServerAddresses * 210.22.70.225 210.22.70.3 192.168.102.81 192.168.102.82\nset ${state}\nd.show\nquit\n" | sudo scutil
    else
        :
    fi
done%
```

