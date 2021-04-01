## 简介
`Kafka`是用`scala`语言编写，最初由`Linkedin`公司开发，后贡献给了`Apache`基金会并成为顶级开源项目。是一个分布式、支持分区的（`partition`）、多副本的（`replication`），基于zookeeper协调的分布式消息系统，它的最大的特性就是可以实时的处理大量数据以满足各种需求场景：比如基于`hadoop`的批处理系统、低延迟的实时系统、`storm/Spark`流式处理引擎，`web/nginx`日志、访问日志，消息服务等等。
`Kafka`是一个类`JMS`消息队列，结合了`JMS`中的两种模式，可以有多个消费者主动拉取数据。虽然它提供了类似于`JMS`的特性，但是在设计实现上完全不同，此外它并不是`JMS`规范的实现，在`JMS`中只有点对点模式才有消费者主动拉取数据。
特性
## 特性
- 高吞吐量、低延迟：`kafka`每秒可以处理几十万条消息，它的延迟最低只有几毫秒，每个`topic`可以分多个`partition`，`consumer group` 对 `partition` 进行`consume`操作。
- 可扩展性：`kafka`集群支持热扩展
- 持久性、可靠性：消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
- 容错性：允许集群中节点失败（若副本数量为`n`，则允许`n-1`个节点失败）
- 高并发：支持数千个客户端同时读写
- 顺序性：由生产者发送到一个特定的主题分区的消息将被以他们被发送的顺序来追加。也就是说，
  如果一个消息`M1`和消息`M2`都来自同一个生产者，`M1`先发，那么`M1`将有一个低于`M2`的偏移，会更早在日志中出现。
  消费者看到的记录排序就是记录被存储在日志中的顺序。
## 设计思想
### 动机
各种应用系统诸如商业、社交、搜索、浏览等像信息工厂一样不断的生产出各种信息，在大数据时代，我们面临如下几个挑战：
- 如何收集这些巨大的信息
- 如何分析它
- 如何及时做到如上两点
  以上几个挑战形成了一个业务需求模型，即生产者生产（`produce`）各种信息，消费者消费（`consume`）（处理分析）这些信息，而在生产者与消费者之间，需要一个沟通两者的桥梁-消息系统。从一个微观层面来说，这种需求也可理解为不同的系统之间如何传递消息
  1.`Kafka` 必须具有高吞吐量来支持高容量事件流，例如实时日志聚合；
  2.`Kafka` 需要能够正常处理大量的数据积压，以便能够支持来自离线系统的周期性数据加载；
  具有低延迟、分区、分布式、实时处理以及容错。
## 基础概念
#### Broker
`kafka` 集群由多个 `kafka` 实例组成，每个实例 (`server`) 称为 `broker` ，在集群中每个`broker`都有一个唯一`brokerid`，不得重复。 无论是 `kafka` 集群，还是 `producer` 和 consumer 都依赖于 `zookeeper` 来保证系统可用性，为集群保存一些 `meta` （元数据）信息。
#### Topics/Log
`Topic` 就是数据主题，是数据记录发布的地方,可以用来区分业务系统。`Kafka`中的`Topics`总是多订阅者模式，一个`topic`可以拥有一个或者多个消费者来订阅它的数据。
对于每一个`topic`， `Kafka`集群都会维持一个分区日志，如下所示：
![image-20210320135717817](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210320135717817.png)
每个分区都是有序且顺序不可变的记录集，并且不断地追加到结构化的`commit log`文件。分区中的每一个记录都会分配一个`id`号来表示顺序，我们称之为*offset*，*offset*用来唯一的标识分区中每一条记录。
`Kafka` 集群保留所有发布的记录—无论他们是否已被消费—并通过一个可配置的参数——保留期限来控制. 举个例子， 如果保留策略设置为2天，一条记录发布后两天内，可以随时被消费，两天过后这条记录会被抛弃并释放磁盘空间。`Kafka`的性能和数据大小无关，所以长时间存储数据没有什么问题.
![image-20210320140833189](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210320140833189.png)
事实上，在每一个消费者中唯一保存的元数据是`offset`（偏移量）即消费在`log`中的位置.偏移量由消费者所控制:通常在读取记录后，消费者会以线性的方式增加偏移量，但是实际上，由于这个位置由消费者控制，所以消费者可以采用任何顺序来消费记录。例如，一个消费者可以重置到一个旧的偏移量，从而重新处理过去的数据；也可以跳过最近的记录，从"现在"开始消费。
这些细节说明`Kafka` 消费者是非常廉价的—消费者的增加和减少，对集群或者其他消费者没有多大的影响。比如，你可以使用命令行工具，对一些`topic`内容执行 `tail`操作，并不会影响已存在的消费者消费数据。

