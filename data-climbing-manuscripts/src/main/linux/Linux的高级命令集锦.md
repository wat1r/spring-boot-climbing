## Linux的高级命令集锦







---

## 1.awk

### 基础知识

```
AWK是一种处理文本文件的语言，是一个强大的文本分析工具。

之所以叫AWK是因为其取了三位创始人 Alfred Aho，Peter Weinberger, 和 Brian Kernighan 的 Family Name 的首字符。

语法
awk [选项参数] 'script' var=value file(s)
或
awk [选项参数] -f scriptfile var=value file(s)
选项参数说明：

-F fs or --field-separator fs
指定输入文件折分隔符，fs是一个字符串或者是一个正则表达式，如-F:。
-v var=value or --asign var=value
赋值一个用户定义变量。
-f scripfile or --file scriptfile
从脚本文件中读取awk命令。
-mf nnn and -mr nnn
对nnn值设置内在限制，-mf选项限制分配给nnn的最大块数目；-mr选项限制记录的最大数目。这两个功能是Bell实验室版awk的扩展功能，在标准awk中不适用。
-W compact or --compat, -W traditional or --traditional
在兼容模式下运行awk。所以gawk的行为和标准的awk完全一样，所有的awk扩展都被忽略。
-W copyleft or --copyleft, -W copyright or --copyright
打印简短的版权信息。
-W help or --help, -W usage or --usage
打印全部awk选项和每个选项的简短说明。
-W lint or --lint
打印不能向传统unix平台移植的结构的警告。
-W lint-old or --lint-old
打印关于不能向传统unix平台移植的结构的警告。
-W posix
打开兼容模式。但有以下限制，不识别：/x、函数关键字、func、换码序列以及当fs是一个空格时，将新行作为一个域分隔符；操作符**和**=不能代替^和^=；fflush无效。
-W re-interval or --re-inerval
允许间隔正则表达式的使用，参考(grep中的Posix字符类)，如括号表达式[[:alpha:]]。
-W source program-text or --source program-text
使用program-text作为源代码，可与-f命令混用。
-W version or --version
打印bug报告信息的版本。
```

+ 变量与说明
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191212141755986.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dhdDFy,size_16,color_FFFFFF,t_70)




+ 查询 `interventions.txt `文件的制定的第三个字段的类型
  `awk -F'|' '  {if($3 ~"Drug|Other|Biological|Genetic") {print $2,$3 }}' interventions.txt OPS="\t"  | less `

+ 查询`interventions.txt `的各个统计
  `awk  -F'|' 'NR!=1{a[$3]++;} END {for (i in a) print i ", " a[i];}' interventions.txt`

+ 对文件的按`.`分隔后取第一个，去重后输出，源数据举例 `dw.XXXXX`  `dm.XXXXX`
  `awk -F'.' '{print $1}' hive_tables.txt | awk '!x[$0]++' | less`

+ 对文件按`.`分隔够取第一个，去重后输出

```shell
$ awk -F'.' '{a[$1]++;} END {for (i in a ) print i ", " a[i];}' hive_tables.txt
dm, 642
dw, 14338
```

+ 根据`"` 分割，取字段 ：`awk -F'"' '{print $6}'   cartridge_msg  | head -7`
+ 看看统计每个用户的进程的占了多少内存（注：sum的RSS那一列)
  `ps aux | awk 'NR!=1{a[$1]+=$6;} END { for(i in a) print i ", " a[i]"KB";}'`

+ awk去重：`awk '!x[$0]++' file1awk > file2`

+ sh脚本中杀死进程：`ps -ef | grep java | grep -v consumer |awk '{print $2}' | xargs -p kill -9 `

+ 查询行数相关：` awk 'FNR==16000' file `

+ 查询需要的信息并且加上第一行:`awk  'NR==1;/205613/{print}' Submissions.txt`

