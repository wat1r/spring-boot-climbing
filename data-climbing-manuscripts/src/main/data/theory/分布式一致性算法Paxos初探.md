## 分布式一致性算法Paxos初探

### 背景

**Paxos算法**是Leslie Lamport于1990年提出的一种基于消息传递且具有高度容错特性的共识（consensus）算法。

需要注意的是，Paxos常被误称为「一致性算法」。但是「一致性（consistency）」”和“「共识（consensus）」”并不是同一个概念。Paxos是一个「共识（consensus）算法」。

Paxos由Lamport于1998年在《The Part-Time Parliament》论文中首次公开，最初的描述使用希腊的一个小岛Paxos作为比喻，描述了Paxos小岛中通过决议的流程，并以此命名这个算法，但是这个描述理解起来比较有挑战性。后来在2001年，Lamport觉得同行不能理解他的幽默感，于是重新发表了朴实的算法描述版本《Paxos Made Simple》。

#### 一致性模型分类

- 弱一致性
  - 最终一致性：DNS(Domain Name System)、Gossip(Cassandra通讯协议)	
- 强一致性：
  - Master-Slave
  - Paxos
  - ZAB(Multi Paxos)
  - Raft(Multi Paxos)

#### Master-Slave模型

##### 步骤(如下图)

- Master接受写请求
- Master复制日志到Slave
- Master等待所有的Slave返回结果

##### 缺陷

- 一个Slave节点失败，Master就会被block，导致整个集群不可用,保证了一致性(**Consistency**)，降低了可用性(**Availability**)

##### 如何保证强一致性

Master 写操作，写完成功后，再写 Slave，两者成功后返回成功。若 Slave失败，两种方法：

1. 标记 Slave 不可用报错，并继续服务（等恢复后，再同步Master的数据，多个Slave少了一个而已）
2. 回滚自己并返回失败

![image-20210805082153856](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210805082153856.png)

### Basic Paxos

#### 概念

- **Proposer** ：提出（propose）提案，提案信息包括提案编号和提议的 value
- **Acceptor**：acceptor 收到提案后可以接受（accept）提案，若提案获得多数派（majority）的 acceptors 的接受，则称该提案被批准（chosen）
- **Learner**：只能“学习”被批准的提案
- **Client**：系统外部角色，请求的发起者

##### 定义问题

1. 决议（value）只有在被 proposers 提出后才能被批准（未经批准的决议称为“提案（proposal）”）；
2. 在一次 Paxos 算法的执行实例中，只批准（chosen）一个 value；
3. learners 只能获得被批准（chosen）的 value。

> 在 Leslie Lamport 之后发表的paper中将 **majority** 替换为更通用的 **quorum** 概念，但在描述classic paxos的论文 **「 Paxos made simple」** 中使用的还是majority的概念。

假设不同角色之间可以通过发送消息来进行通信：

- 每个角色以任意的速度执行，可能因出错而停止，也可能会重启。一个value被选定后，所有的角色可能失败然后重启，除非那些失败后重启的角色能记录某些信息，否则等他们重启后无法确定被选定的值。
- 消息在传递过程中可能出现任意时长的延迟，可能会重复，也可能丢失。但是消息不会被损坏，即消息内容不会被篡改（**拜占庭将军问题**）。

- 在我们要求保证**多数派**的前提下，还是不够的，在并发环境下，顺序也非常重要

![image-20210805083728977](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210805083728977.png)

#### 阶段(Phase)

**Phase 1a:Prepare**

> Proposer提出（propose）一个提案（编号为N），N大于这个Proposer之前提出的提案编号，请求Acceptors的quorum接受

**Phase 1b:Promise**

> 如果N大于此Acceptor之前接受的任何提案编号，则接受该N提案，否则拒绝

**Phase 2a:Accept**

> 如果达到了多数派（majority），Proposer会发出accept请求，此请求包含提案编号N，以及提案内容

**Phase 2b:Accepted**

> 如果此Acceptor在此期间没有收到任何编号大于N的提案，则接受此提案的内容，否则忽略该提案

#### 流程

###### 基本流程

- 其中V = (Va, Vb, Vc) 中最新的一个

