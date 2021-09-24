##  生产时间中的经典算法(四)-BitMap

### 1.BitMap的原理

位图（Bitmap），即位（Bit）的集合，是一种数据结构，可用于记录大量的0-1状态，在很多地方都会用到，比如Linux内核（如inode，磁盘块）、Bloom Filter算法等，其优势是可以在一个非常高的空间利用率下保存大量0-1状态。

如何表达1,2,4,6存在：在相应的下标位置将其设置为1，不存在的位设置为0，进而：如何表达8,10,14存在？只需要再开一位

![image-20210923083234161](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923083234161.png)



java中的8种基本类型的字节数：

![image-20210923083911179](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923083911179.png)



### 2.BitMap一些常见应用场景

#### 2.1.**在2.5亿个整数中找出不重复的整数，注，内存不足以容纳这2.5亿个整数。**

> **BitMap中1bit代表一个数字，1个int = 4Bytes = 4*8bit = 32 bit**

1）方案 1：采用 2-Bitmap（每个数分配 2bit，00 表示不存在，01 表示出现一次，10 表示多次，11 无意义）进行，共需内存 2^32 * 2 bit=1 GB 内存，还可以接受。然后扫描这 2.5亿个整数，查看 Bitmap 中相对应位，如果是 00 变 01，01 变 10，10 保持不变。所描完事后，查看 bitmap，把对应位是 01 的整数输出即可。 2）方案 2：也可采用与第 1 题类似的方法，进行划分小文件的方法。然后在小文件中找出不重复的整数，并排序。然后再进行归并，注意去除重复的元素。

#### 2.2.给40亿个不重复的 unsigned int 的整数，没排过序的，再给一个数，如何快速判断这个数是否在那 40 亿个数当中？

判断集合中存在重复是常见编程任务之一，当集合中数据量比较大时我们通常希望少进行几次扫描，这时双重循环法就不可取了。 位图法比较适合于这种情况，它的做法是按照集合中最大元素 max 创建一个长度为 max+1的新数组，然后再次扫描原数组，遇到几就给新数组的第几位置上 1，如遇到 5 就给新数组的第六个元素置 1，这样下次再遇到 5 想置位时发现新数组的第六个元素已经是 1 了，这说明这次的数据肯定和以前的数据存在着重复。这 种给新数组初始化时置零其后置一的做法类似于位图的处理方法故称位图法。它的运算次数最坏的情况为 2N。如果已知数组的最大值即能事先给新数组定长的话效 率还能提高一倍。



### 3.RoaringBitMap

![image-20210923094708257](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923094708257.png)

#### 主要思路

> **将32位无符号整数按照高16位bucket，即最多可能有2^16=65536个bucket，在原作中被称为container。存储数据时，按照数据的高16位找到container（找不到就会新建一个），再将低16位放入container中。也就是说，一个RoaringBitMap就是很多container的集合。**

![image-20210923090016519](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923090016519.png)



图中示出了三个container：

- 高16位为0000H的container，存储有前1000个62的倍数。
- 高16位为0001H的container，存储有[2^16, 2^16+100)区间内的100个数。
- 高16位为0002H的container，存储有[2×$2^{16}$​, 3×$2^{16}$​)区间内的所有偶数，共$2^{15}$​个









数据结构