+ 查看所有行的记录:` awk '//{print}' passwd.txt`
+ 过滤到`localhost`:` awk '/localhost/{print}' /etc/hosts `
+ 按`~` 分隔后，取field后，去重：`awk -F'~' '!($12 in a){a[$12];print $12}' products_1.txt`
+ 按`~` 分隔后，取field后，去重（去掉第一行）： `awk -F'~' '{if(NR!=1 && !($12 in a) ) {a[$12];print $12}}' products_1.txt`
  `funtions.awk`：计算从1到100的和

```shell
#! /bin/awk -f
#add
function Add(firstNum, secondNum) {
    sum = 0
    for (i = firstNum; i <= secondNum; i++) {
        sum = sum + i;
    }
    return sum
}

function main(num1, num2) {
    result = Add(num1, num2)
    print "Sum is:", result
}

#execute
function
BEGIN {
    main(1, 100)
}
```

`passwd.awk`:以`:` 分割，打印出想要的`field`,`$NF`表示选定每一行的最后一列

```shell
#! /bin/awk -f 

BEGIN {FS=":"} 
/root/ {print "Username is:"$1,"UID is:"$3,"GID is:"$4,"Shell is:"$NF} 

```

`passwd.txt`:文件截取，
执行命令:` awk -f  passwd.awk passwd.txt` OR`  ./passwd.awk passwd.txt`

```
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin

```

---

## 2.sed

### 基础知识

```shell
语法
sed [-hnV][-e<script>][-f<script文件>][文本文件]
参数说明：

-e<script>或--expression=<script> 以选项中指定的script来处理输入的文本文件。
-f<script文件>或--file=<script文件> 以选项中指定的script文件来处理输入的文本文件。
-h或--help 显示帮助。
-n或--quiet或--silent 仅显示script处理后的结果。
-V或--version 显示版本信息。
动作说明：

a ：新增， a 的后面可以接字串，而这些字串会在新的一行出现(目前的下一行)～
c ：取代， c 的后面可以接字串，这些字串可以取代 n1,n2 之间的行！
d ：删除，因为是删除啊，所以 d 后面通常不接任何咚咚；
i ：插入， i 的后面可以接字串，而这些字串会在新的一行出现(目前的上一行)；
p ：打印，亦即将某个选择的数据印出。通常 p 会与参数 sed -n 一起运行～
s ：取代，可以直接进行取代的工作哩！通常这个 s 的动作可以搭配正规表示法！例如 1,20s/old/new/g 就是啦！
```

- 查看文件的第1到第5行：`sed -n '1,5p' passwd.txt`
- 查看文件的第1到结尾：`sed -n '1,$p' passwd.txt`
- 去除文件的空行：`sed -i '/^$/d' passwd.txt`  慎用   `-i `  ，原地修改
- 去除文件的空行：`cat passwd.txt | awk '{if($0!="") print }' ` 
- 在文件的每行结尾处添加字符串:`sed -i 's/$/_MODIFY/'  file`
- 在文件的每行开头处添加字符串:`sed -i  's/^/HEAD_/g' passwd.txt`
- 替换文件的`_MODIFY`为`_UPDATE`：` sed -e  's/_MODIFY/_UPDATE/' passwd.txt  > passwd_update.txt`
- 替换文件的字符串：` sed -i "s/my/Hao Chen's/g" pets.txt   `   其中 `/g` 是全局模式表示每一行出现的符合规则的都被替换掉
- 删除第2行到末尾行：`sed '2,$d' my.txt`



> 找到access.log文件中/req/v2/fetch的 请求，拿到7 14 15列，然后按 "切分， 选取大于1s的输出

```shell
cat access.log | awk -F' ' '/req\/v2\/fetch/{print $7, $14, $NF}'   | awk -F '"' '$4>1' | awk -F' ' '{print $0}' | less
```





```shell
cat hiveassistant3-server-prod-https_access.log | awk -F' ' '/job\/engine\/result/{print $7}' | less  > ../hadoop/hive_server_access_job_engine_result.log
```