```java
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Prepare(1)
   |         |<---------X--X--X       |  |  Promise(1,{Va,Vb,Vc})
   |         X--------->|->|->|       |  |  Accept!(1,V)
   |         |<---------X--X--X------>|->|  Accepted(1,V)
   |<---------------------------------X--X  Response
   |         |          |  |  |       |  |
```

###### 部分节点失败，但达到了Quorums

> 在3个Acceptor中拿到了2个Acceptor的多数派

```java
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Prepare(1)
   |         |          |  |  !       |  |  !! FAIL !!
   |         |<---------X--X          |  |  Promise(1,{NULL NULL})
   |         X--------->|->|          |  |  Accept!(1,V)
   |         |<---------X--X--------->|->|  Accepted(1,V)
   |<---------------------------------X--X  Response
   |         |          |  |  |       |  |
```

###### Proposer失败

```java
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Prepare(1)
   |         |<---------X--X--X       |  |  Promise(1,{NULL,NULL,NULL})
   |         |          |  |  |       |  |
   |         |          |  |  |       |  |  !! Leader FAIL during broadcast !!
   |         X--------->|  |  |       |  |  Accept!(1,Va)
   |         !          |  |  |       |  |
   |           |        |  |  |       |  |  !! NEW LEADER !!
   |           X------->|->|->|       |  |  Prepare(2)
   |           |<-------X--X--X       |  |  Promise(2,{NULL,NULL,NULL})
   |           X------->|->|->|       |  |  Accept!(2,V)
   |           |<-------X--X--X------>|->|  Accepted(2,V)
   |<---------------------------------X--X  Response
   |         |          |  |  |       |  |
```

###### 活锁问题(liveness)

```java
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Prepare(1)
   |         |<---------X--X--X       |  |  Promise(1,{NULL,NULL,NULL})
   |         |          |  |  |       |  |
   |         |          |  |  |       |  |  !! Leader FAIL during broadcast !!
   |         !          |  |  |       |  |
   |           |        |  |  |       |  |  !! NEW LEADER !!
   |           X------->|->|->|       |  |  Prepare(2)
   |           |<-------X--X--X       |  |  Promise(2,{NULL,NULL,NULL})
   |           |        |  |  |       |  |  !! OLD LEADER RECOVERS !!
   |           |        |  |  |       |  |  !! OLD LEADER tries 2 denied!!
   |           X------->|->|->|       |  |  Prepare(2)
   |           |<-------X--X--X       |  |  NACK(2)
   |           |        |  |  |       |  |  !! OLD LEADER tries 3 !!
   |           X------->|->|->|       |  |  Prepare(3)
   |           |<-------X--X--X       |  |  Promise(3,{NULL,NULL,NULL})
   |           |        |  |  |       |  |  !! NEW LEADER propose denied !!
   |           X------->|->|->|       |  |  Accept!(2,Va)
   |           |<-------X--X--X       |  |  NACK(3)
   |           |        |  |  |       |  |  !! NEW LEADER tries 4 !!
  ...                                       ...
```

#### 推导

> **P1：一个 acceptor 必须接受（accept）第一次收到的提案。**

注意 P1 是不完备的。如果恰好一半 acceptor 接受的提案具有 value A，另一半接受的提案具有 value B，那么就无法形成多数派（majority），无法批准任何一个 value。

![image-20210809194313589](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210809194313589.png)

约束2并不要求只批准一个提案，暗示可能存在多个提案。只要提案的 value 是一样的，批准多个提案不违背约束2。于是可以产生约束 P2：

> **P2：一旦一个具有 value v 的提案被批准（chosen），那么之后批准（chosen）的提案必须具有 value v。**

注：通过某种方法可以为每个提案分配一个编号，在提案之间建立一个全序关系，所谓“之后”都是指所有编号更大的提案。
如果 P1 和 P2 都能够保证，那么约束2就能够保证。
批准一个 value 意味着多个 acceptor 接受（accept）了该 value。因此，可以对 P2 进行加强：


> **P2a：一旦一个具有 value v 的提案被批准（chosen），那么之后任何 acceptor 再次接受（accept）的提案必须具有 value v。**

![image-20210809194825548](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210809194825548.png)

