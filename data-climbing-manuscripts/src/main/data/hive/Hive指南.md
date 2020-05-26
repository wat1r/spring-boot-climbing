## Hive指南
### 1.几种类型的存储格式
#### 1.1.TEXTFILE 
 textfile 为默认的数据存储格式，textfile 以文本文件的形式存储数据，
- 优:
  - 可结合Gzip、Bzip2、Snappy等使用（系统自动检查，执行查询时自动解压） 
- 缺:
  -  hive不会对数据进行切分，从而无法对数据进行并行操作 
  -  数据不做压缩，磁盘开销大，数据解析开销大 
#### 1.2.SEQUENCEFILE 
- 存储特点：二进制文件,以<key,value>的形式序列化到文件中， Hive 中的SequenceFile 继承自Hadoop API 的SequenceFile，不过它的key为空，使用value 存放实际的值， 这样是为了避免MR 在运行map 阶段的排序过程 
- 存储方式：行存储
- SequenceFile的文件结构图 
![1589464569541](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\1589464569541.png)
- Header通用头文件格式：
| SEQ              | 3BYTE                                 |
| ---------------- | ------------------------------------- |
| Nun              | 1byte数字                             |
| keyClassName     |                                       |
| ValueClassName   |                                       |
| compression      | （boolean）指明了在文件中是否启用压缩 |
| blockCompression | （boolean，指明是否是block压缩）      |
| compression      | codec                                 |
| Metadata         | 文件元数据                            |
| Sync             | 头文件结束标志                        |
- Block-Compressed SequenceFile格式 
![1589464846430](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\1589464846430.png)
#### 1.3. RCFile 
- Row Columnar , RCFile是一种行列存储相结合的存储方式 ,其将数据按行分块，保证同一个record在一个块上，避免读一个记录需要读取多个block。其次，块数据列式存储，有利于数据压缩和快速的列存取。
- 优：
  -  能够很好的压缩和快速的查询性能 
- 缺：
  -  不支持模式演进 ， 写操作比较慢，比非列形式的文件格式需要更多的内存空间和计算量 
  -  读记录尽量涉及到的block最少。读取需要的列只需要读取每个row group 的头部定义
#### 1.4.ORCFile
*Optimized Row Columnar* File
- 优:
  - 更好的压缩，更快的查询
  - 支持update操作，支持ACID，支持struct，array复杂类型
- 缺:
  - 不支持模式演进
  [官方地址](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+ORC)
![image-20200515091108716](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\image-20200515091108716-1589976775937.png)
- 每个Orc文件由1个或多个stripe组成，每个stripe250MB大小，这个Stripe实际相当于之前的rcfile里的RowGroup概念，不过大小由4MB->250MB，这样应该能提升顺序读的吞吐率。每个Stripe里有三部分组成，分别是Index Data,Row Data,Stripe Footer：
  -  Index Data：一个轻量级的index，默认是每隔1W行做一个索引。这里做的索引应该只是记录某行的各字段在Row Data中的offset，据说还包括每个Column的max和min值；
  -  Row Data：存的是具体的数据，和RCfile一样，先取部分行，然后对这些行按列进行存储。与RCfile不同的地方在于每个列进行了编码，分成多个Stream来存储；
  -  Stripe Footer：存的是各个Stream的类型，长度等信息。
