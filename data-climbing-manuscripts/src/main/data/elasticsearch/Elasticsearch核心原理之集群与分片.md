## Elasticsearch核心原理之集群与分片



## 集群

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



### 添加故障转移

当集群中只有一个节点在运行时，意味着会有一个单点故障问题。

![image-20220427085712923](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427085712923.png)

当第二个节点加入到集群后，3个副本分片将会分配到这个节点上——每个主分片对应一个副本分片。 这意味着当集群内任何一个节点出现问题时，我们的数据都完好无损。

所有新近被索引的文档都将会保存在主分片上，然后被并行的复制到对应的副本分片上。这就保证了我们既可以从主分片又可以从副本分片上获得文档。

`cluster-health` 现在展示的状态为 `green` ，这表示所有6个分片（包括3个主分片和3个副本分片）都在正常运行。

```json
{
  "cluster_name": "elasticsearch",
  "status": "green", 
  "timed_out": false,
  "number_of_nodes": 2,
  "number_of_data_nodes": 2,
  "active_primary_shards": 3,
  "active_shards": 6,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 0,
  "delayed_unassigned_shards": 0,
  "number_of_pending_tasks": 0,
  "number_of_in_flight_fetch": 0,
  "task_max_waiting_in_queue_millis": 0,
  "active_shards_percent_as_number": 100
}
```



### 水平扩容

![image-20220427200602453](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427200602453.png)

`Node 1` 和 `Node 2` 上各有一个分片被迁移到了新的 `Node 3` 节点，现在每个节点上都拥有2个分片，而不是之前的3个。 这表示每个节点的硬件资源（CPU, RAM, I/O）将被更少的分片所共享，每个分片的性能将会得到提升。

分片是一个功能完整的搜索引擎，它拥有使用一个节点上的所有资源的能力。 我们这个拥有6个分片（3个主分片和3个副本分片）的索引可以最大扩容到6个节点，每个节点上存在一个分片，并且每个分片拥有所在节点的全部资源。





### 更多的扩容

如果我们想要扩容超过6个节点怎么办呢？

主分片的数目在索引创建时就已经确定了下来。实际上，这个数目定义了这个索引能够 *存储* 的最大数据量。（实际大小取决于你的数据、硬件和使用场景。） 但是，读操作——搜索和返回数据——可以同时被主分片 *或* 副本分片所处理，所以当你拥有越多的副本分片时，也将拥有越高的吞吐量。

在运行中的集群上是可以动态调整副本分片数目的，我们可以按需伸缩集群。让我们把副本数从默认的 `1` 增加到 `2` ：

```http
PUT /blogs/_settings
{
   "number_of_replicas" : 2
}
```

![image-20220427200700470](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427200700470.png)



> 当然，如果只是在相同节点数目的集群上增加更多的副本分片并不能提高性能，因为每个分片从节点上获得的资源会变少。 你需要增加更多的硬件资源来提升吞吐量。
>
> 但是更多的副本分片数提高了数据冗余量：按照上面的节点配置，我们可以在失去2个节点的情况下不丢失任何数据。







### 应对故障



![image-20220427201719217](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427201719217.png)







关闭的节点是一个主节点。而集群必须拥有一个主节点来保证正常工作，所以发生的第一件事情就是选举一个新的主节点： `Node 2` 。

在我们关闭 `Node 1` 的同时也失去了主分片 `1` 和 `2` ，并且在缺失主分片的时候索引也不能正常工作。 如果此时来检查集群的状况，我们看到的状态将会为 `red` ：不是所有主分片都在正常工作。

幸运的是，在其它节点上存在着这两个主分片的完整副本， 所以新的主节点立即将这些分片在 `Node 2` 和 `Node 3` 上对应的副本分片提升为主分片， 此时集群的状态将会为 `yellow` 。 这个提升主分片的过程是瞬间发生的，如同按下一个开关一般。

为什么我们集群状态是 `yellow` 而不是 `green` 呢？ 虽然我们拥有所有的三个主分片，但是同时设置了每个主分片需要对应2份副本分片，而此时只存在一份副本分片。 所以集群不能为 `green` 的状态，不过我们不必过于担心：如果我们同样关闭了 `Node 2` ，我们的程序 *依然* 可以保持在不丢任何数据的情况下运行，因为 `Node 3` 为每一个分片都保留着一份副本。

如果重新启动 `Node 1` ，集群可以将缺失的副本分片再次进行分配，那么集群的状态也将如上一章节所示。 如果 `Node 1` 依然拥有着之前的分片，它将尝试去重用它们，同时仅从主分片复制发生了修改的数据文件









## 分片与副本



分片分为主分片（`Primary`）和副本分片（`Replica`）。副本分片主要功能如下：

高可用性：副本分片作为数据备份，当某个主分片发生故障时，副本分片能够成为新的主分片，保证服务的可用性。
提高性能：副本分片本身也是一个功能齐全的独立的分片(所以才能够随时取代故障的主分片)，当有查询请求时，既可以在主分片中完成查询，也可以在副本分片中完成查询，当然数据添加、更新的操作只能在主分片中完成。





查看集群的状态

```http
GET http://localhost:2001/_cluster/health
{
    "cluster_name": "my-application",
    "status": "green",
    "timed_out": false,
    "number_of_nodes": 3,
    "number_of_data_nodes": 3,
    "active_primary_shards": 3,
    "active_shards": 9,
    "relocating_shards": 0,
    "initializing_shards": 0,
    "unassigned_shards": 0,
    "delayed_unassigned_shards": 0,
    "number_of_pending_tasks": 0,
    "number_of_in_flight_fetch": 0,
    "task_max_waiting_in_queue_millis": 0,
    "active_shards_percent_as_number": 100.0
}
```





