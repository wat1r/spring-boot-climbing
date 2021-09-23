##  生产时间中的经典算法(四)-BitMap

### 1.BitMap的原理

位图（Bitmap），即位（Bit）的集合，是一种数据结构，可用于记录大量的0-1状态，在很多地方都会用到，比如Linux内核（如inode，磁盘块）、Bloom Filter算法等，其优势是可以在一个非常高的空间利用率下保存大量0-1状态。

如何表达1,2,4,6存在：在相应的下标位置将其设置为1，不存在的位设置为0，进而：如何表达8,10,14存在？只需要再开一位

![image-20210923083234161](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923083234161.png)



java中的8种基本类型的字节数：

![image-20210923083911179](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210923083911179.png)



### 2.一些常用场景

#### 2.1.**在2.5亿个整数中找出不重复的整数，注，内存不足以容纳这2.5亿个整数。**

> **BitMap中1bit代表一个数字，1个int = 4Bytes = 4*8bit = 32 bit**

1）方案 1：采用 2-Bitmap（每个数分配 2bit，00 表示不存在，01 表示出现一次，10 表示多次，11 无意义）进行，共需内存 2^32 * 2 bit=1 GB 内存，还可以接受。然后扫描这 2.5亿个整数，查看 Bitmap 中相对应位，如果是 00 变 01，01 变 10，10 保持不变。所描完事后，查看 bitmap，把对应位是 01 的整数输出即可。 2）方案 2：也可采用与第 1 题类似的方法，进行划分小文件的方法。然后在小文件中找出不重复的整数，并排序。然后再进行归并，注意去除重复的元素。

#### 2.2.给40亿个不重复的 unsigned int 的整数，没排过序的，再给一个数，如何快速判断这个数是否在那 40 亿个数当中？

判断集合中存在重复是常见编程任务之一，当集合中数据量比较大时我们通常希望少进行几次扫描，这时双重循环法就不可取了。 位图法比较适合于这种情况，它的做法是按照集合中最大元素 max 创建一个长度为 max+1的新数组，然后再次扫描原数组，遇到几就给新数组的第几位置上 1，如遇到 5 就给新数组的第六个元素置 1，这样下次再遇到 5 想置位时发现新数组的第六个元素已经是 1 了，这说明这次的数据肯定和以前的数据存在着重复。这 种给新数组初始化时置零其后置一的做法类似于位图的处理方法故称位图法。它的运算次数最坏的情况为 2N。如果已知数组的最大值即能事先给新数组定长的话效 率还能提高一倍

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









### Reference

- [Apache Doris 基于 Bitmap的精确去重和用户行为分析](https://zhuanlan.zhihu.com/p/386879362)

- [DorisDB企业版文档](https://www.kancloud.cn/dorisdb/dorisdb/2140965)

- [RoaringBitmap数据结构及原理](https://blog.csdn.net/yizishou/article/details/78342499)

- https://roaringbitmap.org/
- [BitMap的原理以及运用](https://www.cnblogs.com/dragonsuc/p/10993938.html)

- [《Better bitmap performance with Roaring bitmaps》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1402.6407.pdf)
- [《Consistently faster and smaller compressed bitmaps with Roaring》](https://links.jianshu.com/go?to=https%3A%2F%2Farxiv.org%2Fpdf%2F1603.06549.pdf)