如上图所示：

假设总的有5个Acceptor。Proposer2提出[M1,V1]的提案，Acceptor2-5（半数以上）均接受了该提案，于是对于Acceptor2-5和Proposer2来讲，它们都认为V1被选定。

Acceptor1刚刚从宕机状态恢复过来（之前Acceptor1没有收到过任何提案），此时Proposer1向Acceptor1发送了[M2,V2]的提案（V2≠V1且M2>M1），对于Acceptor1来讲，这是它收到的第一个提案。根据P1（**P1：一个 acceptor 必须接受（accept）第一次收到的提案。**）,Acceptor1必须接受该提案！同时Acceptor1认为V2被选定。这就出现了两个问题：

1. Acceptor1认为V2被选定，Acceptor2~5和Proposer2认为V1被选定。出现了不一致。
2. V1被选定了，但是编号更高的被Acceptor1接受的提案[M2,V2]的value为V2，且V2≠V1。这就跟P2a（**P2a：一旦一个具有 value v 的提案被批准（chosen），那么之后任何 acceptor 再次接受（accept）的提案必须具有 value v**）矛盾了。




由于通信是异步的，P2a 和 P1 会发生冲突。如果一个 value 被批准后，一个 proposer 和一个 acceptor 从休眠中苏醒，前者提出一个具有新的 value 的提案。根据 P1，后者应当接受，根据 P2a，则不应当接受，这种场景下 P2a 和 P1 有矛盾。于是需要换个思路，转而对 proposer 的行为进行约束：


> **P2b：一旦一个具有 value v 的提案被批准（chosen），那么以后任何 proposer 提出的提案必须具有 value v。**

由于 acceptor 能接受的提案都必须由 proposer 提出，所以 P2b 蕴涵了 P2a，是一个更强的约束。
但是根据 P2b 难以提出实现手段。因此需要进一步加强 P2b。
假设一个编号为 m 的 value v 已经获得批准（chosen），来看看在什么情况下对任何编号为 n（n>m）的提案都含有 value v。因为 m 已经获得批准（chosen），显然存在一个 acceptors 的多数派 C，他们都接受（accept）了v。考虑到任何多数派都和 C 具有至少一个公共成员，可以找到一个蕴涵 P2b 的约束 P2c：

> **P2c：如果一个编号为 n 的提案具有 value v，该提案被提出（issued），那么存在一个多数派，要么他们中所有人都没有接受（accept）编号小于 n 的任何提案，要么他们已经接受（accept）的所有编号小于 n 的提案中编号最大的那个提案具有 value v。**

可以用**数学归纳法**证明 **P2c** 蕴涵 **P2b**：

假设具有value v的提案m获得批准，当n=m+1时，采用反证法，假如提案n不具有value v，而是具有value w，根据**P2c**，则存在一个多数派S1，要么他们中没有人接受过编号小于n的任何提案，要么他们已经接受的所有编号小于n的提案中编号最大的那个提案是value w。由于S1和通过提案m时的多数派C之间至少有一个公共的acceptor，所以以上两个条件都不成立，导出矛盾从而推翻假设，证明了提案n必须具有value v；

若（m+1）..（N-1）所有提案都具有value v，采用反证法，假如新提案N不具有value v，而是具有value w',根据P2c，则存在一个多数派S2，要么他们没有接受过m..（N-1）中的任何提案，要么他们已经接受的所有编号小于N的提案中编号最大的那个提案是value w'。由于S2和通过m的多数派C之间至少有一个公共的acceptor，所以至少有一个acceptor曾经接受了m，从而也可以推出S2中已接受的所有编号小于n的提案中编号最大的那个提案的编号范围在m..（N-1）之间，而根据初始假设，m..（N-1）之间的所有提案都具有value v，所以S2中已接受的所有编号小于n的提案中编号最大的那个提案肯定具有value v，导出矛盾从而推翻新提案N不具有value v的假设。根据数学归纳法，我们证明了若满足**P2c**，则**P2b**一定满足。

**P2c**是可以通过消息传递模型实现的。另外，引入了**P2c**后，也解决了前文提到的**P1**不完备的问题。

#### 实现

Paxos算法分为**两个阶段**。具体如下：

