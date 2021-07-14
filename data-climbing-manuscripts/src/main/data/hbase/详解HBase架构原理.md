# 详解HBase架构原理

## 基础

### 数据模型

![image-20210708202859199](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hbase\详解HBase架构原理.assets\image-20210708202859199.png)



![image-20210708205922121](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hbase\详解HBase架构原理.assets\image-20210708205922121.png)





- `Name Space`
  命名空间，类似于关系型数据库的 `DatabBase` 概念，每个命名空间下有多个表。 `HBase`有两个自带的命名空间，分别是 `hbase` 和 `default`， `hbase` 中存放的是 `HBase` 内置的表，`default` 表是用户默认使用的命名空间。
- `Region`
  类似于关系型数据库的表概念。不同的是， `HBase` 定义表时只需要声明列族即可，不需要声明具体的列。这意味着， 往 `HBase` 写入数据时，字段可以动态、 按需指定。因此，和关系型数据库相比， `HBase` 能够轻松应对字段变更的场景。
- `Row`
  `HBase` 表中的每行数据都由一个 `RowKey` 和多个 `Column`（列）组成，数据是按照 `RowKey`的字典顺序存储的，并且查询数据时只能根据 `RowKey` 进行检索，所以 `RowKey` 的设计十分重要。
- `Column`
  `HBase` 中的每个列都由 `Column Family`(列族)和 `Column Qualifier`（列限定符） 进行限定，例如 `info： name`， `info： age`。建表时，只需指明列族，而列限定符无需预先定义。
- `Time` `Stamp`
  用于标识数据的不同版本（`version`）， 每条数据写入时， 如果不指定时间戳， 系统会自动为其加上该字段，其值为写入 `HBase` 的时间。  
- `Cell`
  由{`rowkey`, `column Family`： `column Qualifier`, `time Stamp`} 唯一确定的单元。 `cell` 中的数据是没有类型的，全部是字节码形式存贮。  





#### StoreFile 和 HFile 

StoreFile 以 HFile的格式保存在HDFS上， HFile的格式：

![image-20210712204405790](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hbase\详解HBase架构原理.assets\image-20210712204405790.png)

 `HFile` 文件是不定长的，长度固定的只有其中的两块：`Trailer` 和 `FileInfo`

- `Trailer` 中有指针指向其他数据块的起始点。

- `FileInfo` 中记录了文件的一些 `Meta` 信息，例如：`AVG_KEY_LEN`, `AVG_VALUE_LEN`, `LAST_KEY`, `COMPARATOR`, `MAX_SEQ_ID_KEY` 等。

`HFile`分为6个部分：

- **`Data Block`** 段–保存表中的数据，这部分可以被压缩
  - **`Meta Block`** 段 (可选的)–保存用户自定义的 `kv` 对，可以被压缩。

  - **`File Info`** 段–`Hfile` 的元信息，不被压缩，用户也可以在这一部分添加自己的元信息。
  - **`Data Block Index`** 段–`Data Block` 的索引。每条索引的 `key` 是被索引的 `block` 的第一条记录的 `key`。
  - **`Meta Block Index`** 段 (可选的)–`Meta Block` 的索引。
  - **`Trailer`** 段–这一段是定长的。保存了每一段的偏移量，读取一个 `HFile` 时，会首先读取 `Trailer`， `Trailer`保存了每个段的起始位置(段的`Magic` `Number`用来做安全`check`)，然后，`DataBlock` `Index` 会被读取到内存中，这样，当检索某个 `key` 时，不需要扫描整个 `HFile`，而只需从内存中找 到`key`所在的`block`，通过一次磁盘`io`将整个`block`读取到内存中，再找到需要的`key`。`DataBlock` `Index` 采用 `LRU` 机制淘汰。

　　`HFile` 的 `Data` `Block`，`Meta` `Block` 通常采用压缩方式存储，压缩之后可以大大减少网络 `IO` 和磁 盘 `IO`，随之而来的开销当然是需要花费 `cpu` 进行压缩和解压缩。

目标 `Hfile` 的压缩支持两种方式：`Gzip`，`LZO`。

　　`Data` `Index` 和 `Meta` `Index` 块记录了每个 `Data` 块和 `Meta` 块的起始点。

　　`Data` `Block` 是 `HBase` `I`/`O` 的基本单元，为了提高效率，`HRegionServer` 中有基于 `LRU` 的 `Block` `Cache` 机制。每个 `Data` 块的大小可以在创建一个 `Table` 的时候通过参数指定，大号的 `Block` 有利于顺序 `Scan`，小号 `Block` 利于随机查询。 每个 `Data` 块除了开头的 `Magic` 以外就是一个 个 `KeyValue` 对拼接而成, `Magic` 内容就是一些随机数字，目的是防止数据损坏。



HFile 里面的每个 **KeyValue** 对就是一个简单的 byte 数组。但是这个 byte 数组里面包含了很多项，并且有固定的结构。下面是具体结构：

![image-20210713093452866](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hbase\详解HBase架构原理.assets\image-20210713093452866.png)

其中`Time Stamp` 和 `Key Type（Put/Delete）`



## 架构

![image-20210707201939888](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hbase\详解HBase架构原理.assets\image-20210707201939888.png)



