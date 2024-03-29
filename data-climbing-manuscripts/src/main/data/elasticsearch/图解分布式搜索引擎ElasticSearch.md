## 图解分布式搜索引擎ElasticSearch

### 1.基础概念

![image-20210819090215693](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819090215693.png)

#### index:索引

一个索引就是一个拥有几分相似特征的文档的集合。比如说，你可以有一个客户数据的索引，另一个产品目录的索引，还有一个订单数据的索引。一个索引由一个名字来标识（必须全部是小写字母的），并且当我们要对对应于这个索引中的文档进行索引、搜索、更新和删除的时候，都要使用到这个名字。在一个集群中，可以定义任意多的索引。

#### type:类型

在一个索引中，你可以定义一种或多种类型。一个类型是你的索引的一个逻辑上的分类/分区，其语义完全由你来定。通常，会为具有一组共同字段的文档定义一个类型。比如说，我们假设你运营一个博客平台并且将你所有的数据存储到一个索引中。在这个索引中，你可以为用户数据定义一个类型，为博客数据定义另一个类型，当然，也可以为评论数据定义另一个类型。

![image-20210819090205172](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819090205172.png)

#### field:字段

相当于是数据表的字段，对文档数据根据不同属性进行的分类标识。

#### document:文档

一个文档是一个可被索引的基础信息单元。比如，你可以拥有某一个客户的文档，某一个产品的一个文档，当然，也可以拥有某个订单的一个文档，文档以JSON格式来表示。
如：

```json
{
  "user": "Alice",
  "title": "Dev",
  "desc": "996"
}
```
在一个index/type里面，你可以存储任意多的文档。注意，尽管一个文档，物理上存在于一个索引之中，文档必须被索引/赋予一个索引的type，而**doucument是ElasticSearch中最小的数据单元**

#### mapping:映射

mapping是处理数据的方式和规则方面做一些限制，如某个字段的数据类型、默认值、分析器、是否被索引等等，这些都是映射里面可以设置的，其它就是处理es里面数据的一些使用规则设置也叫做映射，按着最优规则处理数据对性能提高很大，因此才需要建立映射，并且需要思考如何建立映射才能对性能更好。

#### NRT(near real-time):接近实时

ElasticSearch是一个接近实时的搜索平台。这意味着，从索引一个文档直到这个文档能够被搜索到有一个轻微的延迟（通常是1秒以内）。

#### cluster:集群

一个集群就是由一个或多个节点组织在一起，它们共同持有整个的数据，并一起提供索引和搜索功能。一个集群由一个唯一的名字标识，这个名字默认就是“elasticsearch”。这个名字是重要的，因为一个节点只能通过指定某个集群的名字，来加入这个集群。

#### node:节点

一个节点是集群中的一个服务器，作为集群的一部分，它存储数据，参与集群的索引和搜索功能。和集群类似，一个节点也是由一个名字来标识的，默认情况下，这个名字是一个随机的漫威漫画角色的名字，这个名字会在启动的时候赋予节点。这个名字对于管理工作来说挺重要的，因为在这个管理过程中，你会去确定网络中的哪些服务器对应于ElasticSearch集群中的哪些节点。

一个节点可以通过配置集群名称的方式来加入一个指定的集群。默认情况下，每个节点都会被安排加入到一个叫做“elasticsearch”的集群中，这意味着，如果你在你的网络中启动了若干个节点，并假定它们能够相互发现彼此，它们将会自动地形成并加入到一个叫做“elasticsearch”的集群中。

在一个集群里，只要你想，可以拥有任意多个节点。而且，如果当前你的网络中没有运行任何Elasticsearch节点，这时启动一个节点，会默认创建并加入一个叫做“elasticsearch”的集群。

#### shards&replicas:分片和复制

![image-20210819100158711](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819100158711.png)

一个索引可以存储超出单个结点硬件限制的大量数据。比如，一个具有10亿文档的索引占据1TB的磁盘空间，而任一节点都没有这样大的磁盘空间；或者单个节点处理搜索请求，响应太慢。为了解决这个问题，ElasticSearch提供了将索引划分成多份的能力，这些份就叫做分片。当创建一个索引的时候，可以指定想要的分片的数量。每个分片本身也是一个功能完善并且独立的“索引”，这个“索引”可以被放置到集群中的任何节点上。

分片很重要，主要有两方面的原因：

1）允许你水平分割/扩展你的内容容量。

2）允许你在分片（潜在地，位于多个节点上）之上进行分布式的、并行的操作，进而提高性能/吞吐量。

