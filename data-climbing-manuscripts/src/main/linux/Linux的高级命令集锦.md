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

判断一个文件中两个字段相同并筛选出：

```powershell
$ cat action_relation.txt | awk  -F'\t' '{if($2==$3) print}' | head
"658"   "669"   "669"   "-1"    "D"     "0"     "1"
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

- 查找当前文件夹下文件大小：

  ```shell
  $ du -sh *
  332K    $Recycle.Bin
  4.0K    AMTAG.BIN
  4.0K    Config.Msi
  0       Documents and Settings
  1.7G    Drivers
  
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





## 17.ssh

```shell
//指定端口号登录
ssh   hadoop@116.XXX.20.XXX:58422
```







## 18.防火墙

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



#### 查找linux下的java进程：

```shell
ps -ef | grep java
cd /proc/173957
ll cwd
[ 173957]$ ll cwd
lrwxrwxrwx 1 hadoop hadoop 0 May 14 14:06 cwd -> /app/hadoop/jerry/powerjob/server
```

```powershell
/proc/pid/cmdline 包含了用于开始进程的命令 ； -> cat cmdline 可以找到程序启动入口
/proc/pid/cwd包含了当前进程工作目录的一个链接 ；
/proc/pid/environ 包含了可用进程环境变量的列表 ；
/proc/pid/exe 包含了正在进程中运行的程序链接；
/proc/pid/fd/ 这个目录包含了进程打开的每一个文件的链接；
/proc/pid/mem 包含了进程在内存中的内容；
/proc/pid/stat包含了进程的状态信息；
/proc/pid/statm 包含了进程的内存使用信息。
```









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





## 22.screen

```shell
screen -S yourname -> 新建一个叫yourname的session
screen -ls         -> 列出当前所有的session
screen -r yourname -> 回到yourname这个session
screen -d yourname -> 远程detach某个session
screen -d -r yourname -> 结束当前session并回到yourname这个session

session下使用如下命令
ctrl+a(C-a) 
C-a d -> detach，暂时离开当前session，将目前的 screen session (可能含有多个 windows) 丢到后台执行，并会回到还没进 screen 时的状态，此时在 screen session 里，每个 window 内运行的 process (无论是前台/后台)都在继续执行，即使 logout 也不影响。 

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
- https://blog.csdn.net/glw0223/article/details/88823513
- https://blog.csdn.net/feit2417/article/details/82935801
- [Linux中的screen命令使用](https://blog.csdn.net/han0373/article/details/81352663)
- [用 echo 管道命令给sudo自动输入密码](https://blog.csdn.net/xushx_bigbear/article/details/12966625?%3E)