- `Client` 
   - `HBase` 有两张特殊表：

     .`META`.：记录了用户所有表拆分出来的的 `Region` 映射信息，.`META`.可以有多个 `Regoin`

     -`ROOT`-：记录了.`META`.表的 `Region` 信息，-`ROOT`-只有一个 `Region`，无论如何不会分裂

  - `Client` 访问用户数据前需要首先访问 `ZooKeeper`，找到-`ROOT`-表的 `Region` 所在的位置，然 后访问-`ROOT`-表，接着访问.`META`.表，最后才能找到用户数据的位置去访问，中间需要多次网络操作，不过 `client` 端会做 `cache` 缓存。

- `ZooKeeper` 
   - `ZooKeeper` 为 `HBase` 提供 `Failover` 机制，选举 `Master`，避免单点 `Master` 单点故障问题

   - 存储所有 `Region` 的寻址入口：-`ROOT`-表在哪台服务器上。-`ROOT`-这张表的位置信息

   - 实时监控 `RegionServer` 的状态，将 `RegionServer` 的上线和下线信息实时通知给 `Master`

   - 存储 `HBase` 的 `Schema`，包括有哪些 `Table`，每个 `Table` 有哪些 `Column` `Family`

- `Master` 

  - 为 `RegionServer` 分配 `Region`

  - 负责 `RegionServer` 的负载均衡

  - 发现失效的 `RegionServer` 并重新分配其上的 `Region`

  - `HDFS` 上的垃圾文件（`HBase`）回收

  - 处理 `Schema` 更新请求（表的创建，删除，修改，列簇的增加等等）

- `RegionServer` 
  - `RegionServer` 维护 `Master` 分配给它的 `Region`，处理对这些 `Region` 的 `IO` 请求

  - `RegionServer` 负责 `Split` 在运行过程中变得过大的 `Region`，负责 `Compact` 操作

  可以看到，`client` 访问 `HBase` 上数据的过程并不需要 `master` 参与（寻址访问 `zookeeper` 和 `RegioneServer`，数据读写访问 `RegioneServer`），`Master` 仅仅维护者 `Table` 和 `Region` 的元数据信息，负载很低。

  .`META`. 存的是所有的 `Region` 的位置信息，那么 `RegioneServer` 当中 `Region` 在进行分裂之后 的新产生的 `Region`，是由 `Master` 来决定发到哪个 `RegioneServer`，这就意味着，只有 `Master` 知道 `new` `Region` 的位置信息，所以，由 `Master` 来管理.`META`.这个表当中的数据的 `CRUD`

  所以结合以上两点表明，在没有 `Region` 分裂的情况，`Master` 宕机一段时间是可以忍受的。

- `HRegion`
  `table`在行的方向上分隔为多个`Region`。`Region`是`HBase`中分布式存储和负载均衡的最小单元，即不同的`region`可以分别在不同的`Region` `Server`上，但同一个`Region`是不会拆分到多个`server`上。
  `Region`按大小分隔，每个表一般是只有一个`region`。随着数据不断插入表，`region`不断增大，当`region`的某个列族达到一个阈值时就会分成两个新的`region`。
  每个`region`由以下信息标识：< 表名,`startRowkey`,创建时间>
  由目录表(-`ROOT`-和.`META`.)记录该`region`的`endRowkey`
- `Store`
  每一个`region`由一个或多个`store`组成，至少是一个`store`，`hbase`会把一起访问的数据放在一个`store`里面，即为每个 `ColumnFamily`建一个`store`，如果有几个`ColumnFamily`，也就有几个`Store`。一个`Store`由一个`memStore`和0或者 多个`StoreFile`组成。 `HBase`以`store`的大小来判断是否需要切分`region`
- `MemStore`
  `memStore` 是放在内存里的。保存修改的数据即`keyValues`。当`memStore`的大小达到一个阀值（默认`128MB`）时，`memStore`会被`flush`到文 件，即生成一个快照。目前`hbase` 会有一个线程来负责`memStore`的`flush`操作。

- `StoreFile`
  `memStore`内存中的数据写到文件后就是`StoreFile`，`StoreFile`底层是以`HFile`的格式保存。当`storefile`文件的数量增长到一定阈值后，系统会进行合并（`minor`、`major` `compaction`），在合并过程中会进行版本合并和删除工作（`majar`），形成更大的`storefile`。

- `HFile`
   `HBase`中`KeyValue`数据的存储格式，`HFile`是`Hadoop`的 二进制格式文件，实际上`StoreFile`就是对`Hfile`做了轻量级包装，即`StoreFile`底层就是`HFile`。

- `HLog`
  `HLog`(`WAL` `log`)：`WAL`意为`write` `ahead` `log`，用来做灾难恢复使用，`HLog`记录数据的所有变更，一旦`region` `server` 宕机，就可以从`log`中进行恢复。
  `HLog`文件就是一个普通的`Hadoop` `Sequence` `File`， `Sequence` `File`的`value`是`key`时`HLogKey`对象，其中记录了写入数据的归属信息，除了`table`和`region`名字外，还同时包括`sequence` `number`和`timestamp`，`timestamp`是写入时间，`sequence` `number`的起始值为0，或者是最近一次存入文件系统中的`sequence` `number`。 `Sequence` `File`的`value`是`HBase`的`KeyValue`对象，即对应`HFile`中的`KeyValue`。

## 读写原理





## 命令行操作





## Reference

- [HBase（三）HBase架构与工作原理](https://www.cnblogs.com/frankdeng/p/9310278.html)

