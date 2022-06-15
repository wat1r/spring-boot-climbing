## 1.集合类
### 1.0.提纲
![在这里插入图片描述](https://img-blog.csdnimg.cn/10697ada19a8473d8e0348e4e80c88d1.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/87a327a9b6874f43a6f07d97d8d9b228.png)

这些抽象类为集合增加了很多功能：
- HashSet：实现 Set 接口，不允许重复的元素，底层数据结构 hash table
- LinkedHashSet：实现 Set 接口，不允许重复的元素，底层数据结构 hash table 与双链表
- TreeSet：实现 NavigableSet 接口，不允许重复的元素，底层数据结构红黑树
- ArrayList：实现 List 接口，允许重复元素，底层数据结构可变数组
- LinkedList：实现 List 接口，允许重复元素，底层数据结构双链表
- Vector：实现 List 接口，允许重复元素，底层数据结构可变数组
- HashMap：实现 Map 接口，不允许重复的 key，底层数据结构 hash table
- LinkedHashMap：实现 Map 接口，不允许重复的 key，底层数据结构 hash table 与双链表
- HashTable：实现 Map 接口，不允许重复的 key，底层数据结构 hash table
- TreeMap：实现 SortedMap 接口，不允许重复的 key，底层数据结构红黑树


####  [Java中常见数据结构：list与map -底层如何实现](https://www.cnblogs.com/nucdy/p/5867210.html)
### 1.1.[HashMap连击](https://blog.csdn.net/lch_2016/article/details/81045480)

#### [Java进阶（六）从ConcurrentHashMap的演进看Java多线程核心技术](http://www.jasongj.com/java/concurrenthashmap/)


#### 1.1.0.[HashMap初始容量为什么是2的n次幂及扩容为什么是2倍的形式](https://blog.csdn.net/apeopl/article/details/88935422)
当HashMap的容量是2的n次幂时，(n-1)的2进制也就是1111111***111这样形式的，这样与添加元素的hash值进行位运算时，能够充分的散列，使得添加的元素均匀分布在HashMap的每个位置上，减少hash碰撞



#### 1.1.1.HashMap 是不是有序的？不是有序的
有没有有序的Map实现类呢？ TreeMap 和 LinkedHashMap
TreeMap 和 LinkedHashMap 是如何保证它的顺序的？ TreeMap 是通过实现 SortMap 接口，能够把它保存的键值对根据 key 排序，基于红黑树，从而保证 TreeMap 中所有键值对处于有序状态。LinkedHashMap 则是通过插入排序（就是你 put 的时候的顺序是什么，取出来的时候就是什么样子）和访问排序（改变排序把访问过的放到底部）让键值有序。

#### 1.1.2.为什么用HashMap？
HashMap 是一个散列桶（数组和链表），它存储的内容是键值对 key-value 映射
HashMap 采用了数组和链表的数据结构，能在查询和修改方便继承了数组的线性查找和链表的寻址修改
HashMap 是非 synchronized，所以 HashMap 很快
HashMap 可以接受 null 键和值，而 Hashtable 则不能（原因就是 equlas() 方法需要对象，因为 HashMap 是后出的 API 经过处理才可以）

#### 1.1.3.HashMap 的工作原理是什么？
HashMap 是基于 hashing 的原理
我们使用 put(key, value) 存储对象到 HashMap 中，使用 get(key) 从 HashMap 中获取对象。当我们给 put() 方法传递键和值时，我们先对键调用 hashCode() 方法，计算并返回的 hashCode 是用于找到 Map 数组的 bucket 位置来储存 Node 对象。
这里关键点在于指出，HashMap 是在 bucket 中储存键对象和值对象，作为Map.Node 。
![在这里插入图片描述](https://img-blog.csdnimg.cn/78c4d873bf814560a1e2b7b984fe144e.png)
以下是具体的 put 过程（JDK1.8）
- 1.对 Key 求 Hash 值，然后再计算下标
- 2.如果没有碰撞，直接放入桶中（碰撞的意思是计算得到的 Hash 值相同，需要放到同一个 bucket 中）
- 3.如果碰撞了，以链表的方式链接到后面
- 4.如果链表长度超过阀值（TREEIFY THRESHOLD==8），就把链表转成红黑树，链表长度低于6，就把红黑树转回链表
- 5.如果节点已经存在就替换旧值
- 6.如果桶满了（容量16*加载因子0.75），就需要 resize（扩容2倍后重排）
以下是具体 get 过程
考虑特殊情况：如果两个键的 hashcode 相同，你如何获取值对象？
当我们调用 get() 方法，HashMap 会使用键对象的 hashcode 找到 bucket 位置，找到 bucket 位置之后，会调用 keys.equals() 方法去找到链表中正确的节点，最终找到要找的值对象。

![在这里插入图片描述](https://img-blog.csdnimg.cn/5af9bfb582ca49428ffe85c5c929db49.png)

#### 1.1.4.有什么方法可以减少碰撞？
扰动函数可以减少碰撞
原理是如果两个不相等的对象返回不同的 hashcode 的话，那么碰撞的几率就会小些。这就意味着存链表结构减小，这样取值的话就不会频繁调用 equal 方法，从而提高 HashMap 的性能（扰动即 Hash 方法内部的算法实现，目的是让不同对象返回不同hashcode）。
使用不可变的、声明作 final 对象，并且采用合适的 equals() 和 hashCode() 方法，将会减少碰撞的发生
不可变性使得能够缓存不同键的 hashcode，这将提高整个获取对象的速度，使用 String、Integer 这样的 wrapper 类作为键是非常好的选择。
为什么 String、Integer 这样的 wrapper 类适合作为键？
因为 String 是 final，而且已经重写了 equals() 和 hashCode() 方法了。不可变性是必要的，因为为了要计算 hashCode()，就要防止键值改变，如果键值在放入时和获取时返回不同的 hashcode 的话，那么就不能从 HashMap 中找到你想要的对象。

#### 1.1.5.HashMap 中 hash 函数怎么是实现的?
我们可以看到，在 hashmap 中要找到某个元素，需要根据 key 的 hash 值来求得对应数组中的位置。如何计算这个位置就是 hash 算法。
前面说过，hashmap 的数据结构是数组和链表的结合，所以我们当然希望这个 hashmap 里面的元素位置尽量的分布均匀些，尽量使得每个位置上的元素数量只有一个。那么当我们用 hash 算法求得这个位置的时候，马上就可以知道对应位置的元素就是我们要的，而不用再去遍历链表。 所以，我们首先想到的就是把 hashcode 对数组长度取模运算。这样一来，元素的分布相对来说是比较均匀的。
但是“模”运算的消耗还是比较大的，能不能找一种更快速、消耗更小的方式？我们来看看 JDK1.8 源码是怎么做的（被楼主修饰了一下）

![在这里插入图片描述](https://img-blog.csdnimg.cn/80000be2c70043ec8f5beaed873f6cf6.png)
简单来说就是：
高16 bit 不变，低16 bit 和高16 bit 做了一个异或（得到的 hashcode 转化为32位二进制，前16位和后16位低16 bit和高16 bit做了一个异或）
(n·1) & hash = -> 得到下标

#### 1.1.6.拉链法导致的链表过深，为什么不用二叉查找树代替而选择红黑树？为什么不一直使用红黑树？
之所以选择红黑树是为了解决二叉查找树的缺陷：二叉查找树在特殊情况下会变成一条线性结构（这就跟原来使用链表结构一样了，造成层次很深的问题），遍历查找会非常慢。而红黑树在插入新数据后可能需要通过左旋、右旋、变色这些操作来保持平衡。引入红黑树就是为了查找数据快，解决链表查询深度的问题。我们知道红黑树属于平衡二叉树，为了保持“平衡”是需要付出代价的，但是该代价所损耗的资源要比遍历线性链表要少。所以当长度大于8的时候，会使用红黑树；如果链表长度很短的话，根本不需要引入红黑树，引入反而会慢。

#### 1.1.7.说说你对红黑树的见解？

![在这里插入图片描述](https://img-blog.csdnimg.cn/3ba029217edd45c58e706c2926dba1b2.png)
- 每个节点非红即黑
- 根节点总是黑色的
- 如果节点是红色的，则它的子节点必须是黑色的（反之不一定）
- 每个叶子节点都是黑色的空节点（NIL节点）
- 从根节点到叶节点或空子节点的每条路径，必须包含相同数目的黑色节点（即相同的黑色高度）

#### 1.1.8.解决 hash 碰撞还有那些办法？
**开放定址法**
当冲突发生时，使用某种探查技术在散列表中形成一个探查（测）序列。沿此序列逐个单元地查找，直到找到给定的地址。按照形成探查序列的方法不同，可将开放定址法区分为线性探查法、二次探查法、双重散列法等。
下面给一个线性探查法的例子：
问题：已知一组关键字为 (26，36，41，38，44，15，68，12，06，51)，用除余法构造散列函数，用线性探查法解决冲突构造这组关键字的散列表。
解答：为了减少冲突，通常令装填因子 α 由除余法因子是13的散列函数计算出的上述关键字序列的散列地址为 (0，10，2，12，5，2，3，12，6，12)。
前5个关键字插入时，其相应的地址均为开放地址，故将它们直接插入 T[0]、T[10)、T[2]、T[12] 和 T[5] 中。
当插入第6个关键字15时，其散列地址2（即 h(15)=15％13=2）已被关键字 41（15和41互为同义词）占用。故探查 h1=(2+1)％13=3，此地址开放，所以将 15 放入 T[3] 中。
当插入第7个关键字68时，其散列地址3已被非同义词15先占用，故将其插入到T[4]中。
当插入第8个关键字12时，散列地址12已被同义词38占用，故探查 hl=(12+1)％13=0，而 T[0] 亦被26占用，再探查 h2=(12+2)％13=1，此地址开放，可将12插入其中。
类似地，第9个关键字06直接插入 T[6] 中；而最后一个关键字51插人时，因探查的地址 12，0，1，…，6 均非空，故51插入 T[7] 中。

散列表要解决的一个问题就是散列值的冲突问题，通常是两种方法：链表法和开放地址法。链表法就是将相同hash值的对象组织成一个链表放在hash值对应的槽位；开放地址法是通过一个探测算法，当某个槽位已经被占据的情况下继续查找下一个可以使用的槽位。





#### 1.1.9.如果 HashMap 的大小超过了负载因子（load factor）定义的容量怎么办？
HashMap 默认的负载因子大小为0.75。也就是说，当一个 Map 填满了75%的 bucket 时候，和其它集合类一样（如 ArrayList 等），将会创建原来 HashMap 大小的两倍的 bucket 数组来重新调整 Map 大小，并将原来的对象放入新的 bucket 数组中。这个过程叫作 rehashing。
因为它调用 hash 方法找到新的 bucket 位置。这个值只可能在两个地方，一个是原下标的位置，另一种是在下标为 <原下标+原容量> 的位置。

#### 1.1.10.重新调整 HashMap 大小存在什么问题吗？
重新调整 HashMap 大小的时候，确实存在条件竞争。
因为如果两个线程都发现 HashMap 需要重新调整大小了，它们会同时试着调整大小。在调整大小的过程中，存储在链表中的元素的次序会反过来。因为移动到新的 bucket 位置的时候，HashMap 并不会将元素放在链表的尾部，而是放在头部。这是为了避免尾部遍历（tail traversing）。如果条件竞争发生了，那么就死循环了。多线程的环境下不使用 HashMap。
为什么多线程会导致死循环，它是怎么发生的？
HashMap 的容量是有限的。当经过多次元素插入，使得 HashMap 达到一定饱和度时，Key 映射位置发生冲突的几率会逐渐提高。这时候， HashMap 需要扩展它的长度，也就是进行Resize。
扩容：创建一个新的 Entry 空数组，长度是原数组的2倍
rehash：遍历原 Entry 数组，把所有的 Entry 重新 Hash 到新数组
流程图：https://www.cnblogs.com/zhuoqingsen/p/8577646.html

#### 1.1.11.HashTable
- 数组 + 链表方式存储
- 默认容量：11（质数为宜）
- put操作：首先进行索引计算 （key.hashCode() & 0x7FFFFFFF）% table.length；若在链表中找到了，则替换旧值，若未找到则继续；当总元素个数超过 容量 * 加载因子 时，扩容为原来 2 倍并重新散列；将新元素加到链表头部
- 对修改 Hashtable 内部共享数据的方法添加了 synchronized，保证线程安全

#### 1.1.12.HashMap 与 HashTable 区别
- 默认容量不同，扩容不同
- 线程安全性：HashTable 安全
- 效率不同：HashTable 要慢，因为加锁

#### 1.1.13.可以使用 CocurrentHashMap 来代替 Hashtable 吗？
- 我们知道 Hashtable 是 synchronized 的，但是 ConcurrentHashMap 同步性能更好，因为它仅仅根据同步级别对 map 的一部分进行上锁
- ConcurrentHashMap 当然可以代替 HashTable，但是 HashTable 提供更强的线程安全性
- 它们都可以用于多线程的环境，但是当 Hashtable 的大小增加到一定的时候，性能会急剧下降，因为迭代时需要被锁定很长的时间。由于 ConcurrentHashMap 引入了分割（segmentation），不论它变得多么大，仅仅需要锁定 Map 的某个部分，其它的线程不需要等到迭代完成才能访问 Map。简而言之，在迭代的过程中，ConcurrentHashMap 仅仅锁定 Map 的某个部分，而 Hashtable 则会锁定整个 Map

#### 1.1.14.CocurrentHashMap（JDK 1.7）
- CocurrentHashMap 是由 Segment 数组和 HashEntry 数组和链表组成
- Segment 是基于重入锁（ReentrantLock）：一个数据段竞争锁。每个 HashEntry 一个链表结构的元素，利用 Hash 算法得到索引确定归属的数据段，也就是对应到在修改时需要竞争获取的锁。ConcurrentHashMap 支持 CurrencyLevel（Segment 数组数量）的线程并发。每当一个线程占用锁访问一个 Segment 时，不会影响到其他的 Segment
- 核心数据如 value，以及链表都是 volatile 修饰的，保证了获取时的可见性
- 首先是通过 key 定位到 Segment，之后在对应的 Segment 中进行具体的 put 操作如下：
	- 将当前 Segment 中的 table 通过 key 的 hashcode 定位到 HashEntry。
	- 遍历该 HashEntry，如果不为空则判断传入的  key 和当前遍历的 key 是否相等，相等则覆盖旧的 value
	- 不为空则需要新建一个 HashEntry 并加入到 Segment 中，同时会先判断是否需要扩容
	- 最后会解除在 1 中所获取当前 Segment 的锁。
- 虽然 HashEntry 中的 value 是用 volatile 关键词修饰的，但是并不能保证并发的原子性，所以 put 操作时仍然需要加锁处理
首先第一步的时候会尝试获取锁，如果获取失败肯定就有其他线程存在竞争，则利用 scanAndLockForPut() 自旋获取锁。
- 尝试自旋获取锁
- 如果重试的次数达到了 MAX_SCAN_RETRIES 则改为阻塞锁获取，保证能获取成功。最后解除当前 Segment 的锁



### 1.2.什么是快速失败(fail-fast)、能举个例子吗？什么是安全失败(fail-safe)呢？


快速失败(fail-fast)

快速失败(fail-fast)是 Java 集合的一种错误检测机制。在使用迭代器对集合进行遍历的时候，我们在多线程下操作非安全失败(fail-safe)的集合类可能就会触发 fail-fast 机制，导致抛出ConcurrentModificationException 异常。另外，在单线程下，如果在遍历过程中对集合对象的内容进行了修改的话也会触发 fail-fast 机制。

举个例子：多线程下，如果线程 1 正在对集合进行遍历，此时线程 2 对集合进行修改（增加、删除、修改），或者线程 1 在遍历过程中对集合进行修改，都会导致线程 1 抛出异常。

安全失败(fail-safe)

采用安全失败机制的集合容器，在遍历时不是直接在集合内容上访问的，而是先复制原有集 合内容，在拷贝的集合上进行遍历。所以，在遍历过程中对原集合所作的修改并不能被迭代器检测到，故不会抛 ConcurrentModificationException



### 1.3.讲一下 CopyOnWriteArrayList 和 CopyOnWriteArraySet?

CopyOnWrite 容器：
写时复制的容器。当我们往一个容器添加元素的时候，不直接往当前容 器添加，而是先将当前容器进行 Copy，复制出一个新的容器，然后新的容器里添加元素，添 加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对 CopyOnWrite 容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。所以 CopyOnWrite 容器也是一种读写分离的思想，读和写不同的容器。以下代码是向 ArrayList 里添加元素，可以发现在添加的时候是需要加锁的，否则多线程写的时候会 Copy 出N个副本出来。

```java
public boolean add(T e) {  
	final ReentrantLock lock = this.lock; lock.lock();
    try {     
        Object[] elements = getArray(); int len = elements.length;// 复制出新数组 
        Object[] newElements = Arrays.copyOf(elements, len + 1); //把新元素添加到新数组里   
        newElements[len] = e; // 把原数组引用指向新数组
        setArray(newElements); 
        return true;
    } finally { 
        lock.unlock();
    } 
}            

final void setArray(Object[] a) {
    array = a;
}           
```


读的时候不需要加锁，如果读的时候有多个线程正在向 ArrayList 添加数据，读还是会读到旧的数据，因为写的时候不会锁住旧的 ArrayList。

```java
 public E get(int index) { 
	return get(getArray(), index);
 }            

```

CopyOnWrite 并发容器用于读多写少的并发场景。

CopyOnWrite 的缺点
CopyOnWrite容 器有很多优点，但是同时也存在两个问题，即 内存占用问题 和 数据一致性问题。所以在开发的时候需要注意。

内存占用问题。因为 CopyOnWrite 的写时复制机制，所以在进行写操作的时候，内存里会同时驻扎两个对象的内存，旧的对象和新写入的对象（注意:在复制的时候只是复制容器里的引 用，只是在写的时候会创建新对象添加到新容器里，而旧容器的对象还在使用，所以有两份 对象内存）。如果这些对象占用的内存比较大，比如说 200M 左右，那么再写入 100M 数据进去，内存就会占用 300M，那么这个时候很有可能造成频繁的 Yong GC 和 Full GC。针对内存占用问题，可以通过压缩容器中的元素的方法来减少大对象的内存消耗，比如，如 果元素全是10进制的数字，可以考虑把它压缩成36进制或64进制。或者不使用 CopyOnWrite 容器，而使用其他的并发容器，如 ConcurrentHashMap。

### 14、CocurrentHashMap（JDK 1.8）

CocurrentHashMap 抛弃了原有的 Segment 分段锁，采用了 CAS + synchronized 来保证并发安全性。其中的 val next 都用了 volatile 修饰，保证了可见性。
最大特点是引入了 CAS
借助 Unsafe 来实现 native code。CAS有3个操作数，内存值 V、旧的预期值 A、要修改的新值 B。当且仅当预期值 A 和内存值 V 相同时，将内存值V修改为 B，否则什么都不做。Unsafe 借助 CPU 指令 cmpxchg 来实现。
CAS 使用实例
对 sizeCtl 的控制都是用 CAS 来实现的：
- -1 代表 table 正在初始化
- N 表示有 -N-1 个线程正在进行扩容操作
- 如果 table 未初始化，表示table需要初始化的大小
- 如果 table 初始化完成，表示table的容量，默认是table大小的0.75倍，用这个公式算 0.75（n – (n >>> 2)）
	**CAS 会出现的问题：ABA**
	解决：对变量增加一个版本号，每次修改，版本号加 1，比较的时候比较版本号。
	put 过程
	- 根据 key 计算出 hashcode
	- 判断是否需要进行初始化
	- 通过 key 定位出的 Node，如果为空表示当前位置可以			写入数据，利用 CAS 尝试写入，失败则自旋保证成功
	- 如果当前位置的 hashcode == MOVED == -1,则需要进行扩容
	- 如果都不满足，则利用 synchronized 锁写入数据
	- 如果数量大于 TREEIFY_THRESHOLD 则要转换为红黑树
	get 过程
	- 根据计算出来的 hashcode 寻址，如果就在桶上那么直接返回值
	- 如果是红黑树那就按照树的方式获取值
	- 就不满足那就按照链表的方式遍历获取值

![在这里插入图片描述](https://img-blog.csdnimg.cn/11905e38ee2b4f60afb719b0da43dae1.png)

### 15.hashmap线程不安全，请问为什么线程不安全？
会形成循环链表
https://blog.csdn.net/chisunhuang/article/details/79041656

### 16.使用红黑树为什么能提高查询的性能？
红黑树(red-black tree) 是一棵满足下述性质的二叉查找树：
1. 每一个结点要么是红色，要么是黑色。
2. 根结点是黑色的。
3. 所有叶子结点都是黑色的（实际上都是Null指针，下图用NIL表示）。叶子结点不包含任何关键字信息，所有查询关键字都在非终结点上。
4. 每个红色结点的两个子节点必须是黑色的。换句话说：从每个叶子到根的所有路径上不能有两个连续的红色结点
5. 从任一结点到其每个叶子的所有路径都包含相同数目的黑色结点

**红黑树相关定理**
1. 从根到叶子的最长的可能路径不多于最短的可能路径的两倍长。
      根据上面的性质5我们知道上图的红黑树每条路径上都是3个黑结点。因此最短路径长度为2(没有红结点的路径)。再根据性质4(两个红结点不能相连)和性质1，2(叶子和根必须是黑结点)。那么我们可以得出：一条具有3个黑结点的路径上最多只能有2个红结点(红黑间隔存在)。也就是说黑深度为2（根结点也是黑色）的红黑树最长路径为4，最短路径为2。从这一点我们可以看出红黑树是 大致平衡的。 (当然比平衡二叉树要差一些，AVL的平衡因子最多为1)

2. 红黑树的树高(h)不大于两倍的红黑树的黑深度(bd)，即h<=2bd
      根据定理1，我们不难说明这一点。bd是红黑树的最短路径长度。而可能的最长路径长度(树高的最大值)就是红黑相间的路径，等于2bd。因此h<=2bd。

3. 一棵拥有n个内部结点(不包括叶子结点)的红黑树的树高h<=2log(n+1)
      下面我们首先证明一颗有n个内部结点的红黑树满足n>=2^bd-1。这可以用数学归纳法证明，施归纳于树高h。当h=0时，这相当于是一个叶结点，黑高度bd为0，而内部结点数量n为0，此时0>=2^0-1成立。假设树高h<=t时，n>=2^bd-1成立，我们记一颗树高 为t+1的红黑树的根结点的左子树的内部结点数量为nl，右子树的内部结点数量为nr，记这两颗子树的黑高度为bd'（注意这两颗子树的黑高度必然一 样），显然这两颗子树的树高<=t，于是有nl>=2^bd'-1以及nr>=2^bd'-1，将这两个不等式相加有nl+nr>=2^(bd'+1)-2，将该不等式左右加1，得到n>=2^(bd'+1)-1，很显然bd'+1>=bd，于是前面的不等式可以 变为n>=2^bd-1，这样就证明了一颗有n个内部结点的红黑树满足n>=2^bd-1。
        在根据定理2，h<=2bd。即n>=2^(h/2)-1，那么h<=2log(n+1)
        从这里我们能够看出，红黑树的查找长度最多不超过2log(n+1)，因此其查找时间复杂度也是O(log N)级别的。

红黑树的操作

因为每一个红黑树也是一个特化的二叉查找树，因此红黑树上的查找操作与普通二叉查找树上的查找操作相同。然而，在红黑树上进行插入操作和删除操作会导致不 再符合红黑树的性质。恢复红黑树的属性需要少量(O(log n))的颜色变更(实际是非常快速的)和不超过三次树旋转(对于插入操作是两次)。 虽然插入和删除很复杂，但操作时间仍可以保持为 O(log n) 次 。

红黑树能够以O(log2(N))的时间复杂度进行搜索、插入、删除操作。此外,任何不平衡都会在3次旋转之内解决。这一点是AVL所不具备的。
而且实际应用中，很多语言都实现了红黑树的数据结构。比如 TreeMap, TreeSet(Java )、 STL(C++)等。

### 17.HashMap到底是插入链表头部还是尾部
在jdk1.8之前是插入头部的，在jdk1.8中是插入尾部的。



## 1.2.List连击
#### 1.2.1.ArrayList 的优缺点
ArrayList的优点如下：
ArrayList 底层以数组实现，是一种随机访问模式。- ArrayList 实现了 RandomAccess 接口，因此查找的时候非常快。
ArrayList 在顺序添加一个元素的时候非常方便。
ArrayList 的缺点如下：
删除元素的时候，需要做一次元素复制操作。如果要复制的元素很多，那么就会比较耗费性能。
插入元素的时候，也需要做一次元素复制操作，缺点同上。
ArrayList 比较适合顺序添加、随机访问的场景。

####  1.2.2.插入数据时，ArrayList、LinkedList、Vector谁速度较快？
ArrayList、Vector 底层的实现都是使用数组方式存储数据。数组元素数大于实际存储的数据以便增加和插入元素，它们都允许直接按序号索引元素，但是插入元素要涉及数组元素移动等内存操作，所以索引数据快而插入数据慢。
Vector 中的方法由于加了 synchronized 修饰，因此 Vector 是线程安全容器，但性能上较ArrayList差。
LinkedList 使用双向链表实现存储，按序号索引数据需要进行前向或后向遍历，但插入数据时只需要记录当前项的前后项即可，所以 LinkedList 插入速度较快




## 2.高级篇






### 2.1.IO/NIO

同步与异步
同步： 同步就是发起一个调用后，被调用者未处理完请求之前，调用不返回。
异步： 异步就是发起一个调用后，立刻得到被调用者的回应表示已接收到请求，但是被调用者并没有返回结果，此时我们可以处理其他的请求，被调用者通常依靠事件，回调等机制来通知调用者其返回结果。
同步和异步的区别最大在于异步的话调用者不需要等待处理结果，被调用者会通过回调等机制来通知调用者其返回结果。
阻塞和非阻塞
阻塞： 阻塞就是发起一个请求，调用者一直等待请求结果返回，也就是当前线程会被挂起，无法从事其他任务，只有当条件就绪才能继续。
非阻塞： 非阻塞就是发起一个请求，调用者不用一直等着结果返回，可以先去干其他事情。
举个生活中简单的例子，你妈妈让你烧水，小时候你比较笨啊，在哪里傻等着水开（同步阻塞）。等你稍微再长大一点，你知道每次烧水的空隙可以去干点其他事，然后只需要时不时来看看水开了没有（同步非阻塞）。后来，你们家用上了水开了会发出声音的壶，这样你就只需要听到响声后就知道水开了，在这期间你可以随便干自己的事情，你需要去倒水了（异步非阻塞）。

#### 2.1.1.IO/NIO连击
1.NIO的特性/NIO与IO区别
1)Non-blocking IO（非阻塞IO）
IO流是阻塞的，NIO流是不阻塞的。
Java NIO使我们可以进行非阻塞IO操作。比如说，单线程中从通道读取数据到buffer，同时可以继续做别的事情，当数据读取到buffer中后，线程再继续处理数据。写数据也是一样的。另外，非阻塞写也是如此。一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。
Java IO的各种流是阻塞的。这意味着，当一个线程调用read()或write()时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程在此期间不能再干任何事情了
2)Buffer(缓冲区)
IO 面向流(Stream oriented)，而 NIO 面向缓冲区(Buffer oriented)。
Buffer是一个对象，它包含一些要写入或者要读出的数据。在NIO类库中加入Buffer对象，体现了新库与原I/O的一个重要区别。在面向流的I/O中·可以将数据直接写入或者将数据直接读到 Stream 对象中。虽然 Stream 中也有 Buffer 开头的扩展类，但只是流的包装类，还是从流读到缓冲区，而 NIO 却是直接读到 Buffer 中进行操作。
在NIO厍中，所有数据都是用缓冲区处理的。在读取数据时，它是直接读到缓冲区中的; 在写入数据时，写入到缓冲区中。任何时候访问NIO中的数据，都是通过缓冲区进行操作。
最常用的缓冲区是 ByteBuffer,一个 ByteBuffer 提供了一组功能用于操作 byte 数组。除了ByteBuffer,还有其他的一些缓冲区，事实上，每一种Java基本类型（除了Boolean类型）都对应有一种缓冲区。
3)Channel (通道)
NIO 通过Channel（通道） 进行读写。
通道是双向的，可读也可写，而流的读写是单向的。无论读写，通道只能和Buffer交互。因为 Buffer，通道可以异步地读写。
4)Selectors(选择器)
NIO有选择器，而IO没有。
选择器用于使用单个线程处理多个通道。因此，它需要较少的线程来处理这些通道。线程之间的切换对于操作系统来说是昂贵的。 因此，为了提高系统效率选择器是有用的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/96865d6d9cc84fd4b7041b2bca8b0314.png)
2.NIO 读数据和写数据方式
通常来说NIO中的所有IO都是从 Channel（通道） 开始的。
从通道进行数据读取 ：创建一个缓冲区，然后请求通道读取数据。
从通道进行数据写入 ：创建一个缓冲区，填充数据，并要求通道写入数据。
数据读取和写入操作图示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/98aff87e4f11492fbcf9d0bf299e5d2b.png)