### offset
`Kafka` 消费者端有位移（ `offset`）的概念。

- 每条消息在某个 `partition` 的位移是固定的，但消费该 `partition` 的消费者的位移会随着消费进度不断前移
- 消费者位移不可能超过该分区最新一条消息的位移 。
  `Kafka` 中的一条消息其实就是一个`<topic,partition,offset>`三元组（`tuple`），通过该元组值我们可以在 `Kafka` 集群中找到唯一对应的那条消息。
![image-20210331204753947](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(一)之基础概念.assets\image-20210331204753947.png)
### Partition
`topic` 物理上的分组，一个 `topic` 可以分为多个 `partition`，每个 `partition` 是一个有序的队列（分区可以间接理解成数据库的分表操作）
一般来说：
（1）一个 `Topic` 的 `Partition` 数量大于等于 `Broker` 的数量，可以提高吞吐率。
（2）同一个 `Partition` 的 `Replica` 尽量分散到不同的机器，高可用。
日志中的 `partition`（分区）有以下几个用途:

第一，当日志大小超过了单台服务器的限制，允许日志进行扩展。每个单独的分区都必须受限于主机的文件限制，不过一个主题可能有多个分区，因此可以处理无限量的数据。

第二，可以作为并行的单元集。

### Producer
生产者可以将数据发布到所选择的`topic`（主题）中。生产者负责将记录分配到`topic`的哪一个 `partition`（分区）中。可以使用循环的方式来简单地实现负载均衡，也可以根据某些语义分区函数(例如：记录中的key)来完成。
默认是：

```java
defaultPartition Utils.abs(key.hashCode) % numPartitions
```

### Consumer
每个 `Consumer` 进程都会划归到一个逻辑的`Consumer Group`中，逻辑的订阅者是`Consumer Group`，同一个 `Consumer Group` 中的 `Consumer` 可以在不同的程序中，也可以在不同的机器上。所以一条`message`可以被多个订阅该 `message` 所在的`topic`的每一个`Consumer Group` 所消费，也就好像是这条`message`被广播到每个`Consumer Group`一样。而每个`Consumer Group`中，类似于一个`Queue`（`JMS`中的`Queue`）的概念差不多，即`topic`中的一条`message`只会被`Consumer Group`中的一个`Consumer`消费
![image-20210330225625665](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210330225625665.png)

### replia
`partition` 是有序消息日志，为了实现高可靠性，`Kafka`保存了多个备份日志，在 `Kafka` 中被称为**副本（ replica ）**，它们存在的唯一目的就是**防止数据丢失**。副本分为两类 ： 领导者副本（ `leader replica` ）和追随者副本（ `follower replica` ）

- `leader` 对外提供服务
- `follower` 只是被动地追随 `leader` 的状态，保持与 `leader` 的同步。**`follower` 存在的唯一价值就是充当 `leader`的候补**：一旦 `leader` 挂掉立即就会有一个追随者被选举成为新的 `leader` 接替它的工作。
### ISR
在`kafka`中**ISR**是什么？
在zk中会保存**AR**（`Assigned Replicas`）列表，其中包含了分区所有的副本，其中 **AR = ISR+OSR**
**ISR**（`in sync replica`）：是`kafka`动态维护的一组同步副本，在**ISR**中有成员存活时，只有这个组的成员才可以成为`leader`，内部保存的为每次提交信息时必须同步的副本（`acks = all`时），每当`leader`挂掉时，在**ISR**集合中选举出一个`follower`作为`leader`提供服务，当**ISR**中的副本被认为坏掉的时候，会被踢出**ISR**，当重新跟上`leader`的消息数据时，重新进入**ISR**。
**OSR**（`out sync replica`）: 保存的副本不必保证必须同步完成才进行确认，**OSR**内的副本是否同步了`leader`的数据，不影响数据的提交，**OSR**内的`follower`尽力的去同步`leader`，可能数据版本会落后。
**什么情况下OSR中的replica会重新加入到ISR**？