每个[RoaringBitmap](https://github.com/RoaringBitmap/RoaringBitmap)中都包含一个RoaringArray：highLowContainer。highLowContainer存储了RoaringBitmap中的全部数据。int类型（32位）的数据会被拆分成两个16位(short)类型来处理

```java
RoaringArray highLowContainer;
```

RoaringArray的数据结构很简单，核心为以下三个成员：

```java
short[] keys;//高16位，keys是有序的，方便后续二分
Container[] values;//低16位
int size;//当前包含的key-value pair的数量，即keys和values中有效数据的数量。
```

#### 四种类型的Container

在创建一个新Container时，如果只插入一个元素，RBM（RoaringBitMap）默认会用ArrayContainer来存储。

当ArrayContainer（其中每一个元素的类型为 short int 占两个字节,且里面的元素都是按从大到小的顺序排列的）的容量超过4096（即8k）后，会自动转成BitmapContainer(这个所占空间始终都是8k)存储。

4096这个阈值的设计很优雅(**这种思想在java的设计中也容易见到，如果链表长度超过阀值（TREEIFY THRESHOLD==8），就把链表转成红黑树，链表长度低于8，就把红黑树转回链表**)，低于它时ArrayContainer比较省空间，高于它时BitmapContainer比较省空间。

也就是说：ArrayContainer存储稀疏数据，BitmapContainer存储稠密数据，可以最大限度地避免内存浪费。如下图

![image-20210923192210557](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923192210557.png)

##### ArrayContainer

```java
static final int DEFAULT_MAX_SIZE = 4096//阈值，超过这个值后Container会转成BitmapContainer
short[] content;//存低16位的value，不存重复的数据，后续二分
```

- ArrayContainer没有压缩，只存储少量数据，占用的空间大小与存储的数据量为线性关系，每个`short`为2字节，因此存储了N个数据的ArrayContainer占用空间大致为`2N`字节。存储一个数据占用2字节，存储4096个数据占用8kb

##### BitmapContainer

```java
final long[] bitmap;
```

- BitmapContainer使用long[]存储位图数据。每个Container处理16位整型的数据，也就是0~65535，因此根据位图的原理，需要65536个比特来存储数据，每个比特位用1来表示有，0来表示无。每个long有64位，因此需要1024（2^12）个long来提供65536个比特。

- 因此，每个BitmapContainer在构建时就会初始化长度为1024的long[]。这就意味着，不管一个BitmapContainer中只存储了1个数据还是存储了65536个数据，占用的空间都是同样的8kb。

##### RunContainer

```java
private short[] valueslength;
int nbrruns = 0;
```

- RunContainer中的Run指的是行程长度压缩算法(Run Length Encoding)，对连续数据有比较好的压缩效果。它的原理是，对于连续出现的数字，只记录初始数字和后续数量。即：

```java
//11，它会压缩为11,0；
//11,12,13,14,15，它会压缩为11,4；
//11,12,13,14,15,21,22，它会压缩为11,4,21,1；
//源码中的short[] values length中存储的就是压缩后的数据。
```

- 这种压缩算法的性能和数据的连续性（紧凑性）关系极为密切，对于连续的100个short，它能从200字节压缩为4字节，但对于完全不连续的100个short，编码完之后反而会从200字节变为400字节。

- 如果要分析RunContainer的容量，我们可以做下面两种极端的假设：
  - 最好情况，即只存在一个数据或只存在一串连续数字，那么只会存储2个short，占用4字节
  - 最坏情况，0~65535的范围内填充所有的奇数位（或所有偶数位），需要存储65536个short，128kb

##### SharedContainer

这种容器它本身是不存储数据的，只是用它来指向ArrayContainer,BitMapContainer或RunContainer,和指针的作用类似，这个指针可以被多个对象拥有，但是指针所指的实质东西是被这多个对象所共享的。

在进行RoaringBitMap之间的拷贝的时候，有时并不需要将一个Container拷贝多份，可以使用SharedContainer来指向实际的Container，然后把SharedContainer赋给多个RoaringBitMap对象持有，这个RoaringBitMap对象就可以根据SharedContainer找到真正存储数据的Container,这可以省去不必要的空间浪费。



RoaringBitMap针对Container的优化策略
创建时：

创建包含单个值的Container时，选用ArrayContainer
创建包含一串连续值的Container时，比较ArrayContainer和RunContainer，选取空间占用较少的
转换：

针对ArrayContainer：

如果插入值后容量超过4096，则自动转换为BitmapContainer。因此正常使用的情况下不会出现容量超过4096的ArrayContainer。
调用runOptimize()方法时，会比较和RunContainer的空间占用大小，选择是否转换为RunContainer。
针对BitmapContainer：

如果删除某值后容量低至4096，则会自动转换为ArrayContainer。因此正常使用的情况下不会出现容量小于4096的BitmapContainer。
调用runOptimize()方法时，会比较和RunContainer的空间占用大小，选择是否转换为RunContainer。
针对RunContainer：

只有在调用runOptimize()方法才会发生转换，会分别和ArrayContainer、BitmapContainer比较空间占用大小，然后选择是否转换。


### 4.DorisDB中使用案例

#### 背景

用户在使用DorisDB进行精确去重分析时，通常会有两种方式：

- 基于明细去重：即传统的count distinct 方式，好处是可以保留明细数据。提高了分析的灵活性。缺点则是需要消耗极大的计算和存储资源，对大规模数据集和查询延迟敏感的去重场景支持不够友好。
- 基于预计算去重：这种方式也是DorisDB推荐的方式。在某些场景中，用户可能不关心明细数据，仅仅希望知道去重后的结果。这种场景可采用预计算的方式进行去重分析，本质上是利用空间换时间，也是MOLAP聚合模型的核心思路。就是将计算提前到数据导入的过程中，减少存储成本和查询时的现场计算成本。并且可以使用RollUp表降维的方式，进一步减少现场计算的数据集大小。

#### 传统Count distinct计算

DorisDB 是基于MPP 架构实现的，在使用count distinct做精准去重时，可以保留明细数据，灵活性较高。但是，由于在查询执行的过程中需要进行多次数据shuffle（不同节点间传输数据，计算去重），会导致性能随着数据量增大而直线下降。

如以下场景。存在表（dt, page, user_id)，需要通过明细数据计算UV。

