# JVM集锦

## 1.运行时数据区域

![在这里插入图片描述](https://img-blog.csdnimg.cn/687ee69e7c7a4c0db29b0a9bf67eed22.png)

3.1.1 程序计数器

程序计数器是一块较小的内存空间，可以看作是当前线程所执行的字节码的行号指示器。字节码解释器工作时通过改变这个计数器的值来选取下一条需要执行的字节码指令，分支、循环、跳转、异常处理、线程恢复等功能都需要依赖这个计数器来完。

另外，为了线程切换后能恢复到正确的执行位置，每条线程都需要有一个独立的程序计数器，各线程之间计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存。

从上面的介绍中我们知道程序计数器主要有两个作用：

1. 字节码解释器通过改变程序计数器来依次读取指令，从而实现代码的流程控制，如：顺序执行、选择、循环、异常处理。
2. 在多线程的情况下，程序计数器用于记录当前线程执行的位置，从而当线程被切换回来的时候能够知道该线程上次运行到哪儿了。

注意：程序计数器是唯不会出现  的内存区域，它的生命周期随着线程的创建而创建，随着线程的结束而死亡。

3.1.2.Java 虚拟机栈

与程序计数器一样，Java虚拟机栈也是线程私有的，它的生命周期和线程相同，描述的是 Java 方法执行的内存模型。

Java 内存可以粗糙的区分为堆内存（Heap）和栈内存（Stack）其中栈就是现在说的虚拟机栈，或者说是虚拟机栈中局部变量表部分。 （实际上，Java虚拟机栈是由一个个栈帧组成，而每个栈帧中都拥有局部变量表、操作数栈、动态链接、方法出口信息）

局部变量表主要存放了编译器可知的各种数据类型（boolean、byte、char、short、int、float、long、double）、对象引用（reference类型，它不同于对象本身，可能是一个指向对象起始地址的引用指针，也可能是指向一个代表对象的句柄或其他与此对象相关的位置）。

Java 虚拟机栈会出现两种异常：StackOverFlowError 和 OutOfMemoryError。

- StackOverFlowError： 若Java虚拟机栈的内存大小不允许动态扩展，那么当线程请求栈的深度超过当前Java虚拟机栈的最大深度的时候，就抛出StackOverFlowError异常。
- OutOfMemoryError： 若 Java 虚拟机栈的内存大小允许动态扩展，且当线程请求栈时内存用完了，无法再动态扩展了，此时抛出OutOfMemoryError异常。

Java 虚拟机栈也是线程私有的，每个线程都有各自的Java虚拟机栈，而且随着线程的创建而创建，随着线程的死亡而死亡。

3.1.3.本地方法栈

和虚拟机栈所发挥的作用非常相似，区别是： 虚拟机栈为虚拟机执行 Java 方法 （也就是字节码）服务，而本地方法栈则为虚拟机使用到的 Native 方法服务。 在 HotSpot 虚拟机中和 Java 虚拟机栈合二为一。

本地方法被执行的时候，在本地方法栈也会创建一个栈帧，用于存放该本地方法的局部变量表、操作数栈、动态链接、出口信息。

方法执行完毕后相应的栈帧也会出栈并释放内存空间，也会出现 StackOverFlowError 和 OutOfMemoryError 两种异常。

3.1.4.堆

Java 虚拟机所管理的内存中最大的一块，Java 堆是所有线程共享的一块内存区域，在虚拟机启动时创建。此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例以及数组都在这里分配内存。

Java 堆是垃圾收集器管理的主要区域，因此也被称作GC堆（Garbage Collected Heap）.从垃圾回收的角度，由于现在收集器基本都采用分代垃圾收集算法，所以Java堆还可以细分为：新生代和老年代：再细致一点有：Eden空间、From Survivor、To Survivor空间等。进一步划分的目的是更好地回收内存，或者更快地分配内存。





![在这里插入图片描述](https://img-blog.csdnimg.cn/1d55296315344561818cb9084fd8d056.png)



在 JDK 1.8中移除整个永久代，取而代之的是一个叫元空间（Metaspace）的区域（永久代使用的是JVM的堆内存空间，而元空间使用的是物理内存，直接受到本机的物理内存限制）。

3.1.5.方法区

方法区与 Java 堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做 Non-Heap（非堆），目的应该是与 Java 堆区分开来。

HotSpot 虚拟机中方法区也常被称为 “永久代”，本质上两者并不等价。仅仅是因为 HotSpot 虚拟机设计团队用永久代来实现方法区而已，这样 HotSpot 虚拟机的垃圾收集器就可以像管理 Java 堆一样管理这部分内存了。但是这并不是一个好主意，因为这样更容易遇到内存溢出问题。

相对而言，垃圾收集行为在这个区域是比较少出现的，但并非数据进入方法区后就“永久存在”了。

3.1.6. 运行时常量池

运行时常量池是方法区的一部分。Class 文件中除了有类的版本、字段、方法、接口等描述信息外，还有常量池信息（用于存放编译期生成的各种字面量和符号引用）

既然运行时常量池时方法区的一部分，自然受到方法区内存的限制，当常量池无法再申请到内存时会抛出 OutOfMemoryError 异常。

JDK1.7及之后版本的 JVM 已经将运行时常量池从方法区中移了出来，在 Java 堆（Heap）中开辟了一块区域存放运行时常量池。



![在这里插入图片描述](https://img-blog.csdnimg.cn/3a85dade757e418f8ebaa9bd911d719d.png)



3.1.7. 直接内存

直接内存并不是虚拟机运行时数据区的一部分，也不是虚拟机规范中定义的内存区域，但是这部分内存也被频繁地使用。而且也可能导致OutOfMemoryError异常出现。

JDK1.4中新加入的 NIO(New Input/Output) 类，引入了一种基于通道（Channel） 与缓存区（Buffer） 的 I/O 方式，它可以直接使用Native函数库直接分配堆外内存，然后通过一个存储在 Java 堆中的 DirectByteBuffer 对象作为这块内存的引用进行操作。这样就能在一些场景中显著提高性能，因为避免了在 Java 堆和 Native 堆之间来回复制数据。

本机直接内存的分配不会收到 Java 堆的限制，但是，既然是内存就会受到本机总内存大小以及处理器寻址空间的限制。

http://www.importnew.com/31126.html

**3.2.****垃圾回收（GC）**

**3.2.1.哪些内存需要回收**

那些不可能再被任何途径使用的对象

1、引用计数法

这个算法的实现是，给对象中添加一个引用计数器，每当一个地方引用这个对象时，计数器值+1；当引用失效时，计数器值-1。任何时刻计数值为0的对象就是不可能再被使用的。这种算法使用场景很多，但是，Java中却没有使用这种算法，因为这种算法很难解决对象之间相互引用的情况

2、可达性分析法

这个算法的基本思想是通过一系列称为“GC Roots”的对象作为起始点，从这些节点向下搜索，搜索所走过的路径称为引用链，当一个对象到GC Roots没有任何引用链（即GC Roots到对象不可达）时，则证明此对象是不可用的。在Java语言中可以作为GC Roots的对象包括：

· 虚拟机栈中引用的对象

· 方法区中静态属性引用的对象

· 方法区中常量引用的对象

· 本地方法栈中JNI（即Native方法）引用的对象\





![在这里插入图片描述](https://img-blog.csdnimg.cn/f1231bee23304e3986fc47f7e5ba4ba7.png)

4种引用状态

1、强引用

代码中普遍存在的类似”Object obj = new Object()”这类的引用，只要强引用还存在，垃圾收集器永远不会回收掉被引用的对象

2、软引用

描述有些还有用但并非必需的对象。在系统将要发生内存溢出异常之前，将会把这些对象列进回收范围进行二次回收。如果这次回收还没有足够的内存，才会抛出内存溢出异常。Java中的类SoftReference表示软引用

3、弱引用

描述非必需对象。被弱引用关联的对象只能生存到下一次垃圾回收之前，垃圾收集器工作之后，无论当前内存是否足够，都会回收掉只被弱引用关联的对象。Java中的类WeakReference表示弱引用

4、虚引用

这个引用存在的唯一目的就是在这个对象被收集器回收时收到一个系统通知，被虚引用关联的对象，和其生存时间完全没关系。Java中的类PhantomReference表示虚引用

https://www.cnblogs.com/alias-blog/p/5793108.html

**3.2.2.GC算法**

1.标记—清除算法

标记—清除算法是最基础的收集算法，为了解决引用计数法的问题而提出。它使用了根集的概念，它分为“标记”和“清除”两个阶段：首先标记出所需回收的对象，在标记完成后统一回收掉所有被标记的对象，它的标记过程其实就是前面的根搜索算法中判定垃圾对象的标记过程。

  优点：不需要进行对象的移动，并且仅对不存活的对象进行处理，在存活对象比较多的情况下极为高效。

   缺点：（1）*标记和清除过程的效率都不高。*（这种方法需要使用一个空闲列表来记录所有的空闲区域以及大小。对空闲列表的管理会增加分配对象时的工作量。如图4.1所示。）。（2）*标记清除后会产生大量不连续的内存碎片。*虽然空闲区域的大小是足够的，但却可能没有一个单一区域能够满足这次分配所需的大小，因此本次分配还是会失败（在Java中就是一次OutOfMemoryError）不得不触发另一次垃圾收集动作。如图4.2所示。



![在这里插入图片描述](https://img-blog.csdnimg.cn/f94d2f9a72c34a3d8b9c12d359e1d5c2.png)



2.标记—整理算法

该算法标记的过程与标记—清除算法中的标记过程一样，但对标记后出的垃圾对象的处理情况有所不同，它不是直接对可回收对象进行清理，而是让所有的对象都向一端移动，然后直接清理掉端边界以外的内存。在基于Compacting算法的收集器的实现中，一般增加句柄和句柄表。

   优点：（1）经过整理之后，新对象的分配只需要通过指针碰撞便能完成（Pointer Bumping），相当简单。（2）使用这种方法空闲区域的位置是始终可知的，也不会再有碎片的问题了。

   缺点：GC暂停的时间会增长，因为你需要将所有的对象都拷贝到一个新的地方，还得更新它们的引用地址。



![image-20220617193047402](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220617193047402.png)



3.复制算法

该算法的提出是为了克服句柄的开销和解决堆碎片的垃圾回收。它将内存按容量分为大小相等的两块，每次只使用其中的一块（对象面），当这一块的内存用完了，就将还存活着的对象复制到另外一块内存上面（空闲面），然后再把已使用过的内存空间一次清理掉。

   复制算法比较适合于新生代（短生存期的对象），在老年代（长生存期的对象）中，对象存活率比较高，如果执行较多的复制操作，效率将会变低，所以老年代一般会选用其他算法，如标记—整理算法。一种典型的基于Coping算法的垃圾回收是stop-and-copy算法，它将堆分成对象区和空闲区，在对象区与空闲区的切换过程中，程序暂停执行。

   优点：（1）标记阶段和复制阶段可以同时进行。（2）每次只对一块内存进行回收，运行高效。（3）只需移动栈顶指针，按顺序分配内存即可，实现简单。（4）内存回收时不用考虑内存碎片的出现（得活动对象所占的内存空间之间没有空闲间隔）。

   缺点：需要一块能容纳下所有存活对象的额外的内存空间。因此，可一次性分配的最大内存缩小了一半。



![在这里插入图片描述](https://img-blog.csdnimg.cn/ce503ac943054e18bb06263ca29ff20a.png)



4.Adaptive算法（Adaptive Collector）

   在特定的情况下，一些垃圾收集算法会优于其它算法。基于Adaptive算法的垃圾收集器就是监控当前堆的使用情况，并将选择适当算法的垃圾收集器。

**3.2.3.垃圾回收器**

[**Jvm垃圾回收器（终结篇）**](https://www.cnblogs.com/chenpt/p/9803298.html)



![在这里插入图片描述](https://img-blog.csdnimg.cn/0c4316fadf604e68af689507fdff468a.png)

JVM会从年轻代和年老代各选出一个算法进行组合，连线表示哪些算法可以组合使用

1、Serial(年轻代）

1. 年轻代收集器，可以和Serial Old、CMS组合使用
2. 采用复制算法
3. 使用单线程进行垃圾回收，回收时会导致Stop The World，用户进程停止
4. client模式年轻代默认算法
5. GC日志关键字：DefNew(Default New Generation)
6. 图示（Serial+Serial Old)

![在这里插入图片描述](https://img-blog.csdnimg.cn/83c87dbff815485db1b632a0769ed1c9.png)



2、ParNew(年轻代）

1. 新生代收集器，可以和Serial Old、CMS组合使用
2. 采用复制算法
3. 使用多线程进行垃圾回收，回收时会导致Stop The World，其它策略和Serial一样
4. server模式年轻代默认算法
5. 使用-XX:ParallelGCthreads参数来限制垃圾回收的线程数
6. GC日志关键字：ParNew(Parallel New Generation)
7. 图示（ParNew + Serail Old）

![在这里插入图片描述](https://img-blog.csdnimg.cn/3843f8c739e747d1a39767c96c3d2e2a.png)

3、Parallel Scavenge(年轻代）

1. 新生代收集器，可以和Serial Old、Parallel组合使用，不能和CMS组合使用
2. 采用复制算法
3. 使用多线程进行垃圾回收，回收时会导致Stop The World
4. 关注系统吞吐量

- 1. -XX:MaxGCPauseMillis：设置大于0的毫秒数，收集器尽可能在该时间内完成垃圾回收
  2. -XX:GCTimeRatio：大于0小于100的整数，即垃圾回收时间占总时间的比率，设置越小则希望垃圾回收所占时间越小，CPU能花更多的时间进行系统操作，提高吞吐量
  3. -XX:UseAdaptiveSizePolicy：参数开关，启动后系统动态自适应调节各参数，如-Xmn、-XX：SurvivorRatio等参数，这是和ParNew收集器重要的区别

1. GC日志关键字：PSYoungGen

 

4、Serial Old(年老代）

1. 年老代收集器，可以和所有的年轻代收集器组合使用(Serial收集器的年老代版本）
2. 采用 ”标记-整理“算法，会对垃圾回收导致的内存碎片进行整理
3. 使用单线程进行垃圾回收，回收时会导致Stop The World，用户进程停止
4. GC日志关键字：Tenured
5. 图示（Serial+Serial Old)

![在这里插入图片描述](https://img-blog.csdnimg.cn/55f96f796a2841898faf4fc274e5e569.png)



5、Parallel Old(年老代）

1. 年老代收集器，只能和Parallel Scavenge组合使用(Parallel Scavenge收集器的年老代版本）
2. 采用 ”标记-整理“算法，会对垃圾回收导致的内存碎片进行整理
3. 关注吞吐量的系统可以将Parallel Scavenge+Parallel Old组合使用
4. GC日志关键字：ParOldGen
5. 图示(Parallel Scavenge+Parallel Old)

![image-20220617193415470](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220617193415470.png)



6、CMS(Concurrent Mark Sweep年老代）

1. 年老代收集器，可以和Serial、ParNew组合使用
2. 采用 ”标记-清除“算法，可以通过设置参数在垃圾回收时进行内存碎片的整理

1、UserCMSCompactAtFullCollection：默认开启，FullGC时进行内存碎片整理，整理时用户进程需停止，即发生Stop The World

2、CMSFullGCsBeforeCompaction：设置执行多少次不压缩的Full GC后，执行一个带压缩的（默认为0，表示每次进入Full GC时都进行碎片整理）

1. CMS是并发算法，表示垃圾回收和用户进行同时进行，但是不是所有阶段都同时进行，在初始标记、重新标记阶段还是需要Stop the World。CMS垃圾回收分这四个阶段

1、初始标记（CMS Initial mark）   Stop the World  仅仅标记一下GC Roots能直接关联到的对象，速度快

2、并发标记（CMS concurrent mark） 进行GC Roots Tracing，时间长，不发生用户进程停顿

3、重新标记（CMS remark）      Stop the World  修正并发标记期间因用户程序继续运行导致标记变动的那一部分对象的标记记录，停顿时间较长，但远比并发标记时间短

4、并发清除（CMS concurrent sweep） 清除的同时用户进程会导致新的垃圾，时间长，不发生用户进程停顿

1. 适合于对响应时间要求高的系统
2. GC日志关键字：CMS-initial-mark、CMS-concurrent-mark-start、CMS-concurrent-mark、CMS-concurrent-preclean-start、CMS-concurrent-preclean、CMS-concurrent-sweep、CMS-concurrent-reset等等
3. 缺点

1、对CPU资源非常敏感

2、CMS收集器无法处理浮动垃圾，即清除时用户进程同时产生的垃圾，只能等到下次GC时回收

3、因为是使用“标记-清除”算法，所以会产生大量碎片

1. 图示

![在这里插入图片描述](https://img-blog.csdnimg.cn/70e0a81490654da59cc99599f0b2589c.png)

7、G1

1. G1收集器由于没有使用过，所以从网上找了一些教程供大家了解

- 1. 并行与并发
  2. 分代收集
  3. 空间整合
  4. 可预测的停顿、

![在这里插入图片描述](https://img-blog.csdnimg.cn/12946af2b5f849a8be048aa9b30e4b19.png)

**3.2.4.GC触发的时机** **Minor GC Major GC**

GC的触发包括两种情况：

1.程序调用System.gc()的时候。2.系统自身决定是否需要GC。

系统进行GC的依据：1.eden区满会触发 Minor GC。

2.FULL GC的触发条件：

（1）调用System.gc时，系统建议执行Full GC，但是不必然执行。

（2）老年代内存不足的时候。

（3）方法区内存不足的时候。

年轻代 包括 Eden和Survivor区域

- Minor GC 是 清理 Eden区 ；
- Major GC 是 清理 老年代 ；
- Full GC 是 清理整个堆空间，包括 年轻代和老年代。

[**Minor GC、Major GC和Full GC之间的区别**](https://blog.csdn.net/xiaojin21cen/article/details/87779487)

**3.6.类加载机制**

http://www.importnew.com/25295.html

**3.6.2.****双亲委派模型**

3.6.2.1. 什么是双亲委派模型？

首先，先要知道什么是类加载器。简单说，类加载器就是根据指定全限定名称将class文件加载到JVM内存，转为Class对象。如果站在JVM的角度来看，只存在两种类加载器:

- 启动类加载器（Bootstrap ClassLoader）：由C++语言实现（针对HotSpot）,负责将存放在\lib目录或-Xbootclasspath参数指定的路径中的类库加载到内存中。
- 其他类加载器：由Java语言实现，继承自抽象类ClassLoader。如：

- - 扩展类加载器（Extension ClassLoader）：负责加载\lib\ext目录或java.ext.dirs系统变量指定的路径中的所有类库。
  - 应用程序类加载器（Application ClassLoader）。负责加载用户类路径（classpath）上的指定类库，我们可以直接使用这个类加载器。一般情况，如果我们没有自定义类加载器默认就是用这个加载器。

双亲委派模型工作过程是：如果一个类加载器收到类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器完成。每个类加载器都是如此，只有当父加载器在自己的搜索范围内找不到指定的类时（即ClassNotFoundException），子加载器才会尝试自己去加载。



![在这里插入图片描述](https://img-blog.csdnimg.cn/e7589497eaa54b1f8eaa8011c27b83d2.png)

3.6.2.2.为什么需要双亲委派模型？

为什么需要双亲委派模型呢？假设没有双亲委派模型，试想一个场景：

黑客自定义一个java.lang.String类，该String类具有系统的String类一样的功能，只是在某个函数稍作修改。比如equals函数，这个函数经常使用，如果在这这个函数中，黑客加入一些“病毒代码”。并且通过自定义类加载器加入到JVM中。此时，如果没有双亲委派模型，那么JVM就可能误以为黑客自定义的java.lang.String类是系统的String类，导致“病毒代码”被执行。

而有了双亲委派模型，黑客自定义的java.lang.String类永远都不会被加载进内存。因为首先是最顶端的类加载器加载系统的java.lang.String类，最终自定义的类加载器无法加载java.lang.String类。

或许你会想，我在自定义的类加载器里面强制加载自定义的java.lang.String类，不去通过调用父加载器不就好了吗?确实，这样是可行。但是，在JVM中，判断一个对象是否是某个类型时，如果该对象的实际类型与待比较的类型的类加载器不同，那么会返回false。

举个简单例子：

ClassLoader1、ClassLoader2都加载java.lang.String类，对应Class1、Class2对象。那么Class1对象不属于ClassLoad2对象加载的java.lang.String类型。

3.6.2.3. 如何实现双亲委派模型？

双亲委派模型的原理很简单，实现也简单。每次通过先委托父类加载器加载，当父类加载器无法加载时，再自己加载。其实ClassLoader类默认的loadClass方法已经帮我们写好了，我们无需去写。

自定义类加载器

![image-20220617193625345](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220617193625345.png)



**3.8.虚拟机调优**

**3.8.1.**[**JVM（JAVA虚拟机）调优及原理**](https://blog.csdn.net/andong154564667/article/details/52442643)

[**查看jvm内存使用命令**](https://blog.csdn.net/u012516166/article/details/76762662)

1、top 查看cpu的占用情况

2、Jps显示当前所有java进程pid的命令

3、Jsata统计命令

4、dstat统计带宽、Io的

5、jstack用于生成java虚拟机当前时刻的线程快照 

6、Jmap// 查询某个pid进程对应的应用程序内存占用情况

**3.8.2.参数含义**

Xms 是指设定程序启动时占用内存大小。一般来讲，大点，程序会启动的快一点，但是也可能会导致机器暂时间变慢。

Xmx 是指设定程序运行期间最大可占用的内存大小。如果程序运行需要占用更多的内存，超出了这个设置值，就会抛出OutOfMemory异常。

Xss 是指设定每个线程的堆栈大小。这个就要依据你的程序，看一个线程大约需要占用多少内存，可能会有多少线程同时运行等。

以上三个参数的设置都是默认以Byte为单位的，也可以在数字后面添加[k/K]或者[m/M]来表示KB或者MB。而且，超过机器本身的内存大小也是不可以的，否则就等着机器变慢而不是程序变慢了。

-Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M

-Xmx 为jvm运行过程中分配的最大内存，比如-Xmx500m，表示jvm进程最多只能够占用500M内存

-Xss 为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M

**3.8.9.案例**

[**解Bug之路——记一次JVM堆外内存泄露Bug的查找**](https://blog.csdn.net/sd09044901guic/article/details/80433233)

[**Jvm线上调优实战（1）**](https://blog.csdn.net/u014730165/article/details/81984523)

[**JVM性能调优监控工具jps、jstack、jmap、jhat、jstat、hprof使用详解**](https://my.oschina.net/feichexia/blog/196575)

[**关于JVM介绍以及CPU占用过高的问题定位及解决实战经验**](https://blog.csdn.net/terminator_J/article/details/70147392)

1.案例一

场景：一个考核系统，员工可以浏览自己的所有考核项，有图表展示。

环境：内存64G。

问题：经常有用户反映长时间出现卡顿的现象。

处理思路：通过内存监控工具发现经常会发生FullGC，由于系统中经常创建大对象，所以直接会在老年代分配内存，导致老年代内存不够，发生FullGC，堆内存又比较大垃圾收集的时间就非常长，导致有卡顿现象。

解决方案：部署多个web容器，每个web容器的堆内存设置为4G，减小每个web容器的堆内存。

总结经验：如果经常要创建大对象，建议堆内存设置不要太大。

2.案例二

场景：简单数据抓取系统，抓取网站上的一些数据，分发到其他应用。

环境：2G内存。

问题：不定期出现内存溢出，把内存加大不管用，导出堆转储快照信息，没有任何信息，内存监控，正常。

处理思路：应用使用到了nio，会在堆外内存分配空间，又因为堆内存分配较大，堆外内存较小，所以会把堆外内存撑爆。

3.案例三

场景：物联网应用，家庭中有大量终端设配的数据需要服务器处理。

问题：JVM崩溃。

处理思路：JVM崩溃是由于任务大量堆积来不及处理，所以可以加一个任务队列，以生产者消费者模式来处理任务。

[**《深入理解java虚拟机》---调优案例分析（5）**](https://blog.csdn.net/hy_coming/article/details/82154213)

**3.9.杂项**

**3.9.1.Metaspace**

**3.9.2.CAS**



| 参数                               | 描述                                                         |
| ---------------------------------- | ------------------------------------------------------------ |
| -XX:+/-UseSerialGC                 | 虚拟机运行在Client模式下的默认值，打开此开关后，使用Serial + Serial Old的收集器组合进行内存回收 |
| -XX:+/-UseParNewGC                 | 打开此开关后，使用ParNew + Serial Old的收集器组合进行内存回收，在JDK9后不再支持 |
| -XX:+/-UseConcMarkSweepGC          | 打开此开关后，使用ParNew + CMS + Serial Old的收集器组合进行内存回收。Serial Old收集器将作为CMS收集器出现“Concurrent Mode Failure”失败后的后备收集器使用 |
| -XX:+/-UseParallelGC               | JDK9之前虚拟机运行在Server模式下的默认值，打开此开关后，使用Parallel Scavenge + Serial Old（PS MarkSweep）的收集器组合进行内存回收 |
| -XX:+/-UseParallelOldGC            | 打开此开关后，使用Parallel Scavenge + Parallel Old的收集器组合进行内存回收 |
| -Xms256m                           | 初始堆大小                                                   |
| -Xmx512m                           | 最大堆大小                                                   |
| -Xmn128m                           | 新生代大小                                                   |
| -Xss1m                             | 设置栈大小                                                   |
| -XX:+/-UseTLAB                     | 开启本地线程分配缓冲（Thread Local Allocation Buffer，TLAB） |
| -XX:FieldsAllocationStyle          | 字段存储顺序会受到虚拟机分配策略参数                         |
| -XX:HeapDumpOnOutOfMemoryError     | 虚拟机内存溢出时导出堆转储快照文件                           |
| -XX:MaxPermSize                    | 最大永久代大小（JDK6）                                       |
| -XX:PermSize                       | 初始永久代大小（JDK6）                                       |
| -XX:MaxMetaspaceSize               | 最大元空间大小（JDK8），默认是-1，即不限制，或者说只受限于本地内存大小 |
| -XX:MetaspaceSize                  | 指定元空间的初始大小，以字节为单位，达到该单位就会触发垃圾收集进行类型卸载，同时收集器会对该值进行调整：如果释放了大量空间就会降低该值；如果释放很少空间，在不超过最大的元空间大小的前提下，适当提高该值 |
| -XX:MinMetaspaceFreeRatio          | 作用是在垃圾收集之后控制最小的元空间剩余容量的百分比，可减少因为元空间不足导致的垃圾收集的频率。 |
| -XX:MaxMetaspaceFreeRatio          | 用于控制最大的元空间剩余容量的百分比                         |
| -XX:MaxDirectMemorySize            | 设置直接内存的最大容量，若不指定，则默认与堆最大值一致       |
| -XX:SurvivorRatio                  | 新生代中Eden区域与Survivor区域的容量比值，默认为8，代表Eden：Survivor=8:1 |
| -XX:PretenureSizeThreshold         | 直接晋升到老年代的对象大小，设置这个参数后，大于这个参数的对象将直接在老年代分配 |
| -XX:MaxTenuringThreshold           | 晋升到老年代的对象年龄。每个对象在坚持过一次Minor GC之后，年龄就增加1，当超过这个参数值时就进入老年代 |
| -XX:UseAdaptiveSizePolicy          | 动态调整Java堆中各个区域的大小以及进入老年代的年龄           |
| -XX:HandlePromotionFailure         | 是否允许分配担保失败，即老年代的剩余空间不足以应对新生代的整个Eden和Survivor区的所有对象都存活的极端情况 |
| -XX:ParallelGCThreads              | 设置并行GC时进行内存回收的线程数，也就是用户线程冻结期间并行执行的收集器线程数 |
| -XX:GCTimeRatio                    | GC时间占总时间的比率，默认值为99，即允许1%的GC时间。仅在使用Parallel Scavenge收集器时生效 |
| -XX:MaxGCPauseMillis               | 设置GC的最大停顿时间。仅在使用Parallel Scavenge收集器时生效  |
| -XX:CMSInitiatingOccupancyFraction | 设置CMS收集器在老年代空间被使用多少后触发垃圾收集。默认值为68%，仅在使用CMS收集器时生效 |
| -XX:UseCMSCompactAtFullCollection  | 设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片整理。仅在使用CMS收集器时生效，此参数从JDK9开始废弃 |
| -XX:CMSFullGCsBeforeCompaction     | 设置CMS收集器在进行若干次垃圾收集后再启动一次内存碎片整理。仅在使用CMS收集器时生效，此参数从JDK9开始废弃 |
| -XX:UseG1GC                        | 使用G1收集器，这个是JDK9后的Server模式默认值                 |
| -XX:G1HeapRegionSize=n             | 设置Region大小，并非最终值                                   |
| -XX:MaxGCPauseMillis               | 设置G1收集过程目标时间，默认值200ms，不是硬性条件            |
| -XX:G1NewSizePercent               | 新生代最小值，默认值是5%                                     |
| -XX:G1MaxNewSizePercent            | 新生代最大值，默认值是60%                                    |
| -XX:ConcGCThreads=n                | 并发标记、并发整理的执行线程数，对不同的收集器，根据其能够并发的阶段，有不同的含义 |
| -XX:InitiatingHeapOccupancyPercent | 设置触发标记周期的Java堆占用率阈值。默认值是45%。这里的Java堆占比值的是non_young_capacity_bytes，包括old+humongous |
| -XX:UseShenandoahGC                | 使用Shenandoah收集器。这个选项在OracleJDK中不被支持，只能在OpenJDK 12或者某些支持Shenandoah的Backport发行版本使用。目前仍然要配合-XX:+UnlockExperimentalVMOptions使用 |
| -XX:ShenandoahGCHeuristics         | Shenandoah何时启动一次GC过程，其可选值有adaptive、static、compact、passive、aggressive |
| -XX:UseZGC                         | 使用ZGC收集器，目前仍然要配合-XX:+UnlockExperimentalVMOptions使用 |
| -XX:UseNUMA                        | 启用NUMA内存分配支持，目前只有Parallel和ZGC支持，以后G1收集器可能也会支持该选项 |