至于一个分片怎样分布，它的文档怎样聚合回搜索请求，是完全由ElasticSearch管理的，对于作为用户的你来说，这些都是透明的。

在一个网络/云的环境里，失败随时都可能发生，在某个分片/节点不知怎么的就处于离线状态，或者由于任何原因消失了，这种情况下，有一个故障转移机制是非常有用并且是强烈推荐的。为此目的，ElasticSearch允许你创建分片的一份或多份拷贝，这些拷贝叫做复制分片，或者直接叫复制。

复制之所以重要，有两个主要原因： 在分片/节点失败的情况下，提供了高可用性。因为这个原因，注意到复制分片从不与原/主要（original/primary）分片置于同一节点上是非常重要的。扩展你的搜索量/吞吐量，因为搜索可以在所有的复制上并行运行。总之，每个索引可以被分成多个分片。一个索引也可以被复制0次（意思是没有复制）或多次。一旦复制了，每个索引就有了主分片（作为复制源的原来的分片）和复制分片（主分片的拷贝）之别。分片和复制的数量可以在索引创建的时候指定。在索引创建之后，你可以在任何时候动态地改变复制的数量，但是你事后不能改变分片的数量。

默认情况下，ElasticSearch中的每个索引被分片5个主分片和1个复制，这意味着，如果你的集群中至少有两个节点，你的索引将会有5个主分片和另外5个复制分片（1个完全拷贝），这样的话每个索引总共就有10个分片。

### 2.读写数据

#### 写入数据

![image-20210819192950043](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819192950043.png)

1. 客户端选择一个 node 发送请求过去，这个 node 就是 coordinating node（协调节点）
2. coordinating node 对 document 进行路由，将请求转发给对应的 node（有 primary shard）
3. node 上的 主分片（primary shard）处理请求，然后将数据同步到 复制分片（replica node)
4. node报告成功到协调节点，协调节点再报告给客户端

##### 写一致性如何保证

- **one**：只要有一个primary shard是active活跃可用的，就可以执行。
- **all**：必须所有的primary shard和replica shard都是活跃的，才可以执行这个写操作
- **quorum**：默认的值，要求所有的shard中，必须是大部分的shard都是活跃的，可用的，才可以执行这个写操作

 上面三点其实很好理解，只有quorum所谓的“大部分”感觉不是那么的明确。下面有个公式，当集群中的active（可用）分片数量达到如下公式结果时写操作就是可以执行的。否则该操作将无法进行。

```
int( (primary + number_of_replicas) / 2 ) + 1
```

假设我们创建了一个student索引，并且设置primary shard为3个，replica shard有1个（这个1个是相对于索引来说的，对于主分片该数字1意味着每个primary shard都对应的存在一个副本）。也就意味着primary=3，number_of_replicas=1（依然是相对于索引）。shard总数为6。

此时计算上面公式可知：

```
int((3+1)/2) + 1 = 3
```

也就是说当集群中可用的shard数量>=3写操作就是可以执行的。

#### 读取数据

![image-20210819194037122](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819194037122.png)

可以通过 doc id 来查询，会根据 doc id 进行 hash，判断出来当时把 doc id 分配到了哪个 shard 上面去，从那个 shard 去查询。

1. 客户端发送请求到任意一个 node，成为 coordinate node
2. coordinate node 对 doc id 进行哈希路由，将请求转发到对应的 node，此时会使用 round-robin随机轮询算法，在 primary shard 以及其所有 replica 中随机选择一个，让读请求负载均衡
3. 接收请求的 node 返回 document 给 coordinate node
4. coordinate node 返回 document 给客户端

#### 搜索数据

##### MySQL中索引实现

##### MyISAM的索引实现

MyISAM表的索引和数据是分离的，索引保存在”表名.MYI”文件内，而数据保存在“表名.MYD”文件内。
MyISAM的索引方式也叫做“非聚集”的，之所以这么称呼是为了与InnoDB的聚集索引区分。

![image-20210819200857530](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819200857530.png)

##### InnoDB的索引实现

Primary key 这种索引叫做聚集索引。因为InnoDB的数据文件本身要按主键聚集，所以InnoDB要求表必须有主键（MyISAM可以没有），如果没有显式指定，则MySQL系统会自动选择一个可以唯一标识数据记录的列作为主键，如果不存在这种列，则MySQL自动为InnoDB表生成一个隐含字段作为主键，这个字段长度为6个字节，类型为长整形。