- 每个文件有一个File Footer，这里面存的是每个Stripe的行数，每个Column的数据类型信息等；每个文件的尾部是一个PostScript，这里面记录了整个文件的压缩类型以及FileFooter的长度信息等。在读取文件时，会seek到文件尾部读PostScript，从里面解析到File Footer长度，再读FileFooter，从里面解析到各个Stripe信息，再读各个Stripe，即从后往前读。
#### 测试性能
```mysql
--textfile文件格式
CREATE TABLE `test_textfile`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS textfile;
--parquet文件格式
CREATE TABLE `test_parquet`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS parquet;
--orc文件格式
CREATE TABLE `test_orc`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS orc;
--sequence文件格式
CREATE TABLE `test_sequence`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS sequence;
--rc文件格式
CREATE TABLE `test_rc`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS rc;
--avro文件格式
CREATE TABLE `test_avro`(`id` STRING,…,`desc` STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS avro;
```
30000000条
下面从存储空间和SQL查询两个方面进行比较。
其中SQL查询为包含group by的计量统计和不含group by的计量统计。
```mysql
select count(*) from test_table;
select id,count(*) from test_table group by id;
```
相关的查询结果如下（为了防止出现偶然性，每条SQL至少执行三次，取平均值）
| 文件存储格式 | HDFS存储空间 | 不含group by | 含group by |
| ------------ | ------------ | ------------ | ---------- |
| TextFile     | 7.3 G        | 105s         | 370s       |
| Parquet      | 769.0 M      | 28s          | 195s       |
| ORC          | 246.0 M      | 34s          | 310s       |
| Sequence     | 7.8 G        | 135s         | 385s       |
| RC           | 6.9 G        | 92s          | 330s       |
| AVRO         | 8.0G         | 240s         | 530s       |
- ORC的存储于执行效率均是最优的
####  对比
- textfile 存储空间消耗比较大，并且压缩的text 无法分割和合并 查询的效率最低,可以直接存储，加载数据的速度最高
- sequencefile 存储空间消耗最大,压缩的文件可以分割和合并 需要通过text文件转化来加载
- rcfile 存储空间小，查询的效率高 ，需要通过text文件转化来加载，加载的速度最低
- orc 存储空间最小，查询的最高 ，需要通过text文件转化来加载，加载的速度最低（个人建议使用orc）
#### 实践

 **实践中常用的压缩+存储可以选择（部分）**
Textfile+Gzip
SequenceFile+Snappy
ORC+Snappy 





### 2.几种类型的压缩格式

Hive压缩比较
Default
gzip
zip
bzip2
lzo
ZLip
Snappy
开发过程中一般使用orc,Parquet存储和snappy压缩,
orc默认使用了ZLIP压缩
zlip跟snappy比较
GZIP    13.4%    21 MB/s    118 MB/s
LZO    20.5%    135 MB/s    410 MB/s
Zippy/Snappy    22.2%    172 MB/s    409 MB/s
总结
ZLIP压缩最后的文件存储低,但是压缩效率较Snappy低太多了.
ZLIP压缩率高,缺点压缩过程很慢
Snappy压缩率相对Zlip低一些,但是比其他高很多了,压缩过程也很快.

### **MR支持的压缩编码**

| 压缩格式 | hadoop自带？ | 算法    | 文件扩展名 | 是否可切分 | 换成压缩格式后，原来的程序是否需要修改 |
| -------- | ------------ | ------- | ---------- | ---------- | -------------------------------------- |
| DEFAULT  | 是，直接使用 | DEFAULT | .deflate   | 否         | 和文本处理一样，不需要修改             |
| Gzip     | 是，直接使用 | DEFAULT | .gz        | 否         | 和文本处理一样，不需要修改             |
| bzip2    | 是，直接使用 | bzip2   | .bz2       | 是         | 和文本处理一样，不需要修改             |
| LZO      | 否，需要安装 | LZO     | .lzo       | 是         | 需要建索引，还需要指定输入格式         |
| Snappy   | 否，需要安装 | Snappy  | .snappy    | 否         | 和文本处理一样，不需要修改             |

为了支持多种压缩/解压缩算法，Hadoop引入了编码/解码器，如下表所示

| 压缩格式 | 对应的编码/解码器                          |
| -------- | ------------------------------------------ |
| DEFLATE  | org.apache.hadoop.io.compress.DefaultCodec |
| gzip     | org.apache.hadoop.io.compress.GzipCodec    |
| bzip2    | org.apache.hadoop.io.compress.BZip2Codec   |
| LZO      | com.hadoop.compression.lzo.LzopCodec       |
| Snappy   | org.apache.hadoop.io.compress.SnappyCodec  |

 压缩性能的比较