```shell
 awk '/(jobid=)(.*)(&que)/{print $1}' hive_server_access_job_engine_result.log | less
```





提取两个字符之间的字符串

```shell
 sed -E 's/.*jobid=(.*)&id=.*/\1/' hive_server_access_job_engine_result.log | less
```





去重

```shell
awk '!x[$0]++' hive_server_access_job_engine_result_job_id_repeative.log > hive_server_access_job_engine_result_job_id.log
```





```shell
sed -E 's/.*\/tasks\/req\/v2\/fetch\/(.*)\?token=.*/\1/' access_august_prod_more_than_2_seconds.log | less

sed -n '/tasks\/req\/v2\/fetch\/ \?token=/p' access_august_prod_more_than_2_seconds.log | less

sed -E 's/.*\/tasks\/req\/v2\/fetch\/(.*)\?token=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<20'  > seg_1.txt
sed -E 's/.*\/req\/v2\/fetch\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<20'  > seg_2.txt
sed -E 's/.*\/req\/v2\/fetch\/hive\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)=36'  > seg_3.txt
sed -E 's/.*\/req\/v2\/fetch\/hive\/page\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<=36'  > seg_4.txt
```





---

## 3.nl

- NONE

----





## 4.compress/uncompress

 .**tar.gz** 使用tar命令进行解压

```
 tar -zxvf java.tar.gz

解压到指定的文件夹
	tar -zxvf java.tar.gz  -C /usr/java
```

**gz**文件的解压 gzip 命令

```
 	gzip -d java.gz
```

也可使用**zcat** 命令，然后将标准输出 保存文件

```
zcat java.gz > java.java
```



## 9.查询文件(du)

- 查找top10的文件或是文件夹

```shell
du : 计算出单个文件或者文件夹的磁盘空间占用.
sort : 对文件行或者标准输出行记录排序后输出.
head : 输出文件内容的前面部分.
du：
-a：显示目录占用空间的大小，还要显示其下目录占用空间的大小
sort：
-n  : 按照字符串表示的数字值来排序
-r ：按照反序排列
head :
-n : 取出前多少行

以上的问题可以使用命令： du -a | sort -n -r | head -n 10
```

- 查找大于固定值的大文件

```shell
find -type f -size +100M  -print0 | xargs -0 du -h | sort -nr
```

```shell
 find / -type f -size +1024M  -print0 | xargs -0 du -h | sort -nr
```







---


## 10.net|port

- 查看端口使用情况:`lsof -i:8080`

- ping通ip地址：`ping github.com`

- 测试ip对应的端口是否开启:`telnet 140.82.113.3 22 `

- 提示不是内部命令的报错信息的话，windows系统的话可能需要开启`telnet`服务

- 命令查看正在运行状态的服务及端口:`netstat -tunpl`

- 使用cmd命令查看端口号占用情况，例如查看端口 8014，可以看出进程号为10728；

  ```shell
  netstat -ano | findstr 端口号
  ```

- `netstat -nupl`(UDP类型的端口)

- `netstat -ntpl` (TCP类型的端口)

- 使用 lsof 命令来查看某一端口是否开放。查看端口可以这样来使用，我就以80端口为例：
  `lsof -i:80`

- 查询端口占用：`netstat -ano` 或 `netstat -ano | grep 8080`

- 查询这个端口的`PID`被哪个进程占用:`tasklist|findstr "9088"`

-  直接强制杀死指定端口:`taskkill /pid 4136 -t -f`

```shell
 taskkill //F  //pid 2236
成功: 已终止 PID 为 2236 的进程。
```

- 查看默认端口：`ss -lntp`
- 通过端口号找到服务名

```shell
[root@centos7 flink]# netstat -antup | grep 8080
tcp        0      0 0.0.0.0:8080            0.0.0.0:*               LISTEN      15502/java          
[root@centos7 flink]# ll /proc/15502/cwd 
lrwxrwxrwx 1 root root 0 Sep 30 10:47 /proc/15502/cwd -> /usr/local/spark
```