3.AIO (Asynchronous I/O)
AIO 也就是 NIO 2。在 Java 7 中引入了 NIO 的改进版 NIO 2,它是异步非阻塞的IO模型。异步 IO 是基于事件和回调机制实现的，也就是应用操作之后会直接返回，不会堵塞在那里，当后台处理完成，操作系统会通知相应的线程进行后续的操作。
AIO 是异步IO的缩写，虽然 NIO 在网络操作中，提供了非阻塞的方法，但是 NIO 的 IO 行为还是同步的。对于 NIO 来说，我们的业务线程是在 IO 操作准备好时，得到通知，接着就由这个线程自行进行 IO 操作，IO操作本身是同步的。
《漫话：如何给女朋友解释什么是Linux的五种IO模型？》 


https://github.com/Snailclimb/JavaGuide/blob/master/Java%E7%9B%B8%E5%85%B3/Java%20IO%E4%B8%8ENIO.md

#### BIO、NIO、AIO

BIO (Blocking I/O)：

同步阻塞 I/O 模式，数据的读取写入必须阻塞在一个线程内等待其完成。

NIO (Non-blocking/New I/O):

NIO 是一种同步非阻塞的 I/O 模型，对应 java.nio 包，提供了 Channel , Selector，Buffer 等抽象。Java NIO使我们可以进行非阻塞IO操作。比如说， 单线程中从通道读取数据到buffer，同时可以继续做别的事情，当数据读取到buffer中后， 线程再继续处理数据。写数据也是一样的。另外，非阻塞写也是如此。一个线程请求写入一 些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。JDK 的 NIO 底层由 epoll 实现。

