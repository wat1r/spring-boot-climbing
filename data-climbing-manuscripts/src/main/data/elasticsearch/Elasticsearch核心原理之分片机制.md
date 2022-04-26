## Elasticsearch核心原理之分片机制



### 空集群



![image-20220426215516688](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220426215516688.png)

启动一个单独的节点，里面不包含任何的数据和索引，集群看起来就是一个上图一样的包含空内容节点的集群。

一个运行中的 `Elasticsearch` 实例称为一个节点，而集群是由一个或者多个拥有相同 `cluster.name` 配置的节点组成， 它们共同承担数据和负载的压力。当有节点加入集群中或者从集群中移除节点时，集群将会重新平均分布所有的数据。

当一个节点被选举成为主节点时， 它将负责管理集群范围内的所有变更，例如增加、删除索引，或者增加、删除节点等。 而主节点并不需要涉及到文档级别的变更和搜索等操作，所以当集群只拥有一个主节点的情况下，即使流量的增加它也不会成为瓶颈。 任何节点都可以成为主节点。我们的示例集群就只有一个节点，所以它同时也成为了主节点。

作为用户，可以将请求发送到 集群中的任何节点 ，包括主节点。 每个节点都知道任意文档所处的位置，并且能够将我们的请求直接转发到存储我们所需文档的节点。 无论我们将请求发送到哪个节点，它都能负责从各个包含所需文档的节点收集回数据，并将最终结果返回给客户端。 `Elasticsearch` 对这一切的管理都是透明的。





### 集群健康

可以使用下面的请求获取集群的状态

```http
GET /_cluster/health
```

返回的内容如下：

```json
{
   "cluster_name":          "elasticsearch",
   "status":                "green", 
   "timed_out":             false,
   "number_of_nodes":       1,
   "number_of_data_nodes":  1,
   "active_primary_shards": 0,
   "active_shards":         0,
   "relocating_shards":     0,
   "initializing_shards":   0,
   "unassigned_shards":     0
}
```

`status` 字段指示着当前集群在总体上是否工作正常。它的三种颜色含义如下：

- `green` :所有主要分片和复制分片都可用

- `yellow`:所有主要分片可用，但不是所有复制分片都可用

- `red`:不是所有的主要分片都可用



### 添加索引

往`Elasticsearch`添加数据时需要用到**索引** (保存相关数据的地方)。 **索引**实际上是指向一个或者多个物理**分片**的逻辑命名空间 。

一个分片是一个底层的工作单元，它仅保存了全部数据中的一部分。一个分片是一个 `Lucene` 的实例，以及它本身就是一个完整的搜索引擎。 文档被存储和索引到分片内，但是应用程序是直接与索引而不是与分片进行交互。

`Elasticsearch`是利用分片将数据分发到集群内各处的。分片是数据的容器，文档保存在分片内，分片又被分配到集群内的各个节点里。 当你的集群规模扩大或者缩小时， `Elasticsearch` 会自动的在各节点中迁移分片，使得数据仍然均匀分布在集群里。

一个分片可以是主分片或者副本分片。 索引内任意一个文档都归属于一个主分片，所以主分片的数目决定着索引能够保存的最大数据量。

一个副本分片只是一个主分片的拷贝。副本分片作为硬件故障时保护数据不丢失的冗余备份，并为搜索和返回文档等读操作提供服务。

在索引建立的时候就已经确定了主分片数，但是副本分片数可以随时修改。

在包含一个空节点的集群内创建名为 `blogs` 的索引。 索引在默认情况下会被分配5个主分片， 但是为了演示目的，将分配3个主分片和一份副本（每个主分片拥有一个副本分片）：

```http
PUT /blogs
{
   "settings" : {
      "number_of_shards" : 3,
      "number_of_replicas" : 1
   }
}
```

![image-20220426220805661](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220426220805661.png)

可以看出来，在集群中，所有3个主分片都被分配在 `Node 1` 。

查询集群的健康状态：

```json
{
  "cluster_name": "elasticsearch",
  "status": "yellow", 
  "timed_out": false,
  "number_of_nodes": 1,
  "number_of_data_nodes": 1,
  "active_primary_shards": 3,
  "active_shards": 3,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 3, 
  "delayed_unassigned_shards": 0,
  "number_of_pending_tasks": 0,
  "number_of_in_flight_fetch": 0,
  "task_max_waiting_in_queue_millis": 0,
  "active_shards_percent_as_number": 50
}
```

集群的健康状况为 `yellow` 则表示全部 主分片都正常运行（集群可以正常服务所有请求），但是副本分片没有全部处在正常状态。 实际上，所有3个副本分片都是 `unassigned` —— 它们都没有被分配到任何节点。 在同一个节点上既保存原始数据又保存副本是没有意义的，因为一旦失去了那个节点，也将丢失该节点上的所有副本数据。

当前的集群是正常运行的，但是在硬件故障时有丢失数据的风险









分片分为主分片（Primary）和副本分片（Replica）。副本分片主要功能如下：

高可用性：副本分片作为数据备份，当某个主分片发生故障时，副本分片能够成为新的主分片，保证服务的可用性。
提高性能：副本分片本身也是一个功能齐全的独立的分片(所以才能够随时取代故障的主分片)，当有查询请求时，既可以在主分片中完成查询，也可以在副本分片中完成查询，当然数据添加、更新的操作只能在主分片中完成。







### Reference

- https://blog.csdn.net/mgxcool/article/details/49250341
- https://elasticsearch.cn/

- [Elasticsearch原理学习（四）分片、副本、缩容与扩容](https://www.jianshu.com/p/f14aadd31e19)

- [Elasticsearch 分片和副本策略](https://juejin.cn/post/6844903862088777736)
- [Elatsicsearch分片和副本相关知识 ](https://www.cnblogs.com/Yemilice/p/10401688.html)
- [Elasticsearch: 权威指南（2.x）](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)
- [Elasticsearch中文文档](https://learnku.com/docs/elasticsearch73/7.3)

