# Linux命令三剑客

## 1.awk

- 统计机器中网络连接各个状态个数

> ^tcp 表示以tcp开头的行，也就是tcp类型的网络连接
>
> $NF 表示的最后一个Field（列），即输出最后一个字段的内容
>
> ++S[$NF] 表示自增了
>
> { for(a in S ) print a, S[a] } 对结果进行for loop 给出状态的类型已经条数

```shell
$ netstat -a | awk '/^tcp/ {++S[$NF]}  END { for(a in S ) print a, S[a] } ' 
LISTEN 75
CLOSE_WAIT 38
ESTABLISHED 377
TIME_WAIT 144

xa
|sss|XX|
```





## 2.sed

> 把/etc/passwd中的root替换成liu并把所在行显示出来
> #s:替换匹配到的字符串
> #g:每行做全局匹配,否则每行只匹配遇到的第一个字符串
> #p:打印

```shell
# sed -n 's/root/liu/gp' /etc/passwd
liu:x:0:0:liu:/liu:/bin/bash
```

> 打印包含root字串的行

```shell
# sed -n '/root/p'  /etc/passwd
root:x:0:0:root:/root:/bin/bash
```







## 3.grep

[基础命令](https://www.runoob.com/linux/linux-comm-grep.html)

```shell
# grep -P
?=     表示询问后面跟着的东西是否等于这个。
?<=    表示询问是否以这个东西开头。
?!     表示询问后面跟着的东西是否不是这个。
?<!   表示询问是否不是以这个东西开头 。
```

举例

```shell
# cat sys.txt 
AFG
AFS
BAU
CdC
000823
ERU
jNNNA
IFI
entegor
jKL
NNN
MYSQL
QQQ
UUU
CCS
ONLINE
MPB
IFF

# grep -P 'C(?=d)' sys.txt 
CdC

# grep -P '(?<=C)' sys.txt 
CdC
CCS

# grep -P 'M(?!Y)' sys.txt 
MPB

# grep -P '(?<!O)N' sys.txt 
jNNNA
NNN
ONLINE

```



##### 显示IP地址

```shell
# ifconfig eth0 | grep -Po "(?<=inet).*(?=netmask)" 
 172.XXX.XXX.141  
```

##### 忽略文件中的多个关键字

```shell
# cat fileName | grep -v "key1"| grep -v "key2"| grep -v "key3" 
#即过滤掉fileName文件中的key1，key2和key3，必须同时满足所有条件才可过滤
```











### 实际案例

#### 分析nginx日志

`access.log`:

```log
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.051" "0.051"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.052" "0.052"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.078" "0.078"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.079" "0.079"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.085" "0.085"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.065" "0.065"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.081" "0.081"
10.1.9.112 - - [29/Sep/2020:00:00:01 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.079" "0.079"
10.1.9.112 - - [29/Sep/2020:00:00:02 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.080" "0.080"
10.1.9.112 - - [29/Sep/2020:00:00:02 +0800] "POST /api/pre/10177/schemaAction HTTP/1.1" 200 5 "-" "okhttp/3.14.2" "-" "0.087" "0.087"

```

##### 需求1：列出日志中访问最多的10个ip

> a[$1]++ 创建数组a，以第一列作为下标，使用运算符++作为数组元素，元素初始值为0。处理一个IP时，下标是IP，元素加1，处理第二个IP时，下标是IP，元素加1，如果这个IP已经存在，则元素再加1，也就是这个IP出现了两次，元素结果是2，以此类推
>
> sort 命令 中   -r 按倒序来排列熟悉， -n 按数值的大小排序  
>
>  -k 可以按多列进行排序    如sort -k 1 -k 2 test.txt，那么就会根据test.txt文件中的第一列和第二列进行排序

```shell
$  awk '{a[$1]++}END{for(i in a)print a[i],i | " sort -k1   -nr | head  -n10"}' access.log
72259 10.XXX.113.22
...
```

另外一种方法：

```shell
awk  '{print $1} ' access.log   | sort | uniq -c | sort -k1  -nr  | head -10
89721 10.XXX.113.22
...
```

##### 需求2：统计日志中访问次数超过1000次的ip

> 将结果保存a数组后，输出时判断符合要求的IP

```shell
$ awk '{ a[$1]++} END {for (i in a ){ if (a[i] > 1000) print i, a[i]}} ' access.log 
10.1.XXX.109 6717
```

另外一种方法：

> 是将结果保存a数组时，并判断符合要求的IP放到b数组，最后打印b数组的IP

```shell
awk '{a[$1]++;if(a[$1]>100){b[$1]++}}END{for(i in b){print i,a[i]}}' access.log
```

##### 需求3：统计访问最多的前10个页面（$request）

```shell
$ awk '{a[$7]++}END{for(i in a)print a[i],i|"sort -k1 -nr|head -n10"}' access.log
34453 /tasks/req/XXXX?token=XXXX
```

##### 需求4：统计每个IP访问状态码数量（$status）

> 1 是ip   9对应的是状态码 用 "--->"来连接   

```shell
$ awk '{a[$1"--->"$9]++} END {for( i in a) print i, a[i]}' access.log | head -10
10.1.XXX.109--->500 3
```

























### Reference

- https://www.cnblogs.com/276815076/p/6410179.html

- https://www.cnblogs.com/kevingrace/p/9299232.html