通常来说 NIO 中的所有 IO 都是从 Channel（通道） 开始的。

从通道进行数据读取 ：创建一个缓冲区，然后请求通道读取数据。

从通道进行数据写入 ：创建一个缓冲区，填充数据，并要求通道写入数据。

AIO (Asynchronous I/O)：异步非阻塞IO模型，异步 IO 是基于事件和回调机制实现的，也就是应用操作之后会直接返回，不会堵塞在那里，当后台处理完成，操作系统会通知相应的 线程进行后续的操作。AIO 的应用还不是很广泛。





### 2.2.java1.8的新特性

java8Stream map和flatmap的区别：
https://www.cnblogs.com/wangjing666/p/9999666.html
https://www.jianshu.com/p/a5950652ac39



### 2.3.强引用、弱引用、软引用、虚引用

强引用:被强引用关联的对象不会被回收。使用 new 一个新对象的方式来创建强引用。

```java
 Object obj = new Object();      
```

软引用：被软引用关联的对象只有在内存不够的情况下才会被回收。使用 SoftReference 类来创建软引用。

```java
Object obj = new Object();
SoftReference<Object> sf = new SoftReference<Object>(obj);
obj = null; // 使对象只被软引用关联       
```

弱引用：被弱引用关联的对象一定会被回收，也就是说它只能存活到下一次垃圾回收发 生之前。使用 WeakReference 类来创建弱引用。

