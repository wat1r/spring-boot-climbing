



# 1.架构





Flink分别提供了面向流式处理的接口（DataStream API）和面向批处理的接口（DataSet API）。因此，Flink既可以完成流处理，也可以完成批处理。Flink支持的拓展库涉及机器学习（FlinkML）、复杂事件处理（CEP）、以及图计算（Gelly），还有分别针对流处理和批处理的Table API

![image-20220301203939697](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220301203939697.png)





## 四大组件

- JobManager：是执行过程中的 master 进程，负责协调和管理程序的分布式执行，主要的内容包括调度任务（task），管理检查点（checkpoints）和协调故障恢复（failure recovery）等等。至少要有一个JobManager。可以设置多个JobManager以配置高可用性，其中一个总是leader，其他的都是standby

  > 始终至少有一个 JobManager。高可用（HA）设置中可能有多个 JobManager，其中一个始终是 *leader*，其他的则是 standby

- Dispatcher 负责接收用户提供的作业，并且负责为这个新提交的作业拉起一个新的 JobManager 组件

- ResourceManager 负责资源的管理，在整个 Flink 集群中只有一个 ResourceManager

- TaskManager：作为 worker 节点在 JVM 上运行，可以同时执行若干个线程以完成分配给它的 数据流的task(子任务)，并缓冲和交换数据流。必须始终至少有一个TaskManager

 



## 概念

### slots

每个 worker（TaskManager）都是一个 *JVM 进程*，可以在单独的线程中执行一个或多个 subtask。为了控制一个 TaskManager 中接受多少个 task，就有了所谓的 **task slots**（至少一个）。

每个 *task slot* 代表 TaskManager 中资源的固定子集。例如，具有 3 个 slot 的 TaskManager，会将其托管内存 1/3 用于每个 slot。分配资源意味着 subtask 不会与其他作业的 subtask 竞争托管内存，而是具有一定数量的保留托管内存。注意此处没有 CPU 隔离；当前 slot 仅分离 task 的托管内存。

通过调整 task slot 的数量，用户可以定义 subtask 如何互相隔离。每个 TaskManager 有一个 slot，这意味着每个 task 组都在单独的 JVM 中运行（例如，可以在单独的容器中启动）。具有多个 slot 意味着更多 subtask 共享同一 JVM。同一 JVM 中的 task 共享 TCP 连接（通过多路复用）和心跳信息。它们还可以共享数据集和数据结构，从而减少了每个 task 的开销。

![image-20220301204136663](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220301204136663.png)

默认情况下，Flink 允许 subtask 共享 slot，即便它们是不同的 task 的 subtask，只要是来自于同一作业即可。结果就是一个 slot 可以持有整个作业管道。允许 *slot 共享*有两个主要优点：

- Flink 集群所需的 task slot 和作业中使用的最大并行度恰好一样。无需计算程序总共包含多少个 task（具有不同并行度）。
- 容易获得更好的资源利用。如果没有 slot 共享，非密集 subtask（source/map()）将阻塞和密集型 subtask（window） 一样多的资源。通过 slot 共享，我们示例中的基本并行度从 2 增加到 6，可以充分利用分配的资源，同时确保繁重的 subtask 在 TaskManager 之间公平分配。

![image-20220301204204217](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220301204204217.png)

### 有界流与无界流

![image-20220301204404867](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220301204404867.png)

> 有界流，Flink则由一些专为固定大小[数据](https://so.csdn.net/so/search?q=数据&spm=1001.2101.3001.7020)集特殊设计的算法和数据结构进行内部处理，产生了出色的性能。

有界流
有定义流的开始，也有定义流的结束。
有界流可以在摄取所有数据后再进行计算。
有界流所有数据可以被排序，所以并不需要有序摄取。
有界流处理通常被称为批处理

> Flink 擅长精确的时间控制和状态化，使得 Flink 的运行时(runtime)能够运行任何处理无界流的应用。

无界流
有定义流的开始，但没有定义流的结束。
它们会无休止地产生数据。
无界流的数据必须持续处理，即数据被摄取后需要立刻处理。我们不能等到所有数据都到达再处理，因为输入是无限的，在任何时候输入都不会完成。
处理无界数据通常要求以特定顺序摄取事件，例如事件发生的顺序，以便能够推断结果的完整性。



# 2.调度

Flink 通过 Task Slots 来定义执行资源。每个 TaskManager 有一到多个 task slot，每个 task slot 可以运行一条由多个并行 task 组成的流水线。 这样一条流水线由多个连续的 task 组成，比如并行度为 *n* 的 MapFunction 和 并行度为 *n* 的 ReduceFunction。需要注意的是 Flink 经常并发执行连续的 task，不仅在流式作业中到处都是，在批量作业中也很常见。

下图很好的阐释了这一点，一个由数据源、*MapFunction* 和 *ReduceFunction* 组成的 Flink 作业，其中数据源和 MapFunction 的并行度为 4 ，ReduceFunction 的并行度为 3 。流水线由一系列的 Source - Map - Reduce 组成，运行在 2 个 TaskManager 组成的集群上，每个 TaskManager 包含 3 个 slot，整个作业的运行如下图所示。

Flink 内部通过 [SlotSharingGroup ](https://github.com/apache/flink/blob/release-1.14//flink-runtime/src/main/java/org/apache/flink/runtime/jobmanager/scheduler/SlotSharingGroup.java)和 [CoLocationGroup ](https://github.com/apache/flink/blob/release-1.14//flink-runtime/src/main/java/org/apache/flink/runtime/jobmanager/scheduler/CoLocationGroup.java)来定义哪些 task 可以共享一个 slot， 哪些 task 必须严格放到同一个 slot。

![image-20220301204539433](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20220301204539433.png)











# Reference

- [Flink 原理架构总结](https://blog.csdn.net/u013560925/article/details/91381822)

- [Flink架构和执行原理](https://zhuanlan.zhihu.com/p/137055447)