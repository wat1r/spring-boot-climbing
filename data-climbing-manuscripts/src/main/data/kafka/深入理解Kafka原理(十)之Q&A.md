# 深入理解Kafka原理(四)之Q&A





### 什么原因导致副本与`leader`不同步？

- 慢副本：在一定周期时间内follower不能追赶上leader。最常见的原因之一是I/O瓶颈导致follower追加复制消息速度慢于从leader拉取速度。
- 卡住副本：在一定周期时间内follower停止从leader拉取请求。follower replica卡住了是由于GC暂停或follower失效或死亡。
- 新启动副本：当用户给主题增加副本因子时，新的follower不在同步副本列表中，直到他们完全赶上了leader日志。

  一个partition的follower落后于leader足够多时，被认为不在同步副本列表或处于滞后状态。现在kafka判定落后有两种，副本滞后判断依据是副本落后于leader最大消息数量(replica.lag.max.messages)或replicas响应partition leader的最长等待时间(replica.lag.time.max.ms)。前者是用来检测缓慢的副本,而后者是用来检测失效或死亡的副本。