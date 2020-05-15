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
-  SequenceFile的文件结构图 

![1589464569541](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\big_data\Hive指南.assets\1589464569541.png)

- Header通用头文件格式：

| SEQ|3BYTE|
| ---- | ---- |
| Nun|1byte数字|
| keyClassName| |
| ValueClassName| |
| compression|（boolean）指明了在文件中是否启用压缩|
| blockCompression|（boolean，指明是否是block压缩）|
| compression|codec|
| Metadata|文件元数据|
| Sync|头文件结束标志|

- Block-Compressed SequenceFile格式 

![1589464846430](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\big_data\Hive指南.assets\1589464846430.png)





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

![image-20200515091108716](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\hive\Hive指南.assets\image-20200515091108716.png)

- 每个Orc文件由1个或多个stripe组成，每个stripe250MB大小，这个Stripe实际相当于之前的rcfile里的RowGroup概念，不过大小由4MB->250MB，这样应该能提升顺序读的吞吐率。每个Stripe里有三部分组成，分别是Index Data,Row Data,Stripe Footer：
  -  Index Data：一个轻量级的index，默认是每隔1W行做一个索引。这里做的索引应该只是记录某行的各字段在Row Data中的offset，据说还包括每个Column的max和min值；
  -  Row Data：存的是具体的数据，和RCfile一样，先取部分行，然后对这些行按列进行存储。与RCfile不同的地方在于每个列进行了编码，分成多个Stream来存储；
  -   Stripe Footer：存的是各个Stream的类型，长度等信息。
-  每个文件有一个File Footer，这里面存的是每个Stripe的行数，每个Column的数据类型信息等；每个文件的尾部是一个PostScript，这里面记录了整个文件的压缩类型以及FileFooter的长度信息等。在读取文件时，会seek到文件尾部读PostScript，从里面解析到File Footer长度，再读FileFooter，从里面解析到各个Stripe信息，再读各个Stripe，即从后往前读。

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
| ---- | ---- | ---- | ---- |
| TextFile | 7.3 G | 105s | 370s |
| Parquet | 769.0 M | 28s  | 195s |
| ORC  | 246.0 M | 34s  | 310s |
| Sequence | 7.8 G | 135s | 385s |
| RC   | 6.9 G | 92s  | 330s |
| AVRO | 8.0G | 240s | 530s |

- ORC的存储于执行效率均是最优的

####  对比

- textfile 存储空间消耗比较大，并且压缩的text 无法分割和合并 查询的效率最低,可以直接存储，加载数据的速度最高
- sequencefile 存储空间消耗最大,压缩的文件可以分割和合并 需要通过text文件转化来加载
- rcfile 存储空间小，查询的效率高 ，需要通过text文件转化来加载，加载的速度最低
- orc 存储空间最小，查询的最高 ，需要通过text文件转化来加载，加载的速度最低（个人建议使用orc）



### 2.几种类型的压缩格式



 