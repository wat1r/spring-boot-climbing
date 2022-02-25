# Flink性能调优笔记









```shell
bin/flink run \ -t yarn-per-job \ -d \ -p 5 \ -Drest.flamegraph.enabled=true \ -Dyarn.application.queue=test \ -Djobmanager.memory.process.size=1024mb \ -Dtaskmanager.memory.process.size=4096mb \
```





13看完





# Reference

- [【尚硅谷】大数据Flink2.0调优，Flink性能优化](https://www.bilibili.com/video/BV1Q5411f76P?p=5)

- 