- 查看详细信息

```java
[root@centos7 flink]# netstat -nltp
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 0.0.0.0:42214           0.0.0.0:*               LISTEN      11024/java          
tcp        0      0 0.0.0.0:46727           0.0.0.0:*               LISTEN      11310/java      
```

- 查看某进程占用的端口号

```java
 netstat -anp
 [root@www ~]# netstat -anp | grep syslog
 udp    0   0 0.0.0.0:514         0.0.0.0:*                31483/syslogd 
```

- 查看某端口被占用的进程

```java
 [root@nbatest ~]# netstat -altp |grep 9999
 tcp    0   0 0.0.0.0:9999        0.0.0.0:*          LISTEN   16315/gate_applicat
```

- 得到PID后，使用netstat命令查询端口占用

```
netstat -nap | grep [pid]
```









## 11.ls命令

```shell
$ ls -lhS
//ls -lhS  按照由大到小排序
//ls -Slhr   按照从小到大排序
```





## 18.ssh

```shell
//指定端口号登录
ssh   hadoop@116.XXX.20.XXX:58422
```







## 19.防火墙

---

```shell
# 1:查看防火状态
systemctl status firewalld
service  iptables status

# 2:暂时关闭防火墙
systemctl stop firewalld
service  iptables stop

# 3:永久关闭防火墙
systemctl disable firewalld
chkconfig iptables off

# 4:重启防火墙
systemctl enable firewalld
service iptables restart


# 永久性打开某一端口
[root@centos7 elasticsearch-head]#  firewall-cmd --zone=public --add-port=9100/tcp --permanent
# 查看端口开启状态
[root@centos7 elasticsearch-head]# firewall-cmd --query-port=9100/tcp
no
# 重启防火墙
[root@centos7 elasticsearch-head]# firewall-cmd --reload

```







## 19.find

> 在使用find ／ -name 时，会打印很多含“permission denied”错误无用信息
>
> 意思是把 标准错误输出 重定向到 标准输出，grep -v 的意思是“获取相反”，具体参考grep命令

```shell
find / -name art  2>&1 | grep -v "Permission denied"
```





https://blog.csdn.net/feit2417/article/details/82935801

## 20.Maven命令

- 输出Maven项目的目录结构:`tree >> D:/tree.txt`
- 输出maven依赖结构：`mvn dependency:tree >D:/aa.txt `
- 跳过测试构建:`mvn clean install -DskipTests`





## 21.Xshell修改配色

- 临时性修改`grep`结果，下面是部分配色：

```
0 black
31 red
32 green
33 yellow
34 blue
35 purple
36 cyan
37 white
```

```shell
// vim ~/.bash_profile
$ export GREP_OPTIONS='--color=auto' GREP_COLOR='31'
$ ps axu | grep java
```



---

## 50.番外

+ 输出文件的行数的几种方法：
  + ` awk 'END{print NR}' Applications.txt `	 
  + `wc -l test1.sh`
  + `sed -n '$=' Applications.txt`
+ 清空文件:`echo -n >  file`
+ 查询目录下文件的个数：`ls -lh| grep -c "^-"`
+ 比较 A，B文件的差集：`awk 'NR==FNR{ a[$1]=$1 } NR>FNR{ if(a[$1] == ""){ print $1}}'   delete_file_total_20200102_uniq   result_total.txt> file2.txt`









## 60.Macos

- 编辑文件用sudo权限打开，qa!退出编辑

```shell
➜  bin cat /etc/paths
/usr/local/bin
/usr/bin
/bin
/usr/sbin
/sbin
/Applications/Docker.app/Contents/Resources/bin
```

- 找不到命令`zsh: command not found: docker`

```shell
vim .zshrc
```



### 60.1.Redis

```shell
➜  ~ redis-server /usr/local/etc/redis.conf 
4800:C 24 Apr 18:21:05.53  ....
➜  ~ redis-cli
```

- 常用命令