> replica重新追上leader的时候，会回到ISR中
> **LEO**（`last end offset`）:当前replica存的最大的offset的下一个值，LEO=10，则表示在该副本日志上已经保存了10条消息，偏移量范围为[0,9]
> **HW**（`high watermark`）:俗称高水位，**小于 HW 值**的`offset`所对应的消息被认为是「已提交」或「已备份」的消息，才对`consumer`可见，offset只能拉取这个offset之前的消息。任何一个副本对象的HW的值一定不大于其LEO的值（这里强调的是同一个副本的HW和LEO大小的比较）
- leader HW值 = 所有副本LEO最小值
- follower HW值 =min(follower自身LEO 和 leader HW)
![image-20210331203438051](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(一)之基础概念.assets\image-20210331203438051.png)
HW能保证数据的一致性

### LSO

![image-20210401211329843](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(一)之基础概念.assets\image-20210401211329843.png)

如上图所示，它代表一个日志文件，这个日志文件中有 9 条消息，第一条消息的 `offset`( `logStartOffset`)为 0，最后一条消息的 `offset` 为 8，`offset` 为 9 的消息用虚线框表示，代表下一条待写入的消息。日志文件的 `HW` 为 6，表示消费者只能拉取到 `offset` 在 0 至 5 之间的消息， 而 `offset` 为 6 的消息对消费者而言是不可见的。

而`LW` 是 `Low` `Watermark` 的缩写，俗称“低水位”，代表 `AR` 集合中最小的 `logStartOffset` 值。副本的拉取请求(`FetchRequest`，它有可能触发新建日志分段而旧的被清理，进而导致 `logStartOffset` 的增加)和删除消息请求(`DeleteRecordRequest`)都有可能促使 `LW` 的增长。

在 `Kafka` 的日志管理器中会有一个专门的日志删除任务来周期性地检测和删除不符合保留条件的日志分段文件，这个周期可以通过 `broker` 端参数 `log`.`retention`.`check`.`interval`.`ms`来配置，默认值为 300000，即 5 分钟。当前日志分段的保留策略有 3 种：基于时间的保留策略、基于日志大小的保留策略和基于日志起始偏移量的保留策略。而“基于日志起始偏移量的保留策略”正是基于 `logStartOffset`来实现的。

一般情况下，日志文件的起始偏移量 `logStartOffset` 等于第一个日志分段的 `baseOffset`，但这并不是绝对的，`logStartOffset` 的值可以通过 `DeleteRecordsRequest` 请求(比如使用 `KafkaAdminClient` 的 `deleteRecords`()方法、使用 `kafka`-`delete`-`records`.`sh` 脚本、日志的清理和截断等操作进行修改。

基于日志起始偏移量的保留策略的判断依据是某日志分段的下一个日志分段的起始偏移量 `baseOffset` 是否小于等于 `logStartOffset`，若是，则可以删除此日志分段。如下图所示。

![image-20210401212236777](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(一)之基础概念.assets\image-20210401212236777.png)

假设 `logStartOffset` 等于 25，日志分段 1 的起始偏移量为 0，日志分段 2 的起始偏移量为 11， 日志分段 3 的起始偏移量为 23，那么通过如下动作收集可删除的日志分段的文件集合 `deletableSegments`:

1. 从头开始遍历每个日志分段，日志分段 1 的下一个日志分段的起始偏移量为11，小于`logStartOffset` 的大小，将日志分段1加入 `deletableSegments`。
2. 日志分段 2 的下一个日志偏移量的起始偏移量为 23，也小于 `logStartOffset` 的大小， 将日志分段 2 页加入 `deletableSegments`。
3. 日志分段 3 的下一个日志偏移量在 `logStartOffset` 的右侧，故从日志分段 3 开始的所有日志分段都不会加入 `deletableSegments`。



## Reference

https://honeypps.com/mq/kafka-basic-knowledge-of-lw-and-logstartoffset/