```java
Object obj = new Object();
WeakReference<Object> wf = new WeakReference<Object>(obj);
obj = null;
```












### 2.10.一些基础
1.加载顺序：
静态变量和静态语句块优先于实例变量和普通语句块，静态变量和静态语句块的初始化顺序取决于它们在代码中的顺序。
存在继承的情况下，初始化顺序为：
父类（静态变量、静态语句块）
子类（静态变量、静态语句块）
父类（实例变量、普通语句块）
父类（构造函数）
子类（实例变量、普通语句块）
子类（构造函数）
2.异常
Throwable 可以用来表示任何可以作为异常抛出的类，分为两种： Error 和 Exception。其中 Error 用来表示 JVM 无法处理的错误，Exception 分为两种：
受检异常 ：需要用 try...catch... 语句捕获并进行处理，并且可以从异常中恢复；
非受检异常 ：是程序运行时错误，例如除 0 会引发 Arithmetic Exception，此时程序崩溃并且无法恢复。

![在这里插入图片描述](https://img-blog.csdnimg.cn/5457a645b1b34cd7baa771418a71298a.png)

3.泛型
Java 泛型详解
10 道 Java 泛型面试题
根据上面的例子，我们可以总结出一条规律，”Producer Extends, Consumer Super”：
“Producer Extends” – 如果你需要一个只读List，用它来produce T，那么使用? extends T。
“Consumer Super” – 如果你需要一个只写List，用它来consume T，那么使用? super T。
如果需要同时读取以及写入，那么我们就不能使用通配符了。
如何阅读过一些Java集合类的源码，可以发现通常我们会将两者结合起来一起用，比如像下面这样：
```java
public class Collections {
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (int i=0; i<src.size(); i++)
            dest.set(i, src.get(i));
    }
}


```

4.String、StringBuffer、Stringbuild区别、性能比较
https://blog.csdn.net/shenhonglei1234/article/details/54908934