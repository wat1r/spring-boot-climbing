# 大数据组件版本与CLI

## 1.Hive

```shell
Hive自带函数
>show functions; -------查看hive所有自带函数
>desc function extended 函数名；------通过一个简单的例子告诉你怎样使用这个函数
```







## 2.HBase



```shell

scan 'abc', { COLUMN => 'a:b', TIMERANGE => [1634101200000, 1634223600000]}"

扫描所有数据,并用TIMERANGE限制,表示的是”>=开始时间 and <结束时间“
scan 'scores',{TIMERANGE=>[1599461946117, 1667892773000]}

扫描所有数据,只显示某列,并指定时间戳
scan 'scores', {COLUMNS => 'grade', TIMESTAMP=> 1667892773000}



list ‘abc.*’ #显示abc开头的表

#查询表是否存在
exists 'table_name'

```







[HBase命令操作大全](https://blog.csdn.net/xiaoxaoyu/article/details/111312468)

[Hbase shell命令使用 get，scan，时间戳，过滤器，版本等参数详细使用说明](https://blog.csdn.net/qq_41712271/article/details/108465612)







# Reference

[hadoop hbase hive spark对应版本](https://www.tqwba.com/x_d/jishu/73706.html)

[Hive数据导出的几种方式](https://www.cnblogs.com/sheng-sjk/p/13940642.html)