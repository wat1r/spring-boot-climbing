## Linux命令漫谈





#### `grep -v grep`

> 目的：去除包含grep的进程行 ，避免影响最终数据的正确性

- `grep` 是查找含有指定文本行的意思，比如`grep test` 就是查找含有`test`的文本的行
- `grep -v`是反向查找的意思，比如` grep -v grep` 就是查找不含有` grep` 字段的行

 我们想要找出哪个进程的`id`

可以使用如下语段：

```shell
ps -ef | grep "mongod" | grep -v "grep" | awk '{print $2}'
```

杀掉有关`swoole`得进程：

```shell
ps -aux|grep "swoole"|awk '{print "kill -9 "$2}'
```

杀掉flink的关联服务

```shell
ps -ef | egrep "StandaloneSessionClusterEntrypoint|TaskManagerRunner" | grep -v "grep" | awk '{print $2}' | xargs kill -9
```





#### `grep` `egrep` `fgrep`区别

```shell
# grep:
传统的 grep 程序, 在没有参数的情况下, 只输出符合 RE 字符串之句子. 常见参数如下:
-v: 逆反模示, 只输出"不含" RE 字符串之句子.
-r: 递归模式, 可同时处理所有层级子目录里的文件.
-q: 静默模式, 不输出任何结果(stderr 除外. 常用以获取 return value, 符合为 true, 否则为false .)
-i: 忽略大小写.
-w: 整词比对, 类似 <word> .
-n: 同时输出行号.
-c: 只输出符合比对的行数.
-l: 只输出符合比对的文件名称.
-o: 只输出符合 RE 的字符串. (gnu 新版独有, 不见得所有版本都支持.)
-E: 切换为 egrep .

# egrep:
为 grep 的扩充版本, 改良了许多传统 grep 不能或不便的操作. 比方说:
\- grep 之下不支持 ? 与 + 这两种 modifier, 但 egrep 则可.
\- grep 不支持 a|b 或 (abc|xyz) 这类"或一"比对, 但 egrep 则可.
\- grep 在处理 {n,m} 时, 需用 { 与 } 处理, 但 egrep 则不需.
诸如此类的... 我个人会建议能用 egrep 就不用 grep 啦... ^_^

# fgrep:
不作 RE 处理, 表达式仅作一般字符串处理, 所有 meta 均失去功能.
```



#### `join` 命令

- -a<1或2> 除了显示原来的输出内容之外，还显示指令文件中没有相同栏位的行。
- -o<格式> 按照指定的格式来显示结果。
- -t<字符> 使用栏位的分隔字符。

两个文件：`month_cn.txt `  ,`month_en.txt `

```shell
# cat month_cn.txt 
1       一月
2       二月
3       三月
4       四月
5       五月
6       六月
7       七月
8       八月
9       九月
10      十月
11      十一月
12      十二月
13      十三月
```

```shell
# cat month_en.txt 
1       January
2       February
3       March
4       April
5       May
6       June
7       July
8       August
9       September
10      October
11      November
12      December
14      MonthUnknown
```

##### 执行 `join month_cn.txt month_en.txt `

```shell
# join month_cn.txt month_en.txt 
1 一月 January
2 二月 February
3 三月 March
4 四月 April
5 五月 May
6 六月 June
7 七月 July
8 八月 August
9 九月 September
10 十月 October
11 十一月 November
12 十二月 December
```

##### 执行`join -a1 month_cn.txt month_en.txt  ` 

> 左连接，显示左边文件的所有记录，右边的没有匹配的显示空白

```shell
# join -a1 month_cn.txt month_en.txt 
1 一月 January
2 二月 February
3 三月 March
4 四月 April
5 五月 May
6 六月 June
7 七月 July
8 八月 August
9 九月 September
10 十月 October
11 十一月 November
12 十二月 December
13 十三月
```

##### 执行`join -a2 month_cn.txt month_en.txt  ` 

> 右连接，显示右边文件的所有记录，左边没有匹配的显示空白

```shell
# join -a2 month_cn.txt month_en.txt 
1 一月 January
2 二月 February
3 三月 March
4 四月 April
5 五月 May
6 六月 June
7 七月 July
8 八月 August
9 九月 September
10 十月 October
11 十一月 November
12 十二月 December
14 MonthUnknown
```

##### 执行`join -a1  -a2 month_cn.txt month_en.txt `

> 全连接，显示左边和右边所有记录

```shell
# join -a1  -a2 month_cn.txt month_en.txt 
1 一月 January
2 二月 February
3 三月 March
4 四月 April
5 五月 May
6 六月 June
7 七月 July
8 八月 August
9 九月 September
10 十月 October
11 十一月 November
12 十二月 December
13 十三月
14 MonthUnknown
```

##### 输出指定字段

```shell
# join -o 1.1 2.2 1.2 month_cn.txt month_en.txt 
1 January 一月
2 February 二月
3 March 三月
4 April 四月
5 May 五月
6 June 六月
7 July 七月
8 August 八月
9 September 九月
10 October 十月
11 November 十一月
12 December 十二月
```







### Reference

- https://www.cnblogs.com/applelife/p/10481537.html