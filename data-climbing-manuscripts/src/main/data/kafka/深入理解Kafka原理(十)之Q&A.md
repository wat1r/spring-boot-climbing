# 深入理解Kafka原理(十)之Q&A





### 什么原因导致副本与`leader`不同步？

- 慢副本：在一定周期时间内follower不能追赶上leader。最常见的原因之一是I/O瓶颈导致follower追加复制消息速度慢于从leader拉取速度。
- 卡住副本：在一定周期时间内follower停止从leader拉取请求。follower replica卡住了是由于GC暂停或follower失效或死亡。
- 新启动副本：当用户给主题增加副本因子时，新的follower不在同步副本列表中，直到他们完全赶上了leader日志。

  一个partition的follower落后于leader足够多时，被认为不在同步副本列表或处于滞后状态。现在kafka判定落后有两种，副本滞后判断依据是副本落后于leader最大消息数量(replica.lag.max.messages)或replicas响应partition leader的最长等待时间(replica.lag.time.max.ms)。前者是用来检测缓慢的副本,而后者是用来检测失效或死亡的副本。





//TODO

### 如何保证消息不被重复消费？（如何保证消息消费时的幂等性）

  要确定Kafka的消息是否丢失或重复，从两个方面分析入手：消息发送和消息消费

1、消息发送

Kafka消息发送有两种方式：同步（sync）和异步（async），默认是同步方式，可通过producer.type属性进行配置。Kafka通过配置request.required.acks属性来确认消息的生产：

```java
0---表示不进行消息接收是否成功的确认；
1---表示当Leader接收成功时确认；
-1---表示Leader和Follower都接收成功时确认；
```

综上所述，有6种消息生产的情况，下面分情况来分析消息丢失的场景：

```java
（1）acks=0，不和Kafka集群进行消息接收确认，则当网络异常、缓冲区满了等情况时，消息可能丢失；
（2）acks=1、同步模式下，只有Leader确认接收成功后但挂掉了，副本没有同步，数据可能丢失；
```

2、消息消费

  Kafka消息消费有两个consumer接口，Low-level API和High-level API：

```java
Low-level API：消费者自己维护offset等值，可以实现对Kafka的完全控制；
High-level API：封装了对parition和offset的管理，使用简单；
```

如果使用高级接口High-level API，可能存在一个问题就是当消息消费者从集群中把消息取出来、并提交了新的消息offset值后，还没来得及消费就挂掉了，那么下次再消费时之前没消费成功的消息就“诡异”的消失了；    

解决办法：

针对消息丢失：同步模式下，确认机制设置为-1，即让消息写入Leader和Follower之后再确认消息发送成功；异步模式下，为防止缓冲区满，可以在配置文件设置不限制阻塞超时时间，当缓冲区满时让生产者一直处于阻塞状态；

针对消息重复：将消息的唯一标识保存到外部介质中，每次消费时判断是否处理过即可。       

Kafka的Leader选举机制

 Kafka将每个Topic进行分区Patition，以提高消息的并行处理，同时为保证高可用性，每个分区都有一定数量的副本 Replica，这样当部分服务器不可用时副本所在服务器就可以接替上来，保证系统可用性。在Leader上负责读写，Follower负责数据的同步。当一个Leader发生故障如何从Follower中选择新Leader呢？

Kafka在Zookeeper上针对每个Topic都维护了一个ISR（in-sync replica---已同步的副本）的集合，集合的增减Kafka都会更新该记录。如果某分区的Leader不可用，Kafka就从ISR集合中选择一个副本作为新的Leader。这样就可以容忍的失败数比较高，假如某Topic有N+1个副本，则可以容忍N个服务器不可用。

如果ISR中副本都不可用，有两种处理方法：

```java
（1）等待ISR集合中副本复活后选择一个可用的副本；
（2）选择集群中其他可用副本；
```

Kafka的leader选举区分多种角色：

controller的选举
分区leader的选举
消费者group组内leader选举