- **阶段一：**

  (a) Proposer选择一个**提案编号N**，然后向**半数以上**的Acceptor发送编号为N的**Prepare请求**。

  (b) 如果一个Acceptor收到一个编号为N的Prepare请求，且N**大于**该Acceptor已经**响应过的**所有**Prepare请求**的编号，那么它就会将它已经**接受过的编号最大的提案（如果有的话）**作为响应反馈给Proposer，同时该Acceptor承诺**不再接受**任何**编号小于N的提案**。

- **阶段二：**

  (a) 如果Proposer收到**半数以上**Acceptor对其发出的编号为N的Prepare请求的**响应**，那么它就会发送一个针对**[N,V]提案**的**Accept请求**给**半数以上**的Acceptor。注意：V就是收到的**响应**中**编号最大的提案的value**，如果响应中**不包含任何提案**，那么V就由Proposer**自己决定**。

  (b) 如果Acceptor收到一个针对编号为N的提案的Accept请求，只要该Acceptor**没有**对编号**大于N**的**Prepare请求**做出过**响应**，它就**接受该提案**

#### 不足

- 难实现，2轮RPC导致效率低，活锁问题

### Multi Paxos

```java
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |  ---First Request---
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Prepare(N)
   |         |<---------X--X--X       |  |  Promise(N,I,{Va,Vb,Vc})
   |         X--------->|->|->|       |  |  Accept!(N,I,Vm)
   |         |<---------X--X--X------>|->|  Accepted(N,I,Vm)
   |<---------------------------------X--X  Response
   |         |          |  |  |       |  |
 
  
Client   Proposer      Acceptor     Learner
   |         |          |  |  |       |  |  ---Following Requests---
   X-------->|          |  |  |       |  |  Request
   |         X--------->|->|->|       |  |  Accept!(N,I+1,W)
   |         |<---------X--X--X------>|->|  Accepted(N,I+1,W)
   |<---------------------------------X--X  Response
   |         |          |  |  |       |  |
  
```

**Basic Paxos**只能对一个值形成决议，决议的形成至少需要2次RPC，在高并发情况下可能需要更多的RPC，极端情况下甚至可能形成活锁。如果想连续确定多个值，**Basic Paxos**很难完成。因此**Basic Paxos**几乎只是用来做理论研究，并不直接应用在实际工程中。

实际应用中几乎都需要连续确定多个值，而且希望能有更高的效率。Multi-Paxos正是为解决此问题而提出。Multi-Paxos基于Basic Paxos做了两点改进：

1. 针对每一个要确定的值，运行一次Paxos算法实例（Instance），形成决议。每一个Paxos实例使用唯一的Instance ID标识。
2. 在所有Proposers中选举一个Leader，由Leader唯一地提交Proposal给Acceptors进行表决。这样没有Proposer竞争，解决了活锁问题。在系统中仅有一个Leader进行Value提交的情况下，Prepare阶段就可以跳过，从而将两阶段变为一阶段，提高效率。

下图对比了**Basic Paxos和Multi Paxos**

![image-20210809195856150](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210809195856150.png)



![image-20210809200420214](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210809200420214.png)

### Reference

- [分布式一致性算法-Paxos、Raft、ZAB、Gossip](https://zhuanlan.zhihu.com/p/130332285)
- [谈谈分布式一致性算法—— paxos zab raft gossip](https://segmentfault.com/a/1190000038671078)
- [Paxos共识算法详解](https://segmentfault.com/a/1190000018844326)
- [Paxos算法详细图解](https://blog.51cto.com/u_12615191/2086264)
- https://blog.csdn.net/ystyaoshengting/article/details/105048798
- [一致性算法（Paxos、Raft、ZAB）](https://www.bilibili.com/video/BV1TW411M7Fx?from=search&seid=12869954421071795743)
- [分布式系列文章——Paxos算法原理与推导](https://www.cnblogs.com/linbingdong/p/6253479.html)
- [Wiki Pedia](https://chi.jinzhao.wiki/wiki/Paxos%E7%AE%97%E6%B3%95)
- [李永乐谈拜占庭将军问题](https://www.bilibili.com/video/BV1yJ411v7xV)