| 压缩算法 | 原始文件大小 | 压缩文件大小 | 压缩速度 | 解压速度 |
| -------- | ------------ | ------------ | -------- | -------- |
| gzip     | 8.3GB        | 1.8GB        | 17.5MB/s | 58MB/s   |
| bzip2    | 8.3GB        | 1.1GB        | 2.4MB/s  | 9.5MB/s  |
| LZO      | 8.3GB        | 2.9GB        | 49.3MB/s | 74.6MB/s |

### 3.Hive常用语句
#### 建表load数据
```mysql
CREATE TABLE user_test(id INT,name STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INPATH '/opt/document/hive_file/hive_test1.txt' OVERWRITE INTO TABLE user_test PARTITION (DS='20200506');
LOAD DATA LOCAL INPATH '/opt/document/hive_file/hive_test1.txt' OVERWRITE INTO TABLE user_test ;
```
```mysql
CREATE TABLE user_test_mirror AS SELECT * FROM user_test;
```
- https://blog.csdn.net/louzhu_lz/article/details/90046028
- https://blog.csdn.net/qq_32736999/article/details/90728450
### 4.Hive原理解析
#### Hive的架构图
<img src="D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\Hive原理图.jpg" alt="Hive原理图" style="zoom:67%;" />
（1）客户端组件
    1、CLI：命令行接口，是最常用的一种用户接口，CLI启动时会同时启动一个Hive副本。CLI是和Hive交互的最简单也是最常用方式，只需要在一个具备完整Hive环境下的Shell终端中键入hive即可启动服务。 用户可以在CLI上输入HQL来执行创建表、更改属性以及查询等操作。不过Hive CLI不适应于高并发的生产环境，仅仅是Hive管理员的好工具。
    2、JDBC/ODBC： JDBC是java database connection的规范，它定义了一系列java访问各类db的访问接口，因此hive-jdbc其实本质上是扮演一个协议转换的角色，把jdbc的标准协议转换为访问HiveServer服务的协议。hive-jdbc除了扮演网络协议转化的工作，并不承担他的工作，比如sql的合法性校验和解析，一律忽略 。ODBC是一组对数据库访问的标准AP ，它的底层实现源码是采用C/C++编写的。JDBC/ODBC都是通过hiveclient与hiveserver保持通讯的，借助thrfit rpc协议来实现交互。
    3、HWI：HWI是Hive的web方为接口，提供了一种可以可以通过浏览器来访问Hive的服务。
  （2）服务端组件
    1、Thrift Server：Thrift是facebook开发的一个软件框架，它用来进行可扩展且跨语言的服务的开发，hive集成了Thrift Server服务，能让不同的编程语言调用hive的接口。
    2、元数据（Metastore）：元数据服务组件，这个组件用于存储hive的元数据， 包括表名、表所属的数据库、表的拥有者、列/分区字段、表的类型、表的数据所在目录等内容。hive的元数据存储在关系数据库里，支持derby、mysql两种关系型数据库。元数据对于hive十分重要，因此hive支持把metastore服务独立出来，安装到远程的服务器集群里，从而解耦hive服务和metastore服务，保证hive运行的健壮性。
    3、Driver组件：该组件包括Interpreter、Complier、Optimizer和Executor，它的作用是将我们写的HiveQL（类SQL）语句进行解析、编译、优化，生成执行计划，然后调用底层的mapreduce计算框架。
      解释器：将SQL字符串转化为抽象语法树AST；
      编译器：将AST编译成逻辑执行计划；
      优化器：对逻辑执行计划进行优化；
      执行器：将逻辑执行计划转成可执行的物理计划，如MR/Spark
#### 元数据存储
  Metastore的存储有两个部分，服务和存储，Hive的存储有三种部署模式，分别为内嵌模式、本地模式和远程模式。