![image-20210924085710233](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210924085710233.png)

```sql
 select page, count(distinct user_id) as pv from table group by page;
```

对于上图计算 PV 的 SQL，DorisDB 在计算时，会按照下图进行计算，先根据 page 列和 user_id 列 group by,最后再 count。

![image-20210924085824279](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210924085824279.png)

显然，上面的计算方式，**由于数据需要进行多次shuffle，当数据量越来越大时，所需的计算资源就会越来越多，查询也会越来越慢**。使用Bitmap技术，就是为了解决传统count distinct在大量数据场景下的性能问题。

#### 使用bitmap去重及优势

- 假如给定一个数组A, 其取值范围为[0, n)(注: 不包括n), 对该数组去重, 可采用(n+7)/8的字节长度的bitmap, 初始化为全0; 逐个处理数组A的元素, 以A中元素取值作为bitmap的下标, 将该下标的bit置1; 最后统计bitmap中1的个数即为数组A的count distinct结果.

##### 优势

1. 空间:  用bitmap的一个bit位表示对应下标是否存在, 具有极大的空间优势;  比如对int32去重, 使用普通BitMap所需的存储空间只占传统去重的1/32. DorisDB中的BitMap采用Roaring Bitmap的优化实现, 对于稀疏的BitMap, 存储空间会进一步显著降低.
2. 时间:  BitMap的去重涉及的计算包括对给定下标的bit置位, 统计BitMap的置位个数, 分别为O(1)操作和O(n)操作, 并且后者可使用clz, ctz等指令高效计算. 此外, BitMap去重在MPP执行引擎中还可以并行加速处理, 每个计算节点各自计算本地子BitMap,  使用bitor操作将这些子BitMap合并成最终的BitMap, bitor操作比基于sort和基于hash的去重效率要高, 无条件依赖和数据依赖, 可向量化执行。

#### 举个例子

创建一张含有BITMAP列的表，其中visit_users列为聚合列，列类型为BITMAP，聚合函数为BITMAP_UNION

