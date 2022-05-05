## Flink之环境搭建与HelloWorld





hadoop168上

```shell
发消息
[root@hadoop168 bin]# nc -lk 7777

启动jar
[root@hadoop168 flink-1.12.7]# ./bin/flink run -c com.frankcooper.wc.StreamWordCount  -p 2 /opt/data/jar/FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar  --host 192.168.168.168 --port 7777

提交的log
Job has been submitted with JobID a4b001edc0acdecdff8a9e3f7404608e

停止一个job
[root@hadoop168 flink-1.12.7]# ./bin/flink list
Waiting for response...
------------------ Running/Restarting Jobs -------------------
04.05.2022 15:24:36 : a4b001edc0acdecdff8a9e3f7404608e : stream word count go (RUNNING)
--------------------------------------------------------------
No scheduled jobs.
[root@hadoop168 flink-1.12.7]# ./bin/flink cancel a4b001edc0acdecdff8a9e3f7404608e
Cancelling job a4b001edc0acdecdff8a9e3f7404608e.
Cancelled job a4b001edc0acdecdff8a9e3f7404608e.
[root@hadoop168 flink-1.12.7]# ./bin/flink list
Waiting for response...
No running jobs.
No scheduled jobs.
[root@hadoop168 flink-1.12.7]#
```



Flink task的执行日志

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220504152651.png)





kafka写数据Flink读取执行并运算

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220504181019.png)











### Reference

- https://github.com/zhisheng17/flink-learning