![image-20210819204023813](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819204023813.png)

这里以英文字符的ASCII码作为比较准则。聚集索引这种实现方式使得按主键的搜索十分高效，**但是辅助索引搜索需要检索两遍索引**：首先检索辅助索引获得主键，然后用主键到主索引中检索获得记录



##### 倒排索引

以「冰与火之歌」中的文本作为例子：

![image-20210819203134329](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819203134329.png)

##### 流程

1. 客户端发送请求到一个 coordinate node
2. 协调节点将搜索请求转发到所有的 shard 对应的 primary shard 或 replica shard，都可以
3. query phase：每个 shard 将自己的搜索结果（其实就是一些 doc id）返回给协调节点，由协调节点进行数据的合并、排序、分页等操作，产出最终结果
4. fetch phase：接着由协调节点根据 doc id 去各个节点上拉取实际的 document 数据，最终返回给客户端

#### 写入数据的底层逻辑

##### write

数据先写入in-memory buffer，在写入buffer的同时将数据写入translog日志文件，注意：此时数据还没有被成功es索引记录，因此无法搜索到对应数据；

![image-20210819222508953](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819222508953.png)

##### refresh

refresh 默认 1s，执行一次下图流程。ES 是支持修改这个值的，通过 index.refresh_interval 设置 refresh间隔时间。refresh 流程大致如下：
- in-memory buffer 中的文档写入到新的 segment 中，但 segment 是存储在文件系统的缓存中。此时文档可以被搜索到
- 最后清空 in-memory buffer。注意: Translog 没有被清空，为了将 segment 数据写到磁盘，文档经过 refresh 后， segment 暂时写到文件系统缓存，这样避免了性能 IO 操作，又可以使文档搜索到。refresh 默认1s执行一次，性能损耗太大。一般建议稍微延长这个 refresh 时间间隔，比如 5s。因此，ES 其实就是准实时，达不到真正的实时。

![image-20210819222823327](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819222823327.png)

##### flush

上个过程中 segment 在文件系统缓存中，会有意外故障文档丢失。那么，为了保证文档不会丢失，需要将文档写入磁盘。那么文档从文件缓存写入磁盘的过程就是 flush。写入磁盘后，清空 translog。
translog有如下的作用：

- 保证文件缓存中的文档不丢失
- 系统重启时，从 translog 中恢复
- 新的 segment 收录到 commit point 中

![image-20210819223041350](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819223041350.png)

##### merge

 segment 会越来越多，那么搜索会越来越慢，需要通过merge过程解决：

- 就是各个小segment文件，合并成一个大segment文件
- Segment合并结束，旧的segment文件会被删除
- .liv 文件维护的删除文档，会通过这个过程进行清除

![image-20210819223226574](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210819223226574.png)

### 3.性能优化方案

- **filesystem cache**：ES的搜索引擎依赖底层的filesystem cache，如果给filesystem cache更多的内存，尽量让内存可以容纳所有的index segment file索引数据文件
- **数据预热**：对于那些你觉得比较热的数据，即经常会有人访问的数据，最好做一个专门的缓存预热子系统，对于热数据，每隔一段时间，系统本身就提前访问一下，让数据进入filesystem cache里面去，这样下次访问的时候，性能会更好一些。
- **冷热分离**：
  - **冷数据索引**：查询频率低，基本无写入，一般为当天或最近2天以前的数据索引，这种数据可以存储在机械硬盘HDD中
  - **热数据索引**：查询频率高，写入压力大，一般为当天的数据索引，这种数据可以存储在SSD中
- **document的模型设计**：不要在搜索的时候去执行各种复杂的操作，尽量在document模型设计和数据写入的时候就将复杂操作处理掉
- **分页性能优化**：翻页翻得越深，每个shard返回的数据越多，而且协调节点处理的时间越长，此时，要用scroll，scroll会一次性的生成所有数据的快照，然后每次翻页都是通过移动游标来完成

### Reference

- [分布式搜索引擎Elasticsearch（一）](https://blog.csdn.net/u012373815/article/details/50460248/)
- [分布式搜索引擎Elasticsearch的架构分析](https://zhuanlan.zhihu.com/p/334348919)
- [深入详解Elasticsearch](https://blog.csdn.net/laoyang360/category_9266239.html)
- [「干货」图解 Elasticsearch 写入流程](https://blog.51cto.com/u_7117633/2866130)
- [倒排索引与ElasticSearch](https://www.cnblogs.com/kukri/p/9996104.html)

