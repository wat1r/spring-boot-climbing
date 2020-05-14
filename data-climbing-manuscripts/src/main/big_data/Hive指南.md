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

- Record Columnar , RCFile是一种行列存储相结合的存储方式 ,其将数据按行分块，保证同一个record在一个块上，避免读一个记录需要读取多个block。其次，块数据列式存储，有利于数据压缩和快速的列存取。
- 优：
  -  能够很好的压缩和快速的查询性能 
- 缺：
  -  不支持模式演进 ， 写操作比较慢，比非列形式的文件格式需要更多的内存空间和计算量 

#### 1.4.ORCFile



