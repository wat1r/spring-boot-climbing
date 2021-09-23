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

> 



### Reference

- [Apache Doris 基于 Bitmap的精确去重和用户行为分析](https://zhuanlan.zhihu.com/p/386879362)

- [DorisDB企业版文档](https://www.kancloud.cn/dorisdb/dorisdb/2140965)

- [RoaringBitmap数据结构及原理](https://blog.csdn.net/yizishou/article/details/78342499)

- https://roaringbitmap.org/
- [BitMap的原理以及运用](https://www.cnblogs.com/dragonsuc/p/10993938.html)

- [《Better bitmap performance with Roaring bitmaps》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1402.6407.pdf)
- [《Consistently faster and smaller compressed bitmaps with Roaring》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1603.06549.pdf)