```shell
# 返回存在的keys
keys * 
```







### 60.2.Docker 

> 参考文档https://www.runoob.com/docker/macos-docker-install.html

需要先启动Docker的Server 即图形化界面



```shell
➜  ~ docker --version
Docker version 20.10.5, build 55c4c88


➜  ~ docker info
Client:
 Context:    default
 Debug Mode: false
 Plugins:
  app: Docker App (Docker Inc., v0.9.1-beta3)
  buildx: Build with BuildKit (Docker Inc., v0.5.1-docker)
  scan: Docker Scan (Docker Inc., v0.6.0)

Server:
 Containers: 0
  Running: 0
  Paused: 0
  Stopped: 0
 Images: 0
 Server Version: 20.10.5
 Storage Driver: overlay2
  Backing Filesystem: extfs
  Supports d_type: true
  Native Overlay Diff: true
 Logging Driver: json-file
 Cgroup Driver: cgroupfs
 Cgroup Version: 1

```





设置镜像加速

可以配置多个镜像

```shell
"registry-mirrors":["http://hub-mirror.c.163.com"]
```



![image-20210424184507859](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210424184507859.png)

```shell
~ docker info
Registry Mirrors:
  http://hub-mirror.c.163.com/
 Live Restore Enabled: false
```





### 60.3.ClickHouse

> 基础知识参考链接：https://www.jianshu.com/p/250e9d9788f2

安装ClickHouse镜像

```shell
docker pull yandex/clickhouse-client
docker pull yandex/clickhouse-server
```

启动容器服务，加载镜像

```shell
docker run -d --name ck-server --ulimit nofile=262144:262144  -p 8123:8123 -p 9000:9000 -p 9009:9009 --volume=$HOME/Documents/ck2_database:/var/lib/clickhouse yandex/clickhouse-server
#上面的这条语句没成功，用下面的这个
docker run -d --name ch-server --ulimit nofile=262144:262144 -p 8123:8123 -p 9000:9000 -p 9009:9009 yandex/clickhouse-server
```

volume：冒号两侧的路径建立映射，当容器服务读取冒号后面的虚拟机内路径时，会去读冒号前面的本机路径。加这个参数的作用是自定义配置，这个参数可以比较简单的修改部分配置。

-p:暴露容器中的端口到本机端口中。本机端口：容器端口。不配置的话可以后面除来虚拟机中，别的地方连不上8123端口。
查看启动情况

```shell
➜  Resources docker ps
CONTAINER ID   IMAGE                      COMMAND            CREATED          STATUS          PORTS                                                                    NAMES
2864fdf80b8f   yandex/clickhouse-server   "/entrypoint.sh"   56 seconds ago   Up 52 seconds   0.0.0.0:8123->8123/tcp, 0.0.0.0:9000->9000/tcp, 0.0.0.0:9009->9009/tcp   ch-server
➜  Resources
```

进入容器校验一下

```shell
➜  Resources docker exec -it ch-server /bin/bash
root@2864fdf80b8f:/# clickhouse-client
ClickHouse client version 21.4.4.30 (official build).
Connecting to localhost:9000 as user default.
Connected to ClickHouse server version 21.4.4 revision 54447.

2864fdf80b8f :) show databases;

SHOW DATABASES

Query id: dc6cd7d7-c423-4ffd-90fc-46ff0219fa36

┌─name────┐
│ default │
│ system  │
└─────────┘

2 rows in set. Elapsed: 0.003 sec.

2864fdf80b8f :)
```





一些测试命令

```shell
CREATE TABLE arrays_test (s String, arr Array(UInt8)) ENGINE = Memory

INSERT INTO arrays_test VALUES ('Hello', [1,2]), ('World', [3,4,5]), ('Goodbye', [])

SELECT * FROM arrays_test

select s , arr from arrays_test ARRAY join arr

#制定别名
SELECT s, arr, a FROM arrays_test ARRAY JOIN arr AS a

SELECT s, arr, a, num, mapped FROM arrays_test ARRAY JOIN arr AS a, arrayEnumerate(arr) AS num, arrayMap(x -> x + 1, arr) AS mapped
 


```