1、内嵌模式（Embedded）
hive服务和metastore服务运行在同一个进程中，derby服务也运行在该进程中.
内嵌模式使用的是内嵌的Derby数据库来存储元数据，也不需要额外起Metastore服务。
这个是默认的，配置简单，但是一次只能一个客户端连接，适用于用来实验，不适用于生产环境。
2、本地模式（Local）
本地安装mysql 替代derby存储元数据
这种安装方式和嵌入式的区别在于，不再使用内嵌的Derby作为元数据的存储介质，而是使用其他数据库比如MySQL来存储元数据。
hive服务和metastore服务运行在同一个进程中，mysql是单独的进程，可以同一台机器，也可以在远程机器上。
这种方式是一个多用户的模式，运行多个用户client连接到一个数据库中。这种方式一般作为公司内部同时使用Hive。
每一个用户必须要有对MySQL的访问权利，即每一个客户端使用者需要知道MySQL的用户名和密码才行。

```xml
<property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://127.0.0.1:3306/hive? createDatabaseIfNotExit=true</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>root</value>
    </property>
    <property>
    　　<name>hive.metastore.uris</name>
    　　<value></value>
    　　<description>指向的是运行metastore服务的主机,这是hive客户端配置，metastore服务不需要配置</description>
  　</property>
    <property>
    　　<name>hive.metastore.warehouse.dir</name>
    　　<value>/user/hive/warehouse</value>
    　　<description>hive表的默认存储路径,为HDFS的路径location of default database for the warehouse</description>
    </property>
```

3、远程模式（Remote）
远程安装mysql 替代derby存储元数据
Hive服务和metastore在不同的进程内，可能是不同的机器，该模式需要将hive.metastore.local设置为false，将hive.metastore.uris设置为metastore服务器URL，
如果有多个metastore服务器，将URL之间用逗号分隔，metastore服务器URL的格式为thrift://127.0.0.1:9083。
远程元存储需要单独起metastore服务，然后每个客户端都在配置文件里配置连接到该metastore服务。
将metadata作为一个单独的服务进行启动。各种客户端通过beeline来连接，连接之前无需知道数据库的密码。
仅连接远程的mysql并不能称之为“远程模式”，是否远程指的是metastore和hive服务是否在同一进程内.
hive metastore 服务端启动命令：

```
hive --service metastore -p <port_num>
```
如果不加端口默认启动：hive --service metastore，则默认监听端口是：9083 。
注意客户端中的端口配置需要和启动监听的端口一致。服务端启动正常后，客户端就可以执行hive操作了。
客户端连接metastore服务配置如下：
```xml
<property>
    <name>hive.metastore.uris</name>
    <value>thrift://127.0.0.1:9083，thrift://127.0.0.1:9084</value>
    <description>指向的是运行metastore服务的主机</description>
  </property>
```



### 5.Hive SQL的编译流程

![Hive SQL执行计划](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\Hive SQL执行计划.jpg)

(1)Antlr定义SQL的语法规则，完成SQL词法，语法解析，将SQL转化为抽象语法树AST Tree
(2)遍历AST Tree，抽象出查询的基本组成单元QueryBlock
(3)遍历QueryBlock，翻译为执行操作树OperatorTree
(4)逻辑层优化器进行OperatorTree变换，合并不必要的ReduceSinkOperator，减少shuffle数据量
(5)遍历OperatorTree，翻译为MapReduce任务
(6)物理层优化器进行MapReduce任务的变换，生成最终的执行计划



参考文章  Hive指南之Hive SQL的编译过程.md











Hive官网

-  https://cwiki.apache.org/confluence/display/Hive/Home 
- [Hive中metastore（元数据存储）三种方式区别和搭建](https://blog.csdn.net/qq_25371579/article/details/50865990)
- [Hive原理详解](https://blog.csdn.net/ForgetThatNight/article/details/79632364)
 https://blog.csdn.net/qq_37142346/article/details/79873376 
 http://www.ccblog.cn/69.htm 
 https://blog.csdn.net/zyzzxycj/article/details/79267635 
 https://blog.csdn.net/u010003835/article/details/88236132 
 https://blog.csdn.net/lifuxiangcaohui/article/details/50252897 

- [Hive SQL的编译过程](https://tech.meituan.com/2014/02/12/hive-sql-to-mapreduce.html)