创建`test`索引（1个分片）

```http
PUT http://localhost:2001/test
{
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0
    }
}
```

创建`test1`索引（2个分片）

```http
PUT http://localhost:2001/test1
{
    "settings": {
        "number_of_shards": 2,
        "number_of_replicas": 0
    }
}
```

创建`test2`索引（3个分片）

```http
PUT http://localhost:2001/test1
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 0
    }
}
```

创建`test3`索引（4个分片）

```http
PUT http://localhost:2001/test1
{
    "settings": {
        "number_of_shards": 4,
        "number_of_replicas": 0
    }
}
```



![image-20220427210035411](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427210035411.png)

从上面的结果看出，集群节点为3个（`node-2001`，`node-2002`，`node-2003`）,当分片的个数小于等于3的时候，其分布在三个节点或者三个节点的部分节点上，当分片的数量大于3为4的时候，有两个分片分布在其中的一个节点（图中为`node-2001`）



### 副本

在一个网络 / 云的环境里，失败随时都可能发生，在某个分片/节点不知怎么的就处于离线状态，或者由于任何原因消失了，这种情况下，有一个故障转移机制是非常有用并且是强烈推荐的。为此目的，`Elasticsearch`允许你创建分片的一份或多份拷贝，这些拷贝叫做复制分片(副本)。

副本存在的两个重要原因：

- 1.提高可用性：注意的是副本不能与主/原分片位于同一节点。
- 2.提高吞吐量：搜索操作可以在所有的副本上并行运行。

创建`test4`索引（1分片2副本）

```http
PUT http://localhost:2001/test4
{
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 2
    }
}
```

看到了3个绿色的0，其中在`node-2002`节点的边框是粗体的，这个表示分片，而另外两个节点的0的边框是细体的，这两个就是分片的副本。

通常我们三个节点建立两个副本就可以了，三份数据均匀得到分布在三个节点。如果建立三个副本会怎么样呢？

创建`test5`索引（1分片3副本）

```http
PUT http://localhost:2001/test5
{
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 3
    }
}
```

![image-20220427211040025](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427211040025.png)

如上所示我们看到多出一个`Unassigned`的副本，这个副本其实是多余的了，因为每个节点已经包含了分片的本身和其副本，多于这个没有意义。



创建`test6`索引（默认）

```http
PUT http://localhost:2001/test6
{
    "settings": {
       
    }
}
```

可以看出，对于三节点的集群来说，默认是一个分片，一个副本。

创建`test7`索引（2分片2副本）

```http
PUT http://localhost:2001/test7
{
    "settings": {
        "number_of_shards": 2,
        "number_of_replicas": 2
    }
}
```

创建`test8`索引（3分片2副本）

```http
PUT http://localhost:2001/test8
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 2
    }
}
```

![image-20220427212129995](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427212129995.png)

可以看出，分片和副本均匀地分布在每个节点上（粗线框表示分片，细线框表示副本）





### 缩容

如果其中一个节点挂掉，会发生什么？

原本的`node-2003`节点变成了`Unassigned`，并且注意标注的三个红框内的分片，这三个分片已经随着节点的宕机消息了，这就造成了数据的丢失；反观后面几个，虽然`node-2003`宕机了，但是由于做了分片与备份，索引仍然可以正常的工作，而且`test8`索引上原属于`node-2003`的2号分片转移到`node-2001`节点上。

![image-20220427212855613](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427212855613.png)



如果再挂一个节点会出现什么？`node-2002`宕机后，集群已经不可见了，说明只有1个节点的集群不可用。

![image-20220427213112481](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427213112481.png)



### 扩容

创建`test9`索引（3分片2副本）

```http
PUT http://localhost:2001/test9
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 2
    }
}
```



恢复节点`node-2002`后，再次创建`test9`索引，集群的恢复可以访问的状态



![image-20220427213715951](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427213715951.png)

当恢复`node-2003`后，集群恢复正常



![image-20220427214051150](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220427214051150.png)



但是`node-2003`上的全部是副本，并没有将分片转移到`node-2003`节点上



### 索引的路由

通过上面的结果，发现分片和副本的存储规律，那么在每次我们进行索引的时候，是通过什么样的路由方式去找到对应的分片，从而获取想要的数据呢？

实际`es`是通过`hash`运算找到每次数据存储的位置，公式如下：

```java
shard=hash(routing)%number_of_primary_shards
```

`routing` 是一个可变值，默认是文档的 `_id `，也可以设置成一个自定义的值。 `routing` 通过`hash` 函数生成一个数字，然后这个数字再模上 `number_of_primary_shards` （主分片的数量）后得到余数 。这个分布在` 0 `到 `number_of_primary_shards-1` 之间的余数，就是所寻求的文档所在分片的位置。

通过上面的公式，我们理解并且也需要记住一个重要的概念：
 **创建索引的时候就确定好主分片的数量，并且永远不会改变这个数量**
 数量的改变将导致上述公式的结果变化，最终会导致我们的数据无法被找到。



















### Reference

- https://blog.csdn.net/mgxcool/article/details/49250341
- https://elasticsearch.cn/

- [Elasticsearch原理学习（四）分片、副本、缩容与扩容](https://www.jianshu.com/p/f14aadd31e19)

- [Elasticsearch 分片和副本策略](https://juejin.cn/post/6844903862088777736)
- [Elatsicsearch分片和副本相关知识 ](https://www.cnblogs.com/Yemilice/p/10401688.html)
- [Elasticsearch: 权威指南（2.x）](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)
- [Elasticsearch中文文档](https://learnku.com/docs/elasticsearch73/7.3)