### 60.4.Mysql

> 查看mysql的版本信息

```shell
➜  ~ brew  info mysql 
mysql: stable 8.0.23 (bottled)
Open source relational database management system
https://dev.mysql.com/doc/refman/8.0/en/
Conflicts with:
  mariadb (because mysql, mariadb, and percona install the same binaries)
  percona-server (because mysql, ma
  
安装5.7版本mysql
➜  ~ brew install mysql@5.7

```

上面的安装失败了采用dmg的方式安装

```shell
2021-04-24T11:48:20.760596Z 1 [Note] A temporary password is generated for root@localhost: !103t(S-u6%=

If you lose this password, please consult the section How to Reset the Root Password in the MySQL reference manual.
```

查看安装版本

```shell
➜  bin mysql -V
mysql  Ver 14.14 Distrib 5.7.29, for macos10.14 (x86_64) using  EditLine wrapper
➜  bin
```

启动mysql

![image-20210424201258050](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210424201258050.png)



修改密码

```java
mysql> alter user 'root'@'localhost' identified by 'root';
mysql> update mysql.user set authentication_string=PASSWORD('123456') where user='root';


```





### 60.5.brew



改成中科大的源：

```shell
# 进入 brew 的仓库根目录
cd "$(brew --repo)"

# 修改为中科大的源
git remote set-url origin https://mirrors.ustc.edu.cn/brew.git
```

同理，修改 homebrew-cask、homebrew-core、homebrew-services 的远程仓库地址

```shell
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-cask"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-cask.git

cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
```

修改完仓库地址后，更新一下，加上 `-v` 参数可以看到当前跑的进度：

```shell
brew update -v
```

http://www.manongjc.com/detail/15-jxncezljoldhtop.html







### 60.6.Kafka

启动
启动zookeeper
kafka是基于zookeeper的，启动kafka之前，需要先启动zookeeper

 ```shell
/usr/local/Cellar/kafka/3.7.0/bin/zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &


/usr/local/etc/zookeeper
这个目录下
/usr/local/Cellar/zookeeper/3.7.0/bin
启动/停止
./zkServer start
./zkServer stop
ps aux | grep zookeeper 查看进程
或
➜  bin jps
17381 Jps
357
17305 QuorumPeerMain




 ```

https://blog.csdn.net/weixin_33207551/article/details/86521905



安装kafka

```shell
brew install kafka
/usr/local/Cellar/kafka/2.8.0/bin

./kafka-server-start /usr/local/etc/kafka/server.properties &

检测是否启动成功
ps aux | grep kakfka
```



https://www.cnblogs.com/mysticbinary/p/13848497.html







## Reference

- [Linux中查询当前用户的命令总结](https://blog.csdn.net/csdn1198402320/article/details/84335639)
- [SED 简明教程](https://coolshell.cn/articles/9104.html)
- [awk 使用教程 - 通读篇（30分钟入门](https://cloud.tencent.com/developer/article/1159061)
- [AWK 简明教程](https://coolshell.cn/articles/9070.html)
- [Linux Shell常用技巧(四) awk](https://www.cnblogs.com/mchina/archive/2012/06/30/2571308.html)
- [每天一个linux命令(11)：nl命令](https://www.cnblogs.com/peida/archive/2012/11/01/2749048.html)
- [Linux 查询端口被占用命令](https://www.cnblogs.com/ming-blogs/p/11101423.html)
- [Xshell-grep高亮显示匹配项](https://www.jianshu.com/p/9944f96ea378)
- https://www.cnblogs.com/weifeng1463/p/9858048.html
- https://www.cnblogs.com/tianyajuanke/archive/2012/04/25/2470002.html

- [linux下查找java进程所在的目录](https://blog.csdn.net/glw0223/article/details/88823513)