```sql
CREATE TABLE `page_uv` (
  `page_id` INT NOT NULL COMMENT '页面id',
  `visit_date` datetime NOT NULL COMMENT '访问时间',
  `visit_users` BITMAP BITMAP_UNION NOT NULL COMMENT '访问用户id'
) ENGINE=OLAP
AGGREGATE KEY(`page_id`, `visit_date`)
DISTRIBUTED BY HASH(`page_id`) BUCKETS 1
PROPERTIES (
  "replication_num" = "1",
  "storage_format" = "DEFAULT"
);
```

向表中导入数据，采用insert into语句导入

```sql
insert into page_uv values 
(1, '2020-06-23 01:30:30', to_bitmap(13)),
(1, '2020-06-23 01:30:30', to_bitmap(23)),
(1, '2020-06-23 01:30:30', to_bitmap(33)),
(1, '2020-06-23 02:30:30', to_bitmap(13)),
(2, '2020-06-23 01:30:30', to_bitmap(23));
```

在以上数据导入后，在 page_id = 1, visit_date = '2020-06-23 01:30:30'的数据行，visit_user字段包含着3个bitmap元素（13，23，33）

在page_id = 1, visit_date = '2020-06-23 02:30:30'的数据行，visit_user字段包含着1个bitmap元素（13）

在page_id = 2, visit_date = '2020-06-23 01:30:30'的数据行，visit_user字段包含着1个bitmap元素（23）

统计每个页面的UV

```sql
mysql> select page_id, count(distinct visit_users) from page_uv group by page_id;

+-----------+------------------------------+
|  page_id  | count(DISTINCT `visit_user`) |
+-----------+------------------------------+
|         1 |                            3 |
+-----------+------------------------------+
|         2 |                            1 |
+-----------+------------------------------+
```

#### BitMap全局字典

目前，基于BitMap类型的去重机制有一个限制，就是BitMap需要使用整数型类型作为输入。如果用户需要将其他数据类型作为BitMap的输入，那么用户需要自己构建全局字典，将其他类型数据（如字符串类型）通过全局字典映射成为整数类型。构建全局字典有几种思路：

##### 基于Hive表的全局字典

这种方案中全局字典本身是一张 Hive 表，Hive 表有两个列，一个是原始值，一个是编码的 Int 值。全局字典的生成步骤：

1. 将事实表的字典列去重生成临时表
2. 临时表和全局字典进行left join, 悬空的词典项为新value.
3. 对新value进行编码并插入全局字典.
4. 事实表和更新后的全局字典进行left join , 将词典项替换为ID.

采用这种构建全局字典的方式，可以通过 Spark 或者 MR 实现全局字典的更新，和对事实表中 Value 列的替换。相比基于 Trie 树的全局字典，这种方式可以分布式化，还可以实现全局字典复用。

但这种方式构建全局字典有几个点需要注意：原始事实表会被读取多次，而且还有两次 Join，计算全局字典会使用大量额外资源。

##### 基于Trie树构建全局字典

用户还可以使用Trie树自行构建全局字典。Trie 树又叫前缀树或字典树。Trie树中节点的后代存在共同的前缀，可以利用字符串的公共前缀来减少查询时间，可以最大限度地减少字符串比较，所以很适合用来实现字典编码。但Trie树的实现不容易分布式化，在数据量比较大的时候会产生性能瓶颈。

通过构建全局字典，将其他类型的数据转换成为整型数据，就可以利用BitMap对非整型数据列进行精确去重分析了。

### Reference

- [Apache Doris 基于 Bitmap的精确去重和用户行为分析](https://zhuanlan.zhihu.com/p/386879362)

- [DorisDB企业版文档](https://www.kancloud.cn/dorisdb/dorisdb/2140965)

- [RoaringBitmap数据结构及原理](https://blog.csdn.net/yizishou/article/details/78342499)

- https://roaringbitmap.org/
- [BitMap的原理以及运用](https://www.cnblogs.com/dragonsuc/p/10993938.html)

- [《Better bitmap performance with Roaring bitmaps》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1402.6407.pdf)
- [《Consistently faster and smaller compressed bitmaps with Roaring》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1603.06549.pdf)

