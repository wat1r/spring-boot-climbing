



## 3.1.Standalone

```shell
# windows 的terminal监听 7777端口 
nc -lk 7777
# ide Program arguments
--host localhost --port 7777
```



因为是在windows上运行Ubuntu的Terminal，一开始用的是windows的jdk8版本，报错，遂改成linux的jdk1.8，成功

```shell
# sudo vim /etc/profile
JAVA_HOME=/mnt/c/env/java/jdk1.8.0_162
PATH=$JAVA_HOME/bin:$PATH
CLASSPATH=$JAVA_HOME/jre/lib/ext:$JAVA_HOME/lib/toos.jar
export PATH JAVA_HOME CLASSPATH
#  source /etc/profile
# $/mnt/d/Climbing/flink-1.10.1/bin$ ./start-cluster.sh
Starting cluster.
Starting standalonesession daemon on host sh-xx.
Starting taskexecutor daemon on host sh-xx.
/bin$ jps
3988 TaskManagerRunner
3622 StandaloneSessionClusterEntrypoint
4061 Jps
```

- `conf/flink.yaml`

```yaml
################################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
################################################################################


#==============================================================================
# Common
#==============================================================================

# The external address of the host on which the JobManager runs and can be
# reached by the TaskManagers and any clients which want to connect. This setting
# is only used in Standalone mode and may be overwritten on the JobManager side
# by specifying the --host <hostname> parameter of the bin/jobmanager.sh executable.
# In high availability mode, if you use the bin/start-cluster.sh script and setup
# the conf/masters file, this will be taken care of automatically. Yarn/Mesos
# automatically configure the host name based on the hostname of the node where the
# JobManager runs.

jobmanager.rpc.address: localhost

# The RPC port where the JobManager is reachable.

jobmanager.rpc.port: 6123


# The heap size for the JobManager JVM

jobmanager.heap.size: 1024m


# The total process memory size for the TaskManager.
#
# Note this accounts for all memory usage within the TaskManager process, including JVM metaspace and other overhead.

taskmanager.memory.process.size: 1728m

# To exclude JVM metaspace and overhead, please, use total Flink memory size instead of 'taskmanager.memory.process.size'.
# It is not recommended to set both 'taskmanager.memory.process.size' and Flink memory.
#
# taskmanager.memory.flink.size: 1280m

# The number of task slots that each TaskManager offers. Each slot runs one parallel pipeline.

taskmanager.numberOfTaskSlots: 1

# The parallelism used for programs that did not specify and other parallelism.

parallelism.default: 1

# The default file system scheme and authority.
# 
# By default file paths without scheme are interpreted relative to the local
# root file system 'file:///'. Use this to override the default and interpret
# relative paths relative to a different file system,
# for example 'hdfs://mynamenode:12345'
#
# fs.default-scheme

#==============================================================================
# High Availability
#==============================================================================

# The high-availability mode. Possible options are 'NONE' or 'zookeeper'.
#
# high-availability: zookeeper

# The path where metadata for master recovery is persisted. While ZooKeeper stores
# the small ground truth for checkpoint and leader election, this location stores
# the larger objects, like persisted dataflow graphs.
# 
# Must be a durable file system that is accessible from all nodes
# (like HDFS, S3, Ceph, nfs, ...) 
#
# high-availability.storageDir: hdfs:///flink/ha/

# The list of ZooKeeper quorum peers that coordinate the high-availability
# setup. This must be a list of the form:
# "host1:clientPort,host2:clientPort,..." (default clientPort: 2181)
#
# high-availability.zookeeper.quorum: localhost:2181


# ACL options are based on https://zookeeper.apache.org/doc/r3.1.2/zookeeperProgrammers.html#sc_BuiltinACLSchemes
# It can be either "creator" (ZOO_CREATE_ALL_ACL) or "open" (ZOO_OPEN_ACL_UNSAFE)
# The default value is "open" and it can be changed to "creator" if ZK security is enabled
#
# high-availability.zookeeper.client.acl: open

#==============================================================================
# Fault tolerance and checkpointing
#==============================================================================

# The backend that will be used to store operator state checkpoints if
# checkpointing is enabled.
#
# Supported backends are 'jobmanager', 'filesystem', 'rocksdb', or the
# <class-name-of-factory>.
#
# state.backend: filesystem

# Directory for checkpoints filesystem, when using any of the default bundled
# state backends.
#
# state.checkpoints.dir: hdfs://namenode-host:port/flink-checkpoints

# Default target directory for savepoints, optional.
#
# state.savepoints.dir: hdfs://namenode-host:port/flink-checkpoints

# Flag to enable/disable incremental checkpoints for backends that
# support incremental checkpoints (like the RocksDB state backend). 
#
# state.backend.incremental: false

# The failover strategy, i.e., how the job computation recovers from task failures.
# Only restart tasks that may have been affected by the task failure, which typically includes
# downstream tasks and potentially upstream tasks if their produced data is no longer available for consumption.

jobmanager.execution.failover-strategy: region

#==============================================================================
# Rest & web frontend
#==============================================================================

# The port to which the REST client connects to. If rest.bind-port has
# not been specified, then the server will bind to this port as well.
#
#rest.port: 8081

# The address to which the REST client will connect to
#
#rest.address: 0.0.0.0

# Port range for the REST and web server to bind to.
#
#rest.bind-port: 8080-8090

# The address that the REST & web server binds to
#
#rest.bind-address: 0.0.0.0

# Flag to specify whether job submission is enabled from the web-based
# runtime monitor. Uncomment to disable.

#web.submit.enable: false

#==============================================================================
# Advanced
#==============================================================================

# Override the directories for temporary files. If not specified, the
# system-specific Java temporary directory (java.io.tmpdir property) is taken.
#
# For framework setups on Yarn or Mesos, Flink will automatically pick up the
# containers' temp directories without any need for configuration.
#
# Add a delimited list for multiple directories, using the system directory
# delimiter (colon ':' on unix) or a comma, e.g.:
#     /data1/tmp:/data2/tmp:/data3/tmp
#
# Note: Each directory entry is read from and written to by a different I/O
# thread. You can include the same directory multiple times in order to create
# multiple I/O threads against that directory. This is for example relevant for
# high-throughput RAIDs.
#
# io.tmp.dirs: /tmp

# The classloading resolve order. Possible values are 'child-first' (Flink's default)
# and 'parent-first' (Java's default).
#
# Child first classloading allows users to use different dependency/library
# versions in their application than those in the classpath. Switching back
# to 'parent-first' may help with debugging dependency issues.
#
# classloader.resolve-order: child-first

# The amount of memory going to the network stack. These numbers usually need 
# no tuning. Adjusting them may be necessary in case of an "Insufficient number
# of network buffers" error. The default min is 64MB, the default max is 1GB.
# 
# taskmanager.memory.network.fraction: 0.1
# taskmanager.memory.network.min: 64mb
# taskmanager.memory.network.max: 1gb

#==============================================================================
# Flink Cluster Security Configuration
#==============================================================================

# Kerberos authentication for various components - Hadoop, ZooKeeper, and connectors -
# may be enabled in four steps:
# 1. configure the local krb5.conf file
# 2. provide Kerberos credentials (either a keytab or a ticket cache w/ kinit)
# 3. make the credentials available to various JAAS login contexts
# 4. configure the connector to use JAAS/SASL

# The below configure how Kerberos credentials are provided. A keytab will be used instead of
# a ticket cache if the keytab path and principal are set.

# security.kerberos.login.use-ticket-cache: true
# security.kerberos.login.keytab: /path/to/kerberos/keytab
# security.kerberos.login.principal: flink-user

# The configuration below defines which JAAS login contexts

# security.kerberos.login.contexts: Client,KafkaClient

#==============================================================================
# ZK Security Configuration
#==============================================================================

# Below configurations are applicable if ZK ensemble is configured for security

# Override below configuration to provide custom ZK service name if configured
# zookeeper.sasl.service-name: zookeeper

# The configuration below must match one of the values set in "security.kerberos.login.contexts"
# zookeeper.sasl.login-context-name: Client

#==============================================================================
# HistoryServer
#==============================================================================

# The HistoryServer is started and stopped via bin/historyserver.sh (start|stop)

# Directory to upload completed jobs to. Add this directory to the list of
# monitored directories of the HistoryServer as well (see below).
#jobmanager.archive.fs.dir: hdfs:///completed-jobs/

# The address under which the web-based HistoryServer listens.
#historyserver.web.address: 0.0.0.0

# The port under which the web-based HistoryServer listens.
#historyserver.web.port: 8082

# Comma separated list of directories to monitor for completed jobs.
#historyserver.archive.fs.dir: hdfs:///completed-jobs/

# Interval in milliseconds for refreshing the monitored directories.
#historyserver.archive.fs.refresh-interval: 10000
```







- 
- 浏览器 `localhost:8081`

![image-20220220164654822](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220164654822.png)





![image-20220220171721912](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220171721912.png)

![image-20220220171700622](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220171700622.png)

- 命令行提交

```shell
./bin/flink run -c com.atguigu.wc.StreamWordCount -p 3 /mnt/d/Dev/SrcCode/FlinkTutorial/target/FlinkTutorial-1.0-SNAPSHOT.jar  --host localhost --port 7777
# Job has been submitted with JobID 1c92f0dec9a9153efa2016888d46b74b
$ ./bin/flink list
Waiting for response...
------------------ Running/Restarting Jobs -------------------
20.02.2022 17:44:24 : c83540cf858b07e8481814093a7aa811 : Flink Streaming Job (RUNNING)
20.02.2022 17:51:31 : 1c92f0dec9a9153efa2016888d46b74b : Flink Streaming Job (RUNNING)
--------------------------------------------------------------
No scheduled jobs.
$ ./bin/flink cancel c83540cf858b07e8481814093a7aa811
# 查看当前被取消的
$ ./bin/flink list -a

```



## 3.2.Yarn模式

- Session-Cluster

   Session-Cluster 模式需要先启动集群，然后再提交作业，接着会向 yarn 申请一块空间后，**资源永远保持不变**。如果资源满了，下一个作业就无法提交，只能等到 yarn 中的其中一个作业执行完成后，释放了资源，下个作业才会正常提交。**所有作业共享 Dispatcher 和 ResourceManager**；**共享资源；适合规模小执行时间短的作业**

   **在 yarn 中初始化一个 flink 集群，开辟指定的资源，以后提交任务都向这里提交。这个 flink 集群会常驻在 yarn 集群中，除非手工停止。**

![image-20220220180212240](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220180212240.png)

- Per-Job-Cluster

 一个 Job 会对应一个集群，每提交一个作业会根据自身的情况，都会单独向 yarn 申请资源，直到作业执行完成，一个作业的失败与否并不会影响下一个作业的正常提交和运行。**独享 Dispatcher 和 ResourceManager**，按需接受资源申请；适合规模大长时间运行的作业。

 **每次提交都会创建一个新的 flink 集群，任务之间互相独立，互不影响，方便管理。任务执行完成之后创建的集群也会消失。**

![image-20220220180514710](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220180514710.png)

### 3.2.2 Session Cluster

1. 启动*hadoop*集群（略）

2. 启动*yarn-session*

   ```shell
   ./yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm test -d
   ```

   其中：

   - `-n(--container)`：TaskManager的数量。
   - `-s(--slots)`：每个TaskManager的slot数量，默认一个slot一个core，默认每个taskmanager的slot的个数为1，有时可以多一些taskmanager，做冗余。
   - `-jm`：JobManager的内存（单位MB)。
   - `-tm`：每个taskmanager的内存（单位MB)。
   - `-nm`：yarn 的appName(现在yarn的ui上的名字)。
   - `-d`：后台执行。

3. 执行任务

   ```shell
   ./flink run -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar --host lcoalhost –port 7777
   ```

4. 去 yarn 控制台查看任务状态

   ![image-20220220180609008](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220180609008.png)

5. 取消 yarn-session

   ```shell
   yarn application --kill application_1577588252906_0001
   ```

### 3.2.3 Per Job Cluster)

1. 启动*hadoop*集群（略）

2. 不启动**yarn-session**，直接执行*job*

   ```shell
   ./flink run –m yarn-cluster -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar --host lcoalhost –port 7777
   ```

## 3.3 Kubernetes部署

 容器化部署时目前业界很流行的一项技术，基于Docker镜像运行能够让用户更加方便地对应用进行管理和运维。容器管理工具中最为流行的就是Kubernetes（k8s），而Flink也在最近的版本中支持了k8s部署模式。

1.搭建*Kubernetes*集群（略）

2.配置各组件的*yaml*文件

 在k8s上构建Flink Session Cluster，需要将Flink集群的组件对应的docker镜像分别在k8s上启动，包括JobManager、TaskManager、JobManagerService三个镜像服务。每个镜像服务都可以从中央镜像仓库中获取。

 3.启动*Flink Session Cluster*

```shell
// 启动jobmanager-service 服务 
kubectl create -f jobmanager-service.yaml
// 启动jobmanager-deployment服务 
kubectl create -f jobmanager-deployment.yaml
// 启动taskmanager-deployment服务
kubectl create -f taskmanager-deployment.yaml
```

4.访问*Flink UI*页面

集群启动后，就可以通过JobManagerServicers中配置的WebUI端口，用浏览器输入以下url来访问Flink UI页面了：

`http://{JobManagerHost:Port}/api/v1/namespaces/default/services/flink-jobmanager:ui/proxy`

# 4. Flink运行架构

> [Flink-运行时架构中的四大组件|任务提交流程|任务调度原理|Slots和并行度中间的关系|数据流|执行图|数据得传输形式|任务链](https://blog.csdn.net/qq_40180229/article/details/106321149)

## 4.1 Flink运行时的组件

 Flink运行时架构主要包括四个不同的组件，它们会在运行流处理应用程序时协同工作：

- **作业管理器（JobManager）**
- **资源管理器（ResourceManager）**
- **任务管理器（TaskManager）**
- **分发器（Dispatcher）**

 因为Flink是用Java和Scala实现的，所以所有组件都会运行在Java虚拟机上。每个组件的职责如下：

### 作业管理器（JobManager）

 JobManager会先接收到要执行的应用程序，这个应用程序会包括：

- 作业图（JobGraph）
- 逻辑数据流图（logical dataflow graph）
- 打包了所有的类、库和其它资源的JAR包。

 JobManager会把JobGraph转换成一个物理层面的数据流图，这个图被叫做“执行图”（ExecutionGraph），包含了所有可以并发执行的任务。

 **JobManager会向资源管理器（ResourceManager）请求执行任务必要的资源，也就是任务管理器（TaskManager）上的插槽（slot）。一旦它获取到了足够的资源，就会将执行图分发到真正运行它们的TaskManager上**。

 在运行过程中，JobManager会负责所有需要中央协调的操作，比如说检查点（checkpoints）的协调。

### 资源管理器（ResourceManager）

 主要负责管理任务管理器（TaskManager）的插槽（slot），TaskManger插槽是Flink中定义的处理资源单元。

 Flink为不同的环境和资源管理工具提供了不同资源管理器，比如YARN、Mesos、K8s，以及standalone部署。

 **当JobManager申请插槽资源时，ResourceManager会将有空闲插槽的TaskManager分配给JobManager**。如果ResourceManager没有足够的插槽来满足JobManager的请求，它还可以向资源提供平台发起会话，以提供启动TaskManager进程的容器。

 另外，**ResourceManager还负责终止空闲的TaskManager，释放计算资源**。

### 任务管理器（TaskManager）

 Flink中的工作进程。通常在Flink中会有多个TaskManager运行，每一个TaskManager都包含了一定数量的插槽（slots）。**插槽的数量限制了TaskManager能够执行的任务数量**。

 启动之后，TaskManager会向资源管理器注册它的插槽；收到资源管理器的指令后，TaskManager就会将一个或者多个插槽提供给JobManager调用。JobManager就可以向插槽分配任务（tasks）来执行了。

 **在执行过程中，一个TaskManager可以跟其它运行同一应用程序的TaskManager交换数据**。

### 分发器（Dispatcher）

 可以跨作业运行，它为应用提交提供了REST接口。

 当一个应用被提交执行时，分发器就会启动并将应用移交给一个JobManager。由于是REST接口，所以Dispatcher可以作为集群的一个HTTP接入点，这样就能够不受防火墙阻挡。Dispatcher也会启动一个Web UI，用来方便地展示和监控作业执行的信息。

 *Dispatcher在架构中可能并不是必需的，这取决于应用提交运行的方式。*



## 4.2 任务提交流程

 我们来看看当一个应用提交执行时，Flink的各个组件是如何交互协作的：

![image-20220220183644136](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220183644136.png)

 *ps：上图中7.指TaskManager为JobManager提供slots，8.表示JobManager提交要在slots中执行的任务给TaskManager。*

 上图是从一个较为高层级的视角来看应用中各组件的交互协作。

 如果部署的集群环境不同（例如YARN，Mesos，Kubernetes，standalone等），其中一些步骤可以被省略，或是有些组件会运行在同一个JVM进程中。

 具体地，如果我们将Flink集群部署到YARN上，那么就会有如下的提交流程：

![image-20220220183725553](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220183725553.png)

1. Flink任务提交后，Client向HDFS上传Flink的Jar包和配置
2. 之后客户端向Yarn ResourceManager提交任务，ResourceManager分配Container资源并通知对应的NodeManager启动ApplicationMaster
3. ApplicationMaster启动后加载Flink的Jar包和配置构建环境，去启动JobManager，之后**JobManager向Flink自身的RM进行申请资源，自身的RM向Yarn 的ResourceManager申请资源(因为是yarn模式，所有资源归yarn RM管理)启动TaskManager**
4. Yarn ResourceManager分配Container资源后，由ApplicationMaster通知资源所在节点的NodeManager启动TaskManager
5. NodeManager加载Flink的Jar包和配置构建环境并启动TaskManager，TaskManager启动后向JobManager发送心跳包，并等待JobManager向其分配任务。

## 4.3 任务调度原理

![image-20220220184525369](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220184525369.png)



1. 客户端不是运行时和程序执行的一部分，但它用于准备并发送dataflow(JobGraph)给Master(JobManager)，然后，客户端断开连接或者维持连接以等待接收计算结果。而Job Manager会产生一个执行图(Dataflow Graph)

2. 当 Flink 集群启动后，首先会启动一个 JobManger 和一个或多个的 TaskManager。由 Client 提交任务给 JobManager，JobManager 再调度任务到各个 TaskManager 去执行，然后 TaskManager 将心跳和统计信息汇报给 JobManager。TaskManager 之间以流的形式进行数据的传输。上述三者均为独立的 JVM 进程。

3. Client 为提交 Job 的客户端，可以是运行在任何机器上（与 JobManager 环境连通即可）。提交 Job 后，Client 可以结束进程（Streaming的任务），也可以不结束并等待结果返回。

4. JobManager 主要负责调度 Job 并协调 Task 做 checkpoint，职责上很像 Storm 的 Nimbus。从 Client 处接收到 Job 和 JAR 包等资源后，会生成优化后的执行计划，并以 Task 的单元调度到各个 TaskManager 去执行。

5. TaskManager 在启动的时候就设置好了槽位数（Slot），每个 slot 能启动一个 Task，Task 为线程。从 JobManager 处接收需要部署的 Task，部署启动后，与自己的上游建立 Netty 连接，接收数据并处理。

   *注：如果一个Slot中启动多个线程，那么这几个线程类似CPU调度一样共用同一个slot*

### 4.3.1 TaskManger与Slots

要点：

- 考虑到Slot分组，所以实际运行Job时所需的Slot总数 = 每个Slot组中的最大并行度。

  eg（1，1，2，1）,其中第一个归为组“red”、第二个归组“blue”、第三个和第四归组“green”，那么运行所需的slot即max（1）+max（1）+max（2，1） = 1+1+2 = 4

![image-20220220184613460](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220184613460.png)



- Flink中每一个worker(TaskManager)都是一个**JVM**进程，它可能会在独立的线程上执行一个或多个subtask。
- 为了控制一个worker能接收多少个task，worker通过task slot来进行控制（一个worker至少有一个task slot）。

**上图这个每个子任务各自占用一个slot，可以在代码中通过算子的`.slotSharingGroup("组名")`指定算子所在的Slot组名，默认每一个算子的SlotGroup和上一个算子相同，而默认的SlotGroup就是"default"**。

**同一个SlotGroup的算子能共享同一个slot，不同组则必须另外分配独立的Slot。**

![image-20220220184639503](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220184639503.png)



- 默认情况下，Flink允许子任务共享slot，即使它们是不同任务的子任务（前提需要来自同一个Job）。这样结果是，**一个slot可以保存作业的整个管道pipeline**。

  - **不同任务共享同一个Slot的前提：这几个任务前后顺序不同，如上图中Source和keyBy是两个不同步骤顺序的任务，所以可以在同一个Slot执行**。
  - 一个slot可以保存作业的整个管道的好处：
    - 如果有某个slot执行完了整个任务流程，那么其他任务就可以不用继续了，这样也省去了跨slot、跨TaskManager的通信损耗（降低了并行度）
    - 同时slot能够保存整个管道，使得整个任务执行健壮性更高，因为某些slot执行出异常也能有其他slot补上。
    - 有些slot分配到的子任务非CPU密集型，有些则CPU密集型，如果每个slot只完成自己的子任务，将出现某些slot太闲，某些slot过忙的现象。
  - *假设拆分的多个Source子任务放到同一个Slot，那么任务不能并行执行了=>因为多个相同步骤的子任务需要抢占的具体资源相同，比如抢占某个锁，这样就不能并行。*

- Task Slot是静态的概念，是指TaskManager具有的并发执行能力，可以通过参数`taskmanager.numberOfTaskSlots`进行配置。

  *而并行度**parallelism**是动态概念，即**TaskManager**运行程序时实际使用的并发能力，可以通过参数`parallelism.default`进行配置。*

 每个task slot表示TaskManager拥有资源的一个固定大小的子集。假如一个TaskManager有三个slot，那么它会将其管理的内存分成三份给各个slot。资源slot化意味着一个subtask将不需要跟来自其他job的subtask竞争被管理的内存，取而代之的是它将拥有一定数量的内存储备。

 **需要注意的是，这里不会涉及到CPU的隔离，slot目前仅仅用来隔离task的受管理的内存**。

 通过调整task slot的数量，允许用户定义subtask之间如何互相隔离。如果一个TaskManager一个slot，那将意味着每个task group运行在独立的JVM中（该JVM可能是通过一个特定的容器启动的），而一个TaskManager多个slot意味着更多的subtask可以共享同一个JVM。而在同一个JVM进程中的task将共享TCP连接（基于多路复用）和心跳消息。它们也可能共享数据集和数据结构，因此这减少了每个task的负载。

### 4.3.2 Slot和并行度

1. **一个特定算子的 子任务（subtask）的个数被称之为其并行度（parallelism）**，我们可以对单独的每个算子进行设置并行度，也可以直接用env设置全局的并行度，更可以在页面中去指定并行度。
2. 最后，由于并行度是实际Task Manager处理task 的能力，而一般情况下，**一个 stream 的并行度，可以认为就是其所有算子中最大的并行度**，则可以得出**在设置Slot时，在所有设置中的最大设置的并行度大小则就是所需要设置的Slot的数量。**（如果Slot分组，则需要为每组Slot并行度最大值的和）

![image-20220220185054515](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220185054515.png)

并行子任务 的分配

![image-20220220192032874](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220192032874.png)

![image-20220220185122012](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220185122012.png)![image-20220220185130097](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220185130097.png)

 假设一共有3个TaskManager，每一个TaskManager中的分配3个TaskSlot，也就是每个TaskManager可以接收3个task，一共9个TaskSlot，如果我们设置`parallelism.default=1`，即运行程序默认的并行度为1，9个TaskSlot只用了1个，有8个空闲，因此，设置合适的并行度才能提高效率。

 *ps：上图最后一个因为是输出到文件，避免多个Slot（多线程）里的算子都输出到同一个文件互相覆盖等混乱问题，直接设置sink的并行度为1。*



### 4.3.3 程序和数据流（DataFlow）

![image-20220220192955654](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220192955654.png)

- **所有的Flink程序都是由三部分组成的： Source 、Transformation 和 Sink。**
- Source 负责读取数据源，Transformation 利用各种算子进行处理加工，Sink 负责输出![image-20220220193012454](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220193012454.png)

- 在运行时，Flink上运行的程序会被映射成“逻辑数据流”（dataflows），它包含了这三部分
- 每一个dataflow以一个或多个sources开始以一个或多个sinks结束。dataflow类似于任意的有向无环图（DAG）
- 在大部分情况下，程序中的转换运算（transformations）跟dataflow中的算子（operator）是一一对应的关系

### 4.3.4 执行图（**ExecutionGraph**）

 由Flink程序直接映射成的数据流图是StreamGraph，也被称为**逻辑流图**，因为它们表示的是计算逻辑的高级视图。为了执行一个流处理程序，Flink需要将**逻辑流图**转换为**物理数据流图**（也叫**执行图**），详细说明程序的执行方式。

- Flink 中的执行图可以分成四层：StreamGraph -> JobGraph -> ExecutionGraph -> 物理执行图。
  - **StreamGraph**：是根据用户通过Stream API 编写的代码生成的最初的图。用来表示程序的拓扑结构。
  - **JobGraph**：StreamGraph经过优化后生成了JobGraph，提交给JobManager 的数据结构。主要的优化为，将多个符合条件的节点chain 在一起作为一个节点，这样可以减少数据在节点之间流动所需要的序列化/反序列化/传输消耗。
  - **ExecutionGraph**：JobManager 根据JobGraph 生成ExecutionGraph。ExecutionGraph是JobGraph的并行化版本，是调度层最核心的数据结构。
  - 物理执行图：JobManager 根据ExecutionGraph 对Job 进行调度后，在各个TaskManager 上部署Task 后形成的“图”，并不是一个具体的数据结构。

![image-20220220193045217](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220193045217.png)

### 4.3.5 数据传输形式

- 一个程序中，不同的算子可能具有不同的并行度
- 算子之间传输数据的形式可以是 one-to-one (forwarding) 的模式也可以是redistributing 的模式，具体是哪一种形式，取决于算子的种类
  - **One-to-one**：stream维护着分区以及元素的顺序（比如source和map之间）。这意味着map 算子的子任务看到的元素的个数以及顺序跟 source 算子的子任务生产的元素的个数、顺序相同。**map、fliter、flatMap等算子都是one-to-one的对应关系**。
  - **Redistributing**：stream的分区会发生改变。每一个算子的子任务依据所选择的transformation发送数据到不同的目标任务。例如，keyBy 基于 hashCode 重分区、而 broadcast 和 rebalance 会随机重新分区，这些算子都会引起redistribute过程，而 redistribute 过程就类似于 Spark 中的 shuffle 过程。

### 4.3.6 任务链（OperatorChains）

 Flink 采用了一种称为任务链的优化技术，可以在特定条件下减少本地通信的开销。为了满足任务链的要求，必须将两个或多个算子设为**相同的并行度**，并通过本地转发（local forward）的方式进行连接

- 相同并行度的one-to-one 操作，Flink 这样相连的算子链接在一起形成一个 task，原来的算子成为里面的 subtask
  - 并行度相同、并且是 one-to-one 操作，两个条件缺一不可

 **为什么需要并行度相同，因为若flatMap并行度为1，到了之后的map并行度为2，从flatMap到map的数据涉及到数据由于并行度map为2会往两个slot处理，数据会分散，所产生的元素个数和顺序发生的改变所以有2个单独的task，不能成为任务链**

![image-20220220193108781](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220193108781.png)

 **如果前后任务逻辑上可以是OneToOne，且并行度一致，那么就能合并在一个Slot里**（并行度原本是多少就是多少，两者并行度一致）执行。

- keyBy需要根据Hash值分配给不同slot执行，所以只能Hash，不能OneToOne。
- 逻辑上可OneToOne但是并行度不同，那么就会Rebalance，轮询形式分配给下一个任务的多个slot。

------

- **代码中如果`算子.disableChaining()`，能够强制当前算子的子任务不参与任务链的合并，即不和其他Slot资源合并，但是仍然可以保留“Slot共享”的特性**。
- **如果`StreamExecutionEnvironment env.disableOperatorChaining()`则当前执行环境全局设置算子不参与"任务链的合并"。**
- **如果`算子.startNewChain()`表示不管前面任务链合并与否，从当前算子往后重新计算任务链的合并。通常用于前面强制不要任务链合并，而当前往后又需要任务链合并的特殊场景。**

*ps：如果`算子.shuffle()`，能够强制算子之后重分区到不同slot执行下一个算子操作，逻辑上也实现了任务不参与任务链合并=>但是仅为“不参与任务链的合并”，这个明显不是最优解操作*

> [Flink slotSharingGroup disableChain startNewChain 用法案例](https://blog.csdn.net/qq_31866793/article/details/102786249)



# 5. Flink流处理API

## 5.1 Environment

![image-20220220200111007](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220200111007.png)

### 5.1.1 getExecutionEnvironment

 创建一个执行环境，表示当前执行程序的上下文。如果程序是独立调用的，则此方法返回本地执行环境；如果从命令行客户端调用程序以提交到集群，则此方法返回此集群的执行环境，也就是说，getExecutionEnvironment会根据查询运行的方式决定返回什么样的运行环境，是最常用的一种创建执行环境的方式。

```java
ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment(); 
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(); 
```

如果没有设置并行度，会以flink-conf.yaml中的配置为准，默认是1。

![image-20220220200126872](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220200126872.png)

### 5.1.2 createLocalEnvironment

 返回本地执行环境，需要在调用时指定默认的并行度。

```java
LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(1); 
```

### 5.1.3 createRemoteEnvironment

 返回集群执行环境，将Jar提交到远程服务器。需要在调用时指定JobManager的IP和端口号，并指定要在集群中运行的Jar包。

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("jobmanage-hostname",6123,"YOURPATH//WordCoubt.jar");
```

### 5.2.1 从集合读取数据

java代码：

```java
package apitest.source;

import apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * 测试Flink从集合中获取数据
 */
public class SourceTest1_Collection {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置env并行度1，使得整个任务抢占同一个线程执行
        env.setParallelism(1);

        // Source: 从集合Collection中获取数据
        DataStream<SensorReading> dataStream = env.fromCollection(
                Arrays.asList(
                        new SensorReading("sensor_1", 1547718199L, 35.8),
                        new SensorReading("sensor_6", 1547718201L, 15.4),
                        new SensorReading("sensor_7", 1547718202L, 6.7),
                        new SensorReading("sensor_10", 1547718205L, 38.1)
                )
        );

        DataStream<Integer> intStream = env.fromElements(1,2,3,4,5,6,7,8,9);

        // 打印输出
        dataStream.print("SENSOR");
        intStream.print("INT");

        // 执行
        env.execute("JobName");

    }

}
```

输出：

```shell
INT> 1
INT> 2
SENSOR> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
INT> 3
SENSOR> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
INT> 4
SENSOR> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
INT> 5
SENSOR> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
INT> 6
INT> 7
INT> 8
INT> 9
```

### 5.2.2 从文件读取数据

java代码如下：

```java
package apitest.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Flink从文件中获取数据
 */
public class SourceTest2_File {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 使得任务抢占同一个线程
        env.setParallelism(1);

        // 从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

        dataStream.print();

        env.execute();
    }
}
```

sensor.txt文件内容

```txt
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718209,32.8
sensor_1,1547718212,37.1
```

输出：

```shell
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718209,32.8
sensor_1,1547718212,37.1
```

### 5.2.3 从Kafka读取数据

1. pom依赖

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>org.example</groupId>
       <artifactId>Flink_Tutorial</artifactId>
       <version>1.0-SNAPSHOT</version>
   
       <properties>
           <maven.compiler.source>8</maven.compiler.source>
           <maven.compiler.target>8</maven.compiler.target>
           <flink.version>1.12.1</flink.version>
           <scala.binary.version>2.12</scala.binary.version>
       </properties>
   
       <dependencies>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-java</artifactId>
               <version>${flink.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-streaming-scala_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-clients_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
   
           <!-- kafka -->
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-connector-kafka_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
       </dependencies>
   </project>
   ```

2. 启动zookeeper

   ```shell
   $ zookeeper-3.4.10/bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

3. 启动kafka服务

   ```shell
   $ kafka-2.11-2.1.0/bin/kafka-server-start.sh config/server.properties
   ```

4. 启动kafka生产者

   ```shell
   $ kafka-2.11-2.1.0/bin/kafka-console-producer.sh --broker-list localhost:9092  --topic sensor
   ```

5. 编写java代码

   ```java
   package apitest.source;
   
   import org.apache.flink.api.common.serialization.SimpleStringSchema;
   import org.apache.flink.streaming.api.datastream.DataStream;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
   
   import java.util.Properties;
   
   
   public class SourceTest3_Kafka {
   
       public static void main(String[] args) throws Exception {
           // 创建执行环境
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
   
           // 设置并行度1
           env.setParallelism(1);
   
           Properties properties = new Properties();
           properties.setProperty("bootstrap.servers", "localhost:9092");
           // 下面这些次要参数
           properties.setProperty("group.id", "consumer-group");
           properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("auto.offset.reset", "latest");
   
           // flink添加外部数据源
           DataStream<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("sensor", new SimpleStringSchema(),properties));
   
           // 打印输出
           dataStream.print();
   
           env.execute();
       }
   }
   ```

6. 运行java代码，在Kafka生产者console中输入

   ```shell
   $ bin/kafka-console-producer.sh --broker-list localhost:9092  --topic sensor
   >sensor_1,1547718199,35.8
   >sensor_6,1547718201,15.4
   >
   ```

7. java输出

   ```shell
   sensor_1,1547718199,35.8
   sensor_6,1547718201,15.4
   ```

### 5.2.4 自定义Source

java代码：

```java
package apitest.source;

import apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.HashMap;
import java.util.Random;


public class SourceTest4_UDF {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<SensorReading> dataStream = env.addSource(new MySensorSource());

        dataStream.print();

        env.execute();
    }

    // 实现自定义的SourceFunction
    public static class MySensorSource implements SourceFunction<SensorReading> {

        // 标示位，控制数据产生
        private volatile boolean running = true;


        @Override
        public void run(SourceContext<SensorReading> ctx) throws Exception {
            //定义一个随机数发生器
            Random random = new Random();

            // 设置10个传感器的初始温度
            HashMap<String, Double> sensorTempMap = new HashMap<>();
            for (int i = 0; i < 10; ++i) {
                sensorTempMap.put("sensor_" + (i + 1), 60 + random.nextGaussian() * 20);
            }

            while (running) {
                for (String sensorId : sensorTempMap.keySet()) {
                    // 在当前温度基础上随机波动
                    Double newTemp = sensorTempMap.get(sensorId) + random.nextGaussian();
                    sensorTempMap.put(sensorId, newTemp);
                    ctx.collect(new SensorReading(sensorId,System.currentTimeMillis(),newTemp));
                }
                // 控制输出评率
                Thread.sleep(2000L);
            }
        }

        @Override
        public void cancel() {
            this.running = false;
        }
    }
}
```

输出：

```shell
7> SensorReading{id='sensor_9', timestamp=1612091759321, temperature=83.80320976056609}
15> SensorReading{id='sensor_10', timestamp=1612091759321, temperature=68.77967856820972}
1> SensorReading{id='sensor_1', timestamp=1612091759321, temperature=45.75304941852771}
6> SensorReading{id='sensor_6', timestamp=1612091759321, temperature=71.80036477804133}
3> SensorReading{id='sensor_7', timestamp=1612091759321, temperature=55.262086521569564}
2> SensorReading{id='sensor_2', timestamp=1612091759321, temperature=64.0969570576537}
5> SensorReading{id='sensor_5', timestamp=1612091759321, temperature=51.09761352612651}
14> SensorReading{id='sensor_3', timestamp=1612091759313, temperature=32.49085393551031}
4> SensorReading{id='sensor_8', timestamp=1612091759321, temperature=64.83732456896752}
16> SensorReading{id='sensor_4', timestamp=1612091759321, temperature=88.88318538017865}
12> SensorReading{id='sensor_2', timestamp=1612091761325, temperature=65.21522804626638}
16> SensorReading{id='sensor_6', timestamp=1612091761325, temperature=70.49210870668041}
15> SensorReading{id='sensor_5', timestamp=1612091761325, temperature=50.32349231082738}
....
```

## [5.3 Transform](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_53-transform)

map、flatMap、filter通常被统一称为**基本转换算子**（**简单转换算子**）。

### 5.3.1 基本转换算子(map/flatMap/filter)

> [到处是map、flatMap，啥意思？](https://zhuanlan.zhihu.com/p/66196174)

java代码：

```java
package apitest.transform;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


public class TransformTest1_Base {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 使得任务抢占同一个线程
        env.setParallelism(1);

        // 从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

        // 1. map, String => 字符串长度INT
        DataStream<Integer> mapStream = dataStream.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String value) throws Exception {
                return value.length();
            }
        });

        // 2. flatMap，按逗号分割字符串
        DataStream<String> flatMapStream = dataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] fields = value.split(",");
                for(String field:fields){
                    out.collect(field);
                }
            }
        });

        // 3. filter,筛选"sensor_1"开头的数据
        DataStream<String> filterStream = dataStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return value.startsWith("sensor_1");
            }
        });

        // 打印输出
        mapStream.print("map");
        flatMapStream.print("flatMap");
        filterStream.print("filter");

        env.execute();
    }
}
```

输出：

```shell
map> 24
flatMap> sensor_1
flatMap> 1547718199
flatMap> 35.8
filter> sensor_1,1547718199,35.8
map> 24
flatMap> sensor_6
flatMap> 1547718201
flatMap> 15.4
map> 23
flatMap> sensor_7
flatMap> 1547718202
flatMap> 6.7
map> 25
flatMap> sensor_10
flatMap> 1547718205
flatMap> 38.1
filter> sensor_10,1547718205,38.1
map> 24
flatMap> sensor_1
flatMap> 1547718207
flatMap> 36.3
filter> sensor_1,1547718207,36.3
map> 24
flatMap> sensor_1
flatMap> 1547718209
flatMap> 32.8
filter> sensor_1,1547718209,32.8
map> 24
flatMap> sensor_1
flatMap> 1547718212
flatMap> 37.1
filter> sensor_1,1547718212,37.1
```

### 5.3.2 聚合操作算子

> [Flink_Trasform算子](https://blog.csdn.net/dongkang123456/article/details/108361376)

- DataStream里没有reduce和sum这类聚合操作的方法，因为**Flink设计中，所有数据必须先分组才能做聚合操作**。
- **先keyBy得到KeyedStream，然后调用其reduce、sum等聚合操作方法。（先分组后聚合）**

------

常见的聚合操作算子主要有：

- keyBy
- 滚动聚合算子Rolling Aggregation
- reduce

------

#### keyBy

![image-20220220210448694](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220210448694.png)

**DataStream -> KeyedStream**：逻辑地将一个流拆分成不相交的分区，每个分区包含具有相同key的元素，在内部以hash的形式实现的。

1、KeyBy会重新分区； 2、不同的key有可能分到一起，因为是通过hash原理实现的；

#### Rolling Aggregation

这些算子可以针对KeyedStream的每一个支流做聚合。

- sum()
- min()
- max()
- minBy()
- maxBy()

------

测试maxBy的java代码一

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 滚动聚合，测试
 */
public class TransformTest2_RollingAggregation {
    public static void main(String[] args) throws Exception {
        // 创建 执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 执行环境并行度设置1
        env.setParallelism(1);

        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

//        DataStream<SensorReading> sensorStream = dataStream.map(new MapFunction<String, SensorReading>() {
//            @Override
//            public SensorReading map(String value) throws Exception {
//                String[] fields = value.split(",");
//                return new SensorReading(fields[0],new Long(fields[1]),new Double(fields[2]));
//            }
//        });

        DataStream<SensorReading> sensorStream = dataStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });
        // 先分组再聚合
        // 分组
        KeyedStream<SensorReading, String> keyedStream = sensorStream.keyBy(SensorReading::getId);

        // 滚动聚合，max和maxBy区别在于，maxBy除了用于max比较的字段以外，其他字段也会更新成最新的，而max只有比较的字段更新，其他字段不变
        DataStream<SensorReading> resultStream = keyedStream.maxBy("temperature");

        resultStream.print("result");

        env.execute();
    }
}
```

其中`sensor.txt`文件内容如下

```txt
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718209,32.8
sensor_1,1547718212,37.1
```

输出如下：

*由于是滚动更新，每次输出历史最大值，所以下面36.3才会出现两次*

```shell
result> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
result> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
result> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
result> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
result> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
result> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
result> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
```

#### reduce

 **Reduce适用于更加一般化的聚合操作场景**。java中需要实现`ReduceFunction`函数式接口。

------

 在前面Rolling Aggregation的前提下，对需求进行修改。获取同组历史温度最高的传感器信息，同时要求实时更新其时间戳信息。

java代码如下：

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.metrics.stats.Max;

/**
 * 复杂场景，除了获取最大温度的整个传感器信息以外，还要求时间戳更新成最新的
 */
public class TransformTest3_Reduce {
    public static void main(String[] args) throws Exception {
        // 创建 执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 执行环境并行度设置1
        env.setParallelism(1);

        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

        DataStream<SensorReading> sensorStream = dataStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });
        // 先分组再聚合
        // 分组
        KeyedStream<SensorReading, String> keyedStream = sensorStream.keyBy(SensorReading::getId);

        // reduce，自定义规约函数，获取max温度的传感器信息以外，时间戳要求更新成最新的
        DataStream<SensorReading> resultStream = keyedStream.reduce(
                (curSensor,newSensor)->new SensorReading(curSensor.getId(),newSensor.getTimestamp(), Math.max(curSensor.getTemperature(), newSensor.getTemperature()))
        );

        resultStream.print("result");

        env.execute();
    }
}
```

`sensor.txt`文件内容如下：

```txt
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718209,32.8
sensor_1,1547718212,37.1
```

输出如下：

*和前面“Rolling Aggregation”小节不同的是，倒数第二条数据的时间戳用了当前比较时最新的时间戳。*

```shell
result> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
result> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
result> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
result> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
result> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
result> SensorReading{id='sensor_1', timestamp=1547718209, temperature=36.3}
result> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
```

### 5.3.3 多流转换算子

> [Flink_Trasform算子](https://blog.csdn.net/dongkang123456/article/details/108361376)

多流转换算子一般包括：

- Split和Select （新版已经移除）
- Connect和CoMap
- Union

#### Split和Select

**注：新版Flink已经不存在Split和Select这两个API了（至少Flink1.12.1没有！）**

##### Split



![image-20220220210853819](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220220210853819.png)

**SplitStream虽然看起来像是两个Stream，但是其实它是一个特殊的Stream**;

##### Select

**我们可以结合split&select将一个DataStream拆分成多个DataStream。**

------

测试场景：根据传感器温度高低，划分成两组，high和low（>30归入high）：

*这个我发现在Flink当前时间最新版1.12.1已经不是DataStream的方法了，被去除了*

这里直接附上教程代码（Flink1.10.1）

```java
package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.Collections;


public class TransformTest4_MultipleStreams {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 从文件读取数据
    DataStream<String> inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt");

    // 转换成SensorReading
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    } );

    // 1. 分流，按照温度值30度为界分为两条流
    SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
      @Override
      public Iterable<String> select(SensorReading value) {
        return (value.getTemperature() > 30) ? Collections.singletonList("high") : Collections.singletonList("low");
      }
    });

    DataStream<SensorReading> highTempStream = splitStream.select("high");
    DataStream<SensorReading> lowTempStream = splitStream.select("low");
    DataStream<SensorReading> allTempStream = splitStream.select("high", "low");

    highTempStream.print("high");
    lowTempStream.print("low");
    allTempStream.print("all");
    
    env.execute();
  }
}
```

输出结果如下：

```shell
high> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
all > SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
low > SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
all > SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
...
```

#### Connect和CoMap

##### Connect

![image-20220221090902723](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221090902723.png)

 **DataStream,DataStream -> ConnectedStreams**: 连接两个保持他们类型的数据流，两个数据流被Connect 之后，只是被放在了一个流中，内部依然保持各自的数据和形式不发生任何变化，两个流相互独立。

##### CoMap

![image-20220221092740765](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221092740765.png)

**ConnectedStreams -> DataStream**: 作用于ConnectedStreams 上，功能与map和flatMap一样，对ConnectedStreams 中的**每一个Stream分别进行map和flatMap操作**；

------

虽然Flink1.12.1的DataStream有connect和map方法，但是教程基于前面的split和select编写，所以这里直接附上教程的代码：

```java
package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.Collections;


public class TransformTest4_MultipleStreams {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
        DataStream<String> inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt");

        // 转换成SensorReading
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        } );

        // 1. 分流，按照温度值30度为界分为两条流
        SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
            @Override
            public Iterable<String> select(SensorReading value) {
                return (value.getTemperature() > 30) ? Collections.singletonList("high") : Collections.singletonList("low");
            }
        });

        DataStream<SensorReading> highTempStream = splitStream.select("high");
        DataStream<SensorReading> lowTempStream = splitStream.select("low");
        DataStream<SensorReading> allTempStream = splitStream.select("high", "low");

        // highTempStream.print("high");
        // lowTempStream.print("low");
        // allTempStream.print("all");

        // 2. 合流 connect，将高温流转换成二元组类型，与低温流连接合并之后，输出状态信息
        DataStream<Tuple2<String, Double>> warningStream = highTempStream.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(SensorReading value) throws Exception {
                return new Tuple2<>(value.getId(), value.getTemperature());
            }
        });

        ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStreams = warningStream.connect(lowTempStream);

        DataStream<Object> resultStream = connectedStreams.map(new CoMapFunction<Tuple2<String, Double>, SensorReading, Object>() {
            @Override
            public Object map1(Tuple2<String, Double> value) throws Exception {
                return new Tuple3<>(value.f0, value.f1, "high temp warning");
            }

            @Override
            public Object map2(SensorReading value) throws Exception {
                return new Tuple2<>(value.getId(), "normal");
            }
        });

        resultStream.print();
        
        env.execute();
    }
}
```

输出如下：

```shell
(sensor_1,35.8,high temp warning)
(sensor_6,normal)
(sensor_10,38.1,high temp warning)
(sensor_7,normal)
(sensor_1,36.3,high temp warning)
(sensor_1,32.8,high temp warning)
(sensor_1,37.1,high temp warning)
```

#### Union

![image-20220221092844372](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221092844372.png)



**DataStream -> DataStream**：对**两个或者两个以上**的DataStream进行Union操作，产生一个包含多有DataStream元素的新DataStream。

**问题：和Connect的区别？**

1. Connect 的数据类型可以不同，**Connect 只能合并两个流**；
2. **Union可以合并多条流，Union的数据结构必须是一样的**；

```java
// 3. union联合多条流
//        warningStream.union(lowTempStream); 这个不行，因为warningStream类型是DataStream<Tuple2<String, Double>>，而highTempStream是DataStream<SensorReading>
        highTempStream.union(lowTempStream, allTempStream);
```

### 5.3.4 算子转换

 在Storm中，我们常常用Bolt的层级关系来表示各个数据的流向关系，组成一个拓扑。

 在Flink中，**Transformation算子就是将一个或多个DataStream转换为新的DataStream**，可以将多个转换组合成复杂的数据流拓扑。 如下图所示，DataStream会由不同的Transformation操作，转换、过滤、聚合成其他不同的流，从而完成我们的业务要求。

![image-20220221093941125](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221093941125.png)



## 5.4 支持的数据类型

Flink流应用程序处理的是以数据对象表示的事件流。所以在Flink内部，我们需要能够处理这些对象。它们**需要被序列化和反序列化**，以便通过网络传送它们；或者从状态后端、检查点和保存点读取它们。为了有效地做到这一点，Flink需要明确知道应用程序所处理的数据类型。Flink使用类型信息的概念来表示数据类型，并为每个数据类型生成特定的序列化器、反序列化器和比较器。

 Flink还具有一个类型提取系统，该系统分析函数的输入和返回类型，以自动获取类型信息，从而获得序列化器和反序列化器。但是，在某些情况下，例如lambda函数或泛型类型，需要显式地提供类型信息，才能使应用程序正常工作或提高其性能。

 Flink支持Java和Scala中所有常见数据类型。使用最广泛的类型有以下几种。

### 5.4.1 基础数据类型

 Flink支持所有的Java和Scala基础数据类型，Int, Double, Long, String, …

```java
DataStream<Integer> numberStream = env.fromElements(1, 2, 3, 4);
numberStream.map(data -> data * 2);
```

### 5.4.2 Java和Scala元组(Tuples

java不像Scala天生支持元组Tuple类型，java的元组类型由Flink的包提供，默认提供Tuple0~Tuple25

```java
DataStream<Tuple2<String, Integer>> personStream = env.fromElements( 
  new Tuple2("Adam", 17), 
  new Tuple2("Sarah", 23) 
); 
personStream.filter(p -> p.f1 > 18);
```

### 5.4.3 Scala样例类(case classes)

```scala
case class Person(name:String,age:Int)

val numbers: DataStream[(String,Integer)] = env.fromElements(
  Person("张三",12),
  Person("李四"，23)
)
```

### 5.4.4 Java简单对象(POJO)

java的POJO这里要求必须提供无参构造函数

- 成员变量要求都是public（或者private但是提供get、set方法）

```java
public class Person{
  public String name;
  public int age;
  public Person() {}
  public Person( String name , int age) {
    this.name = name;
    this.age = age;
  }
}
DataStream Pe rson > persons = env.fromElements(
  new Person (" Alex", 42),
  new Person (" Wendy",23)
);
```

### 5.4.5 其他(Arrays, Lists, Maps, Enums,等等)

Flink对Java和Scala中的一些特殊目的的类型也都是支持的，比如Java的ArrayList，HashMap，Enum等等。

## 5.5 实现UDF函数——更细粒度的控制流

### 5.5.1 函数类(Function Classes)

 Flink暴露了所有UDF函数的接口(实现方式为接口或者抽象类)。例如MapFunction, FilterFunction, ProcessFunction等等。

 下面例子实现了FilterFunction接口：

```java
DataStream<String> flinkTweets = tweets.filter(new FlinkFilter()); 
public static class FlinkFilter implements FilterFunction<String> { 
  @Override public boolean filter(String value) throws Exception { 
    return value.contains("flink");
  }
}
```

 还可以将函数实现成匿名类

```java
DataStream<String> flinkTweets = tweets.filter(
  new FilterFunction<String>() { 
    @Override public boolean filter(String value) throws Exception { 
      return value.contains("flink"); 
    }
  }
);
```

 我们filter的字符串"flink"还可以当作参数传进去。

```java
DataStream<String> tweets = env.readTextFile("INPUT_FILE "); 
DataStream<String> flinkTweets = tweets.filter(new KeyWordFilter("flink")); 
public static class KeyWordFilter implements FilterFunction<String> { 
  private String keyWord; 

  KeyWordFilter(String keyWord) { 
    this.keyWord = keyWord; 
  } 

  @Override public boolean filter(String value) throws Exception { 
    return value.contains(this.keyWord); 
  } 
}
```

### 5.5.2 匿名函数(Lambda Functions)

```java
DataStream<String> tweets = env.readTextFile("INPUT_FILE"); 
DataStream<String> flinkTweets = tweets.filter( tweet -> tweet.contains("flink") );
```

### 5.5.3 富函数(Rich Functions)

 “富函数”是DataStream API提供的一个函数类的接口，所有Flink函数类都有其Rich版本。

 **它与常规函数的不同在于，可以获取运行环境的上下文，并拥有一些生命周期方法，所以可以实现更复杂的功能**。

- RichMapFunction
- RichFlatMapFunction
- RichFilterFunction
- …

 Rich Function有一个**生命周期**的概念。典型的生命周期方法有：

- **`open()`方法是rich function的初始化方法，当一个算子例如map或者filter被调用之前`open()`会被调用。**
- **`close()`方法是生命周期中的最后一个调用的方法，做一些清理工作。**
- **`getRuntimeContext()`方法提供了函数的RuntimeContext的一些信息，例如函数执行的并行度，任务的名字，以及state状态**

```java
public static class MyMapFunction extends RichMapFunction<SensorReading, Tuple2<Integer, String>> { 

  @Override public Tuple2<Integer, String> map(SensorReading value) throws Exception {
    return new Tuple2<>(getRuntimeContext().getIndexOfThisSubtask(), value.getId()); 
  } 

  @Override public void open(Configuration parameters) throws Exception { 
    System.out.println("my map open"); // 以下可以做一些初始化工作，例如建立一个和HDFS的连接 
  } 

  @Override public void close() throws Exception { 
    System.out.println("my map close"); // 以下做一些清理工作，例如断开和HDFS的连接 
  } 
}
```

------

测试代码：

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class TransformTest5_RichFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        DataStream<Tuple2<String, Integer>> resultStream = dataStream.map( new MyMapper() );

        resultStream.print();

        env.execute();
    }

    // 传统的Function不能获取上下文信息，只能处理当前数据，不能和其他数据交互
    public static class MyMapper0 implements MapFunction<SensorReading, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
            return new Tuple2<>(value.getId(), value.getId().length());
        }
    }

    // 实现自定义富函数类（RichMapFunction是一个抽象类）
    public static class MyMapper extends RichMapFunction<SensorReading, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
//            RichFunction可以获取State状态
//            getRuntimeContext().getState();
            return new Tuple2<>(value.getId(), getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化工作，一般是定义状态，或者建立数据库连接
            System.out.println("open");
        }

        @Override
        public void close() throws Exception {
            // 一般是关闭连接和清空状态的收尾操作
            System.out.println("close");
        }
    }
}
```

输出如下：

由于设置了执行环境env的并行度为4，所以有4个slot执行自定义的RichFunction，输出4次open和close

```shell
open
open
open
open
4> (sensor_1,3)
4> (sensor_6,3)
close
2> (sensor_1,1)
2> (sensor_1,1)
close
3> (sensor_1,2)
close
1> (sensor_7,0)
1> (sensor_10,0)
close
```

## 5.6 数据重分区操作

重分区操作，在DataStream类中可以看到很多`Partitioner`字眼的类。

**其中`partitionCustom(...)`方法用于自定义重分区**。

java代码：

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class TransformTest6_Partition {
  public static void main(String[] args) throws Exception{

    // 创建执行环境
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // 设置并行度 = 4
    env.setParallelism(4);

    // 从文件读取数据
    DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

    // 转换成SensorReading类型
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    });

    // SingleOutputStreamOperator多并行度默认就rebalance,轮询方式分配
    dataStream.print("input");

    // 1. shuffle (并非批处理中的获取一批后才打乱，这里每次获取到直接打乱且分区)
    DataStream<String> shuffleStream = inputStream.shuffle();
    shuffleStream.print("shuffle");

    // 2. keyBy (Hash，然后取模)
    dataStream.keyBy(SensorReading::getId).print("keyBy");

    // 3. global (直接发送给第一个分区，少数特殊情况才用)
    dataStream.global().print("global");

    env.execute();
  }
}
```

输出：

```shell
input:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
input:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
input:1> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
input:1> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
shuffle:2> sensor_6,1547718201,15.4
shuffle:1> sensor_1,1547718199,35.8
input:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
input:4> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
shuffle:1> sensor_1,1547718207,36.3
shuffle:2> sensor_1,1547718209,32.8
global:1> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
global:1> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
keyBy:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
global:1> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
shuffle:1> sensor_7,1547718202,6.7
global:1> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
shuffle:2> sensor_10,1547718205,38.1
input:2> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
global:1> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
keyBy:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
keyBy:2> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
global:1> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
shuffle:1> sensor_1,1547718212,37.1
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
global:1> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
```

## [5.7 Sink](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_57-sink)

> [Flink之流处理API之Sink](https://blog.csdn.net/lixinkuan328/article/details/104116894)

 Flink没有类似于spark中foreach方法，让用户进行迭代的操作。虽有对外的输出操作都要利用Sink完成。最后通过类似如下方式完成整个任务最终输出操作。

```java
stream.addSink(new MySink(xxxx)) 
```

 官方提供了一部分的框架的sink。除此以外，需要用户自定义实现sink。

## [5.5 实现UDF函数——更细粒度的控制流](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_55-实现udf函数更细粒度的控制流)

### [5.5.1 函数类(Function Classes)](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_551-函数类function-classes)

 Flink暴露了所有UDF函数的接口(实现方式为接口或者抽象类)。例如MapFunction, FilterFunction, ProcessFunction等等。

 下面例子实现了FilterFunction接口：

```java
DataStream<String> flinkTweets = tweets.filter(new FlinkFilter()); 
public static class FlinkFilter implements FilterFunction<String> { 
  @Override public boolean filter(String value) throws Exception { 
    return value.contains("flink");
  }
}
```

 还可以将函数实现成匿名类

```java
DataStream<String> flinkTweets = tweets.filter(
  new FilterFunction<String>() { 
    @Override public boolean filter(String value) throws Exception { 
      return value.contains("flink"); 
    }
  }
);
```

 我们filter的字符串"flink"还可以当作参数传进去。

```java
DataStream<String> tweets = env.readTextFile("INPUT_FILE "); 
DataStream<String> flinkTweets = tweets.filter(new KeyWordFilter("flink")); 
public static class KeyWordFilter implements FilterFunction<String> { 
  private String keyWord; 

  KeyWordFilter(String keyWord) { 
    this.keyWord = keyWord; 
  } 

  @Override public boolean filter(String value) throws Exception { 
    return value.contains(this.keyWord); 
  } 
}
```

### [5.5.2 匿名函数(Lambda Functions)](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_552-匿名函数lambda-functions)

```java
DataStream<String> tweets = env.readTextFile("INPUT_FILE"); 
DataStream<String> flinkTweets = tweets.filter( tweet -> tweet.contains("flink") );
```

### [5.5.3 富函数(Rich Functions)](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_553-富函数rich-functions)

 “富函数”是DataStream API提供的一个函数类的接口，所有Flink函数类都有其Rich版本。

 **它与常规函数的不同在于，可以获取运行环境的上下文，并拥有一些生命周期方法，所以可以实现更复杂的功能**。

- RichMapFunction
- RichFlatMapFunction
- RichFilterFunction
- …

 Rich Function有一个**生命周期**的概念。典型的生命周期方法有：

- **`open()`方法是rich function的初始化方法，当一个算子例如map或者filter被调用之前`open()`会被调用。**
- **`close()`方法是生命周期中的最后一个调用的方法，做一些清理工作。**
- **`getRuntimeContext()`方法提供了函数的RuntimeContext的一些信息，例如函数执行的并行度，任务的名字，以及state状态**

```java
public static class MyMapFunction extends RichMapFunction<SensorReading, Tuple2<Integer, String>> { 

  @Override public Tuple2<Integer, String> map(SensorReading value) throws Exception {
    return new Tuple2<>(getRuntimeContext().getIndexOfThisSubtask(), value.getId()); 
  } 

  @Override public void open(Configuration parameters) throws Exception { 
    System.out.println("my map open"); // 以下可以做一些初始化工作，例如建立一个和HDFS的连接 
  } 

  @Override public void close() throws Exception { 
    System.out.println("my map close"); // 以下做一些清理工作，例如断开和HDFS的连接 
  } 
}
```

------

测试代码：

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class TransformTest5_RichFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        DataStream<Tuple2<String, Integer>> resultStream = dataStream.map( new MyMapper() );

        resultStream.print();

        env.execute();
    }

    // 传统的Function不能获取上下文信息，只能处理当前数据，不能和其他数据交互
    public static class MyMapper0 implements MapFunction<SensorReading, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
            return new Tuple2<>(value.getId(), value.getId().length());
        }
    }

    // 实现自定义富函数类（RichMapFunction是一个抽象类）
    public static class MyMapper extends RichMapFunction<SensorReading, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
//            RichFunction可以获取State状态
//            getRuntimeContext().getState();
            return new Tuple2<>(value.getId(), getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化工作，一般是定义状态，或者建立数据库连接
            System.out.println("open");
        }

        @Override
        public void close() throws Exception {
            // 一般是关闭连接和清空状态的收尾操作
            System.out.println("close");
        }
    }
}
```

输出如下：

由于设置了执行环境env的并行度为4，所以有4个slot执行自定义的RichFunction，输出4次open和close

```shell
open
open
open
open
4> (sensor_1,3)
4> (sensor_6,3)
close
2> (sensor_1,1)
2> (sensor_1,1)
close
3> (sensor_1,2)
close
1> (sensor_7,0)
1> (sensor_10,0)
close
```

## [5.6 数据重分区操作](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=_56-数据重分区操作)

重分区操作，在DataStream类中可以看到很多`Partitioner`字眼的类。

**其中`partitionCustom(...)`方法用于自定义重分区**。

java代码：

```java
package apitest.transform;

import apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class TransformTest6_Partition {
  public static void main(String[] args) throws Exception{

    // 创建执行环境
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // 设置并行度 = 4
    env.setParallelism(4);

    // 从文件读取数据
    DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");

    // 转换成SensorReading类型
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    });

    // SingleOutputStreamOperator多并行度默认就rebalance,轮询方式分配
    dataStream.print("input");

    // 1. shuffle (并非批处理中的获取一批后才打乱，这里每次获取到直接打乱且分区)
    DataStream<String> shuffleStream = inputStream.shuffle();
    shuffleStream.print("shuffle");

    // 2. keyBy (Hash，然后取模)
    dataStream.keyBy(SensorReading::getId).print("keyBy");

    // 3. global (直接发送给第一个分区，少数特殊情况才用)
    dataStream.global().print("global");

    env.execute();
  }
}
```

输出：

```shell
input:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
input:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
input:1> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
input:1> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
shuffle:2> sensor_6,1547718201,15.4
shuffle:1> sensor_1,1547718199,35.8
input:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
input:4> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
shuffle:1> sensor_1,1547718207,36.3
shuffle:2> sensor_1,1547718209,32.8
global:1> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
global:1> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
keyBy:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
global:1> SensorReading{id='sensor_1', timestamp=1547718207, temperature=36.3}
shuffle:1> sensor_7,1547718202,6.7
global:1> SensorReading{id='sensor_1', timestamp=1547718209, temperature=32.8}
shuffle:2> sensor_10,1547718205,38.1
input:2> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
global:1> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
keyBy:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
keyBy:2> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
global:1> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
shuffle:1> sensor_1,1547718212,37.1
keyBy:3> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
global:1> SensorReading{id='sensor_1', timestamp=1547718212, temperature=37.1}
```

## 5.7 Sink

 Flink没有类似于spark中foreach方法，让用户进行迭代的操作。虽有对外的输出操作都要利用Sink完成。最后通过类似如下方式完成整个任务最终输出操作。

```java
stream.addSink(new MySink(xxxx)) 
```

 官方提供了一部分的框架的sink。除此以外，需要用户自定义实现

![image-20220221192540874](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221192540874.png)



### 5.7.1 Kafka

1. pom依赖

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>org.example</groupId>
       <artifactId>Flink_Tutorial</artifactId>
       <version>1.0-SNAPSHOT</version>
   
       <properties>
           <maven.compiler.source>8</maven.compiler.source>
           <maven.compiler.target>8</maven.compiler.target>
           <flink.version>1.12.1</flink.version>
           <scala.binary.version>2.12</scala.binary.version>
       </properties>
   
       <dependencies>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-java</artifactId>
               <version>${flink.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-streaming-scala_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-clients_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
   
           <!-- kafka -->
           <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-connector-kafka_${scala.binary.version}</artifactId>
               <version>${flink.version}</version>
           </dependency>
       </dependencies>
   
   </project>
   ```

2. 编写java代码

   ```java
   package apitest.sink;
   
   import apitest.beans.SensorReading;
   import org.apache.flink.api.common.serialization.SimpleStringSchema;
   import org.apache.flink.streaming.api.datastream.DataStream;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
   import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
   
   import java.util.Properties;
   
   
   public class SinkTest1_Kafka {
       public static void main(String[] args) throws Exception{
           // 创建执行环境
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
   
           // 并行度设置为1
           env.setParallelism(1);
   
           Properties properties = new Properties();
           properties.setProperty("bootstrap.servers", "localhost:9092");
           properties.setProperty("group.id", "consumer-group");
           properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("auto.offset.reset", "latest");
   
           // 从Kafka中读取数据
           DataStream<String> inputStream = env.addSource( new FlinkKafkaConsumer<String>("sensor", new SimpleStringSchema(), properties));
   
           // 序列化从Kafka中读取的数据
           DataStream<String> dataStream = inputStream.map(line -> {
               String[] fields = line.split(",");
               return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2])).toString();
           });
   
           // 将数据写入Kafka
           dataStream.addSink( new FlinkKafkaProducer<String>("localhost:9092", "sinktest", new SimpleStringSchema()));
           
           env.execute();
       }
   }
   ```

3. 启动zookeeper

   ```shell
   $ bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

4. 启动kafka服务

   ```shell
   $ bin/kafka-server-start.sh config/server.properties
   ```

5. 新建kafka生产者console

   ```shell
   $ bin/kafka-console-producer.sh --broker-list localhost:9092  --topic sensor
   ```

6. 新建kafka消费者console

   ```shell
   $ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sinktest
   ```

7. 运行Flink程序，在kafka生产者console输入数据，查看kafka消费者console的输出结果

   输入(kafka生产者console)

   ```shell
   >sensor_1,1547718199,35.8
   >sensor_6,1547718201,15.4
   ```

   输出(kafka消费者console)

   ```shell
   SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
   SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
   ```

这里Flink的作用相当于pipeline了。

### 5.7.2 Redis

> [flink-connector-redis](https://mvnrepository.com/search?q=flink-connector-redis)
>
> 查询Flink连接器，最简单的就是查询关键字`flink-connector-`

这里将Redis当作sink的输出对象。

1. pom依赖

   这个可谓相当老的依赖了，2017年的。

   ```xml
   <!-- https://mvnrepository.com/artifact/org.apache.bahir/flink-connector-redis -->
   <dependency>
       <groupId>org.apache.bahir</groupId>
       <artifactId>flink-connector-redis_2.11</artifactId>
       <version>1.0</version>
   </dependency>
   ```

2. 编写java代码

   ```java
   package apitest.sink;
   
   import apitest.beans.SensorReading;
   import org.apache.flink.streaming.api.datastream.DataStream;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.connectors.redis.RedisSink;
   import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
   import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
   import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
   import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
   
   
   public class SinkTest2_Redis {
       public static void main(String[] args) throws Exception {
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
           env.setParallelism(1);
   
           // 从文件读取数据
           DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");
   
           // 转换成SensorReading类型
           DataStream<SensorReading> dataStream = inputStream.map(line -> {
               String[] fields = line.split(",");
               return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
           });
   
           // 定义jedis连接配置(我这里连接的是docker的redis)
           FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                   .setHost("localhost")
                   .setPort(6379)
                   .setPassword("123456")
                   .setDatabase(0)
                   .build();
   
           dataStream.addSink(new RedisSink<>(config, new MyRedisMapper()));
   
           env.execute();
       }
   
       // 自定义RedisMapper
       public static class MyRedisMapper implements RedisMapper<SensorReading> {
           // 定义保存数据到redis的命令，存成Hash表，hset sensor_temp id temperature
           @Override
           public RedisCommandDescription getCommandDescription() {
               return new RedisCommandDescription(RedisCommand.HSET, "sensor_temp");
           }
   
           @Override
           public String getKeyFromData(SensorReading data) {
               return data.getId();
           }
   
           @Override
           public String getValueFromData(SensorReading data) {
               return data.getTemperature().toString();
           }
       }
   }
   ```

3. 启动redis服务

4. 启动Flink程序

5. 查看Redis里的数据

   *因为最新数据覆盖前面的，所以最后redis里呈现的是最新的数据。*

   ```shell
   localhost:0>hgetall sensor_temp
   1) "sensor_1"
   2) "37.1"
   3) "sensor_6"
   4) "15.4"
   5) "sensor_7"
   6) "6.7"
   7) "sensor_10"
   8) "38.1"
   ```

### 5.7.3 Elasticsearch

1. pom依赖

   ```xml
   <!-- ElasticSearch7 -->
   <dependency>
       <groupId>org.apache.flink</groupId>
       <artifactId>flink-connector-elasticsearch7_2.12</artifactId>
       <version>1.12.1</version>
   </dependency>
   ```

2. 编写java代码

   ```java
   package apitest.sink;
   
   import apitest.beans.SensorReading;
   import org.apache.flink.api.common.functions.RuntimeContext;
   import org.apache.flink.streaming.api.datastream.DataStream;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
   import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
   import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
   import org.apache.http.HttpHost;
   import org.elasticsearch.action.index.IndexRequest;
   import org.elasticsearch.client.Requests;
   
   import java.util.ArrayList;
   import java.util.HashMap;
   import java.util.List;
   
   
   public class SinkTest3_Es {
       public static void main(String[] args) throws Exception {
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
           env.setParallelism(1);
   
           // 从文件读取数据
           DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");
   
           // 转换成SensorReading类型
           DataStream<SensorReading> dataStream = inputStream.map(line -> {
               String[] fields = line.split(",");
               return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
           });
   
           // 定义es的连接配置
           List<HttpHost> httpHosts = new ArrayList<>();
           httpHosts.add(new HttpHost("localhost", 9200));
   
           dataStream.addSink( new ElasticsearchSink.Builder<SensorReading>(httpHosts, new MyEsSinkFunction()).build());
   
           env.execute();
       }
   
       // 实现自定义的ES写入操作
       public static class MyEsSinkFunction implements ElasticsearchSinkFunction<SensorReading> {
           @Override
           public void process(SensorReading element, RuntimeContext ctx, RequestIndexer indexer) {
               // 定义写入的数据source
               HashMap<String, String> dataSource = new HashMap<>();
               dataSource.put("id", element.getId());
               dataSource.put("temp", element.getTemperature().toString());
               dataSource.put("ts", element.getTimestamp().toString());
   
               // 创建请求，作为向es发起的写入命令(ES7统一type就是_doc，不再允许指定type)
               IndexRequest indexRequest = Requests.indexRequest()
                       .index("sensor")
                       .source(dataSource);
   
               // 用index发送请求
               indexer.add(indexRequest);
           }
       }
   }
   ```

3. 启动ElasticSearch（我这里是docker启动的

4. 运行Flink程序，查看ElasticSearch是否新增数据

   ```shell
   $ curl "localhost:9200/sensor/_search?pretty"
   {
     "took" : 1,
     "timed_out" : false,
     "_shards" : {
       "total" : 1,
       "successful" : 1,
       "skipped" : 0,
       "failed" : 0
     },
     "hits" : {
       "total" : {
         "value" : 7,
         "relation" : "eq"
       },
       "max_score" : 1.0,
       "hits" : [
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "jciyWXcBiXrGJa12kSQt",
           "_score" : 1.0,
           "_source" : {
             "temp" : "35.8",
             "id" : "sensor_1",
             "ts" : "1547718199"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "jsiyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "15.4",
             "id" : "sensor_6",
             "ts" : "1547718201"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "j8iyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "6.7",
             "id" : "sensor_7",
             "ts" : "1547718202"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "kMiyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "38.1",
             "id" : "sensor_10",
             "ts" : "1547718205"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "kciyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "36.3",
             "id" : "sensor_1",
             "ts" : "1547718207"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "ksiyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "32.8",
             "id" : "sensor_1",
             "ts" : "1547718209"
           }
         },
         {
           "_index" : "sensor",
           "_type" : "_doc",
           "_id" : "k8iyWXcBiXrGJa12kSQu",
           "_score" : 1.0,
           "_source" : {
             "temp" : "37.1",
             "id" : "sensor_1",
             "ts" : "1547718212"
           }
         }
       ]
     }
   }
   ```

### 5.7.4 JDBC自定义sink

> [JDBC Connector](https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/connectors/jdbc.html) <= 官方目前没有专门针对MySQL的，我们自己实现就好了

这里测试的是连接MySQL。

1. pom依赖（我本地docker里的mysql是8.0.19版本的）

   ```xml
   <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.19</version>
   </dependency>
   ```

2. 启动mysql服务（我本地是docker启动的）

3. 新建数据库

   ```sql
   CREATE DATABASE `flink_test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```

4. 新建schema

   ```sql
   CREATE TABLE `sensor_temp` (
     `id` varchar(32) NOT NULL,
     `temp` double NOT NULL
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
   ```

5. 编写java代码

   ```java
   package apitest.sink;
   
   import apitest.beans.SensorReading;
   import apitest.source.SourceTest4_UDF;
   import org.apache.flink.configuration.Configuration;
   import org.apache.flink.streaming.api.datastream.DataStream;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
   
   import java.sql.Connection;
   import java.sql.DriverManager;
   import java.sql.PreparedStatement;
   
   
   public class SinkTest4_Jdbc {
       public static void main(String[] args) throws Exception {
   
           // 创建执行环境
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
   
           // 设置并行度 = 1
           env.setParallelism(1);
   
           // 从文件读取数据
   //        DataStream<String> inputStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");
   //
   //        // 转换成SensorReading类型
   //        DataStream<SensorReading> dataStream = inputStream.map(line -> {
   //            String[] fields = line.split(",");
   //            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
   //        });
   
           // 使用之前编写的随机变动温度的SourceFunction来生成数据
           DataStream<SensorReading> dataStream = env.addSource(new SourceTest4_UDF.MySensorSource());
   
           dataStream.addSink(new MyJdbcSink());
   
           env.execute();
       }
   
       // 实现自定义的SinkFunction
       public static class MyJdbcSink extends RichSinkFunction<SensorReading> {
           // 声明连接和预编译语句
           Connection connection = null;
           PreparedStatement insertStmt = null;
           PreparedStatement updateStmt = null;
   
           @Override
           public void open(Configuration parameters) throws Exception {
               connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flink_test?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useSSL=false", "root", "example");
               insertStmt = connection.prepareStatement("insert into sensor_temp (id, temp) values (?, ?)");
               updateStmt = connection.prepareStatement("update sensor_temp set temp = ? where id = ?");
           }
   
           // 每来一条数据，调用连接，执行sql
           @Override
           public void invoke(SensorReading value, Context context) throws Exception {
               // 直接执行更新语句，如果没有更新那么就插入
               updateStmt.setDouble(1, value.getTemperature());
               updateStmt.setString(2, value.getId());
               updateStmt.execute();
               if (updateStmt.getUpdateCount() == 0) {
                   insertStmt.setString(1, value.getId());
                   insertStmt.setDouble(2, value.getTemperature());
                   insertStmt.execute();
               }
           }
   
           @Override
           public void close() throws Exception {
               insertStmt.close();
               updateStmt.close();
               connection.close();
           }
       }
   }
   ```

6. 运行Flink程序，查看MySQL数据（可以看到MySQL里的数据一直在变动）

   ```shell
   mysql> SELECT * FROM sensor_temp;
   +-----------+--------------------+
   | id        | temp               |
   +-----------+--------------------+
   | sensor_3  | 20.489172407885917 |
   | sensor_10 |  73.01289164711463 |
   | sensor_4  | 43.402500895809744 |
   | sensor_1  |  6.894772325662007 |
   | sensor_2  | 101.79309911751122 |
   | sensor_7  | 63.070612021580324 |
   | sensor_8  |  63.82606628090501 |
   | sensor_5  |  57.67115738487047 |
   | sensor_6  |  50.84442627975055 |
   | sensor_9  |  52.58400793021675 |
   +-----------+--------------------+
   10 rows in set (0.00 sec)
   
   mysql> SELECT * FROM sensor_temp;
   +-----------+--------------------+
   | id        | temp               |
   +-----------+--------------------+
   | sensor_3  | 19.498209543035923 |
   | sensor_10 |  71.92981963197121 |
   | sensor_4  | 43.566017489470426 |
   | sensor_1  |  6.378208186786803 |
   | sensor_2  | 101.71010087830145 |
   | sensor_7  |  62.11402602179431 |
   | sensor_8  |  64.33196455020062 |
   | sensor_5  |  56.39071692662006 |
   | sensor_6  | 48.952784757264894 |
   | sensor_9  | 52.078086096436685 |
   +-----------+--------------------+
   10 rows in set (0.00 sec)
   ```

## 5.8 Joining

### 5.8.1 Window Join

 A window join joins the elements of two streams that share a common key and lie in the same window. These windows can be defined by using a [window assigner](https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/stream/operators/windows.html#window-assigners) and are evaluated on elements from both of the streams.

 The elements from both sides are then passed to a user-defined `JoinFunction` or `FlatJoinFunction` where the user can emit results that meet the join criteria.

 The general usage can be summarized as follows:

```java
stream.join(otherStream)
    .where(<KeySelector>)
    .equalTo(<KeySelector>)
    .window(<WindowAssigner>)
    .apply(<JoinFunction>)
```

#### Tumbling Window Join

 When performing a tumbling window join, all elements with a common key and a common tumbling window are joined as pairwise combinations and passed on to a `JoinFunction` or `FlatJoinFunction`. Because this behaves like an inner join, elements of one stream that do not have elements from another stream in their tumbling window are not emitted!

![image-20220221202621588](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202621588.png)

 As illustrated in the figure, we define a tumbling window with the size of 2 milliseconds, which results in windows of the form `[0,1], [2,3], ...`. The image shows the pairwise combinations of all elements in each window which will be passed on to the `JoinFunction`. Note that in the tumbling window `[6,7]` nothing is emitted because no elements exist in the green stream to be joined with the orange elements ⑥ and ⑦.

```java
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
 
...

DataStream<Integer> orangeStream = ...
DataStream<Integer> greenStream = ...

orangeStream.join(greenStream)
    .where(<KeySelector>)
    .equalTo(<KeySelector>)
    .window(TumblingEventTimeWindows.of(Time.milliseconds(2)))
    .apply (new JoinFunction<Integer, Integer, String> (){
        @Override
        public String join(Integer first, Integer second) {
            return first + "," + second;
        }
    });
```

#### Sliding Window Join

 When performing a sliding window join, all elements with a common key and common sliding window are joined as pairwise combinations and passed on to the `JoinFunction` or `FlatJoinFunction`. Elements of one stream that do not have elements from the other stream in the current sliding window are not emitted! Note that some elements might be joined in one sliding window but not in another!

![image-20220221202633905](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202633905.png)

 In this example we are using sliding windows with a size of two milliseconds and slide them by one millisecond, resulting in the sliding windows `[-1, 0],[0,1],[1,2],[2,3], …`. The joined elements below the x-axis are the ones that are passed to the `JoinFunction` for each sliding window. Here you can also see how for example the orange ② is joined with the green ③ in the window `[2,3]`, but is not joined with anything in the window `[1,2]`.

```java
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

...

DataStream<Integer> orangeStream = ...
DataStream<Integer> greenStream = ...

orangeStream.join(greenStream)
    .where(<KeySelector>)
    .equalTo(<KeySelector>)
    .window(SlidingEventTimeWindows.of(Time.milliseconds(2) /* size */, Time.milliseconds(1) /* slide */))
    .apply (new JoinFunction<Integer, Integer, String> (){
        @Override
        public String join(Integer first, Integer second) {
            return first + "," + second;
        }
    });
```

#### Session Window Join

 When performing a session window join, all elements with the same key that when *“combined”* fulfill the session criteria are joined in pairwise combinations and passed on to the `JoinFunction` or `FlatJoinFunction`. Again this performs an inner join, so if there is a session window that only contains elements from one stream, no output will be emitted!

![image-20220221202645802](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202645802.png)

 Here we define a session window join where each session is divided by a gap of at least 1ms. There are three sessions, and in the first two sessions the joined elements from both streams are passed to the `JoinFunction`. In the third session there are no elements in the green stream, so ⑧ and ⑨ are not joined!

```java
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
 
...

DataStream<Integer> orangeStream = ...
DataStream<Integer> greenStream = ...

orangeStream.join(greenStream)
    .where(<KeySelector>)
    .equalTo(<KeySelector>)
    .window(EventTimeSessionWindows.withGap(Time.milliseconds(1)))
    .apply (new JoinFunction<Integer, Integer, String> (){
        @Override
        public String join(Integer first, Integer second) {
            return first + "," + second;
        }
    });
```

### 5.8.2 Interval Join

The interval join joins elements of two streams (we’ll call them A & B for now) with a common key and where elements of stream B have timestamps that lie in a relative time interval to timestamps of elements in stream A.

This can also be expressed more formally as `b.timestamp ∈ [a.timestamp + lowerBound; a.timestamp + upperBound]` or `a.timestamp + lowerBound <= b.timestamp <= a.timestamp + upperBound`

where a and b are elements of A and B that share a common key. Both the lower and upper bound can be either negative or positive as long as as the lower bound is always smaller or equal to the upper bound. The interval join currently only performs inner joins.

When a pair of elements are passed to the `ProcessJoinFunction`, they will be assigned with the larger timestamp (which can be accessed via the `ProcessJoinFunction.Context`) of the two elements.

**Note** The interval join currently only supports event time.

![image-20220221202703771](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202703771.png)

In the example above, we join two streams ‘orange’ and ‘green’ with a lower bound of -2 milliseconds and an upper bound of +1 millisecond. Be default, these boundaries are inclusive, but `.lowerBoundExclusive()` and `.upperBoundExclusive` can be applied to change the behaviour.

Using the more formal notation again this will translate to

```
orangeElem.ts + lowerBound <= greenElem.ts <= orangeElem.ts + upperBound
```

as indicated by the triangles.

```java
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

...

DataStream<Integer> orangeStream = ...
DataStream<Integer> greenStream = ...

orangeStream
    .keyBy(<KeySelector>)
    .intervalJoin(greenStream.keyBy(<KeySelector>))
    .between(Time.milliseconds(-2), Time.milliseconds(1))
    .process (new ProcessJoinFunction<Integer, Integer, String(){

        @Override
        public void processElement(Integer left, Integer right, Context ctx, Collector<String> out) {
            out.collect(first + "," + second);
        }
    });
```





# 6. Flink的Window

## 6.1 Window

### 6.1.1 概述

![image-20220221202109121](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202109121.png)

 streaming流式计算是一种被设计用于处理无限数据集的数据处理引擎，而无限数据集是指一种不断增长的本质上无限的数据集，而**window是一种切割无限数据为有限块进行处理的手段**。

 **Window是无限数据流处理的核心，Window将一个无限的stream拆分成有限大小的”buckets”桶，我们可以在这些桶上做计算操作**。

*举例子：假设按照时间段划分桶，接收到的数据马上能判断放到哪个桶，且多个桶的数据能并行被处理。（迟到的数据也可判断是原本属于哪个桶的）*

### 6.1.2 Window类型

- 时间窗口（Time Window）
  - 滚动时间窗口
  - 滑动时间窗口
  - 会话窗口
- 计数窗口（Count Window）
  - 滚动计数窗口
  - 滑动计数窗口

**TimeWindow：按照时间生成Window**

**CountWindow：按照指定的数据条数生成一个Window，与时间无关**

#### 滚动窗口(Tumbling Windows)

![image-20220221202137536](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202137536.png)

- 依据**固定的窗口长度**对数据进行切分
- 时间对齐，窗口长度固定，没有重叠

#### 滑动窗口(Sliding Windows)

![image-20220221202201621](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220221202201621.png)

- 可以按照固定的长度向后滑动固定的距离
- 滑动窗口由**固定的窗口长度**和**滑动间隔**组成
- 可以有重叠(是否重叠和滑动距离有关系)
- 滑动窗口是固定窗口的更广义的一种形式，滚动窗口可以看做是滑动窗口的一种特殊情况（即窗口大小和滑动间隔相等）

#### 会话窗口(Session Windows)

![image-20220222083440471](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222083440471.png)

- 由一系列事件组合一个指定时间长度的timeout间隙组成，也就是一段时间没有接收到新数据就会生成新的窗口
- 特点：时间无对齐

## 6.2 Window API

### 6.2.1 概述)

- 窗口分配器——`window()`方法

- 我们可以用`.window()`来定义一个窗口，然后基于这个window去做一些聚合或者其他处理操作。

  **注意`window()`方法必须在keyBy之后才能使用**。

- Flink提供了更加简单的`.timeWindow()`和`.countWindow()`方法，用于定义时间窗口和计数窗口。

```java
DataStream<Tuple2<String,Double>> minTempPerWindowStream = 
  datastream
  .map(new MyMapper())
  .keyBy(data -> data.f0)
  .timeWindow(Time.seconds(15))
  .minBy(1);
```

#### 窗口分配器(window assigner)

- `window()`方法接收的输入参数是一个WindowAssigner
- WindowAssigner负责将每条输入的数据分发到正确的window中
- Flink提供了通用的WindowAssigner
  - 滚动窗口（tumbling window）
  - 滑动窗口（sliding window）
  - 会话窗口（session window）
  - **全局窗口（global window）**

#### 创建不同类型的窗口

- 滚动时间窗口（tumbling time window）

  `.timeWindow(Time.seconds(15))`

- 滑动时间窗口（sliding time window）

  `.timeWindow(Time.seconds(15),Time.seconds(5))`

- 会话窗口（session window）

  `.window(EventTimeSessionWindows.withGap(Time.minutes(10)))`

- 滚动计数窗口（tumbling count window）

  `.countWindow(5)`

- 滑动计数窗口（sliding count window）

  `.countWindow(10,2)`

*DataStream的`windowAll()`类似分区的global操作，这个操作是non-parallel的(并行度强行为1)，所有的数据都会被传递到同一个算子operator上，官方建议如果非必要就不要用这个API*

### 6.2.2 TimeWindow

 TimeWindow将指定时间范围内的所有数据组成一个window，一次对一个window里面的所有数据进行计算。

#### 滚动窗口

 Flink默认的时间窗口根据ProcessingTime进行窗口的划分，将Flink获取到的数据根据进入Flink的时间划分到不同的窗口中。

```java
DataStream<Tuple2<String, Double>> minTempPerWindowStream = dataStream 
  .map(new MapFunction<SensorReading, Tuple2<String, Double>>() { 
    @Override 
    public Tuple2<String, Double> map(SensorReading value) throws Exception {
      return new Tuple2<>(value.getId(), value.getTemperature()); 
    } 
  }) 
  .keyBy(data -> data.f0) 
  .timeWindow( Time.seconds(15) ) 
  .minBy(1);
```

 时间间隔可以通过`Time.milliseconds(x)`，`Time.seconds(x)`，`Time.minutes(x)`等其中的一个来指定。

#### 滑动窗口

 滑动窗口和滚动窗口的函数名是完全一致的，只是在传参数时需要传入两个参数，一个是window_size，一个是sliding_size。

 下面代码中的sliding_size设置为了5s，也就是说，每5s就计算输出结果一次，每一次计算的window范围是15s内的所有元素。

```java
DataStream<SensorReading> minTempPerWindowStream = dataStream 
  .keyBy(SensorReading::getId) 
  .timeWindow( Time.seconds(15), Time.seconds(5) ) 
  .minBy("temperature");
```

 时间间隔可以通过`Time.milliseconds(x)`，`Time.seconds(x)`，`Time.minutes(x)`等其中的一个来指定。

### 6.2.3 CountWindow

 CountWindow根据窗口中相同key元素的数量来触发执行，执行时只计算元素数量达到窗口大小的key对应的结果。

 **注意：CountWindow的window_size指的是相同Key的元素的个数，不是输入的所有元素的总数。**

#### 滚动窗口

 默认的CountWindow是一个滚动窗口，只需要指定窗口大小即可，**当元素数量达到窗口大小时，就会触发窗口的执行**。

```java
DataStream<SensorReading> minTempPerWindowStream = dataStream 
  .keyBy(SensorReading::getId) 
  .countWindow( 5 ) 
  .minBy("temperature");
```

#### 滑动窗口

 滑动窗口和滚动窗口的函数名是完全一致的，只是在传参数时需要传入两个参数，一个是window_size，一个是sliding_size。

 下面代码中的sliding_size设置为了2，也就是说，每收到两个相同key的数据就计算一次，每一次计算的window范围是10个元素。

```java
DataStream<SensorReading> minTempPerWindowStream = dataStream 
  .keyBy(SensorReading::getId) 
  .countWindow( 10, 2 ) 
  .minBy("temperature");
```

### 6.2.4 window function

window function 定义了要对窗口中收集的数据做的计算操作，主要可以分为两类：

- 增量聚合函数（incremental aggregation functions）
- 全窗口函数（full window functions）

#### 增量聚合函数

- **每条数据到来就进行计算**，保持一个简单的状态。（来一条处理一条，但是不输出，到窗口临界位置才输出）
- 典型的增量聚合函数有ReduceFunction, AggregateFunction。

#### 全窗口函数

- **先把窗口所有数据收集起来，等到计算的时候会遍历所有数据**。（来一个放一个，窗口临界位置才遍历且计算、输出）
- ProcessWindowFunction，WindowFunction。

### 6.2.5 其它可选API

- `.trigger()` ——触发器

  定义window 什么时候关闭，触发计算并输出结果

- `.evitor()` ——移除器

  定义移除某些数据的逻辑

- `.allowedLateness()` ——允许处理迟到的数据

- `.sideOutputLateData()` ——将迟到的数据放入侧输出流

- `.getSideOutput()` ——获取侧输出流

![image-20220222083339647](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222083339647.png)

### 6.2.6 代码测试

1. 测试滚动时间窗口的**增量聚合函数**

   增量聚合函数，特点即每次数据过来都处理，但是**到了窗口临界才输出结果**。

   - 编写java代码

     ```java
     package apitest.window;
     
     import apitest.beans.SensorReading;
     import org.apache.flink.api.common.functions.AggregateFunction;
     import org.apache.flink.streaming.api.datastream.DataStream;
     import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
     import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
     import org.apache.flink.streaming.api.windowing.time.Time;
     
     
     public class WindowTest1_TimeWindow {
       public static void main(String[] args) throws Exception {
     
         // 创建执行环境
         StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
     
         // 并行度设置1，方便看结果
         env.setParallelism(1);
     
         //        // 从文件读取数据
         //        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");
     
         // 从socket文本流获取数据
         DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
     
         // 转换成SensorReading类型
         DataStream<SensorReading> dataStream = inputStream.map(line -> {
           String[] fields = line.split(",");
           return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
         });
     
         // 开窗测试
     
         // 1. 增量聚合函数 (这里简单统计每个key组里传感器信息的总数)
         DataStream<Integer> resultStream = dataStream.keyBy("id")
           //                .countWindow(10, 2);
           //                .window(EventTimeSessionWindows.withGap(Time.minutes(1)));
           //                .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
           //                .timeWindow(Time.seconds(15)) // 已经不建议使用@Deprecated
           .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
           .aggregate(new AggregateFunction<SensorReading, Integer, Integer>() {
     
             // 新建的累加器
             @Override
             public Integer createAccumulator() {
               return 0;
             }
     
             // 每个数据在上次的基础上累加
             @Override
             public Integer add(SensorReading value, Integer accumulator) {
               return accumulator + 1;
             }
     
             // 返回结果值
             @Override
             public Integer getResult(Integer accumulator) {
               return accumulator;
             }
     
             // 分区合并结果(TimeWindow一般用不到，SessionWindow可能需要考虑合并)
             @Override
             public Integer merge(Integer a, Integer b) {
               return a + b;
             }
           });
     
         resultStream.print("result");
     
         env.execute();
       }
     }
     ```

   - 本地开启socket服务

     ```shell
     nc -lk 7777
     ```

   - 启动Flink程序，在socket窗口输入数据

     - 输入(下面用“换行”区分每个15s内的输入，实际输入时无换行)

       ```none
       sensor_1,1547718199,35.8
       sensor_6,1547718201,15.4
       
       sensor_7,1547718202,6.7
       sensor_10,1547718205,38.1
       sensor_1,1547718207,36.3
       sensor_1,1547718209,32.8
       
       sensor_1,1547718212,37.1
       ```

     - 输出（下面用“换行”区分每个15s内的输出，实际输出无换行）

       *因为代码实现每15s一个window，所以"sensor_1"中间一组才累计2，最初一次不累计，最后一次也是另外的window，重新从1计数。*

       ```none
       result> 1
       result> 1
       
       result> 1
       result> 1
       result> 2
       
       result> 1
       ```

2. 测试滚动时间窗口的**全窗口函数**

   全窗口函数，特点即数据过来先不处理，等到窗口临界再遍历、计算、输出结果。

   - 编写java测试代码

     ```java
     package apitest.window;
     
     import apitest.beans.SensorReading;
     import org.apache.commons.collections.IteratorUtils;
     import org.apache.flink.api.common.functions.AggregateFunction;
     import org.apache.flink.api.java.tuple.Tuple;
     import org.apache.flink.api.java.tuple.Tuple3;
     import org.apache.flink.streaming.api.datastream.DataStream;
     import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
     import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
     import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
     import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
     import org.apache.flink.streaming.api.windowing.time.Time;
     import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
     import org.apache.flink.util.Collector;
     
     
     public class WindowTest1_TimeWindow {
         public static void main(String[] args) throws Exception {
     
             // 创建执行环境
             StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
     
             // 并行度设置1，方便看结果
             env.setParallelism(1);
     
     //        // 从文件读取数据
     //        DataStream<String> dataStream = env.readTextFile("/tmp/Flink_Tutorial/src/main/resources/sensor.txt");
     
             // 从socket文本流获取数据
             DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
     
             // 转换成SensorReading类型
             DataStream<SensorReading> dataStream = inputStream.map(line -> {
                 String[] fields = line.split(",");
                 return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
             });
     
             // 2. 全窗口函数 （WindowFunction和ProcessWindowFunction，后者更全面）
             SingleOutputStreamOperator<Tuple3<String, Long, Integer>> resultStream2 = dataStream.keyBy(SensorReading::getId)
                     .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
     //                .process(new ProcessWindowFunction<SensorReading, Object, Tuple, TimeWindow>() {
     //                })
                     .apply(new WindowFunction<SensorReading, Tuple3<String, Long, Integer>, String, TimeWindow>() {
                         @Override
                         public void apply(String s, TimeWindow window, Iterable<SensorReading> input, Collector<Tuple3<String, Long, Integer>> out) throws Exception {
                             String id = s;
                             long windowEnd = window.getEnd();
                             int count = IteratorUtils.toList(input.iterator()).size();
                             out.collect(new Tuple3<>(id, windowEnd, count));
                         }
                     });
     
             resultStream2.print("result2");
     
             env.execute();
         }
     }
     ```

   - 启动本地socket

     ```shell
     nc -lk 7777
     ```

   - 在本地socket输入，查看Flink输出结果

     - 输入（以“空行”表示每个15s时间窗口内的输入，实际没有“空行”）

       ```none
       sensor_1,1547718199,35.8
       sensor_6,1547718201,15.4
       
       sensor_7,1547718202,6.7
       sensor_10,1547718205,38.1
       sensor_1,1547718207,36.3
       sensor_1,1547718209,32.8
       ```

     - 输出（以“空行”表示每个15s时间窗口内的输入，实际没有“空行”）

       *这里每个window都是分开计算的，所以第一个window里的sensor_1和第二个window里的sensor_1并没有累计。*

       ```none
       result2> (sensor_1,1612190820000,1)
       result2> (sensor_6,1612190820000,1)
       
       result2> (sensor_7,1612190835000,1)
       result2> (sensor_1,1612190835000,2)
       result2> (sensor_10,1612190835000,1)
       ```

3. 测试滑动计数窗口的**增量聚合函数**

   滑动窗口，当窗口不足设置的大小时，会先按照步长输出。

   eg：窗口大小10，步长2，那么前5次输出时，窗口内的元素个数分别是（2，4，6，8，10），再往后就是10个为一个窗口了。

   - 编写java代码：

     这里获取每个窗口里的温度平均值

     ```java
     package apitest.window;
     
     import apitest.beans.SensorReading;
     import org.apache.flink.api.common.functions.AggregateFunction;
     import org.apache.flink.api.java.tuple.Tuple2;
     import org.apache.flink.streaming.api.datastream.DataStream;
     import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
     
     
     public class WindowTest2_CountWindow {
       public static void main(String[] args) throws Exception {
     
         // 创建执行环境
         StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
     
         // 并行度设置1，方便看结果
         env.setParallelism(1);
     
         // 从socket文本流获取数据
         DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
     
         // 转换成SensorReading类型
         DataStream<SensorReading> dataStream = inputStream.map(line -> {
           String[] fields = line.split(",");
           return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
         });
     
         DataStream<Double> resultStream = dataStream.keyBy(SensorReading::getId)
           .countWindow(10, 2)
           .aggregate(new MyAvgFunc());
     
         resultStream.print("result");
     
         env.execute();
       }
     
       public static class MyAvgFunc implements AggregateFunction<SensorReading, Tuple2<Double, Integer>, Double> {
     
         @Override
         public Tuple2<Double, Integer> createAccumulator() {
           return new Tuple2<>(0.0, 0);
         }
     
         @Override
         public Tuple2<Double, Integer> add(SensorReading value, Tuple2<Double, Integer> accumulator) {
           // 温度累加求和，当前统计的温度个数+1
           return new Tuple2<>(accumulator.f0 + value.getTemperature(), accumulator.f1 + 1);
         }
     
         @Override
         public Double getResult(Tuple2<Double, Integer> accumulator) {
           return accumulator.f0 / accumulator.f1;
         }
     
         @Override
         public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> a, Tuple2<Double, Integer> b) {
           return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
         }
       }
     }
     ```

   - 启动socket服务

     ```shell
     nc -lk 7777
     ```

   - 本地socket输入，Flink控制台查看输出结果

     - 输入

       这里为了方便，就只输入同一个keyBy组的数据`sensor_1`

       ```none
       sensor_1,1547718199,1
       sensor_1,1547718199,2
       sensor_1,1547718199,3
       sensor_1,1547718199,4
       sensor_1,1547718199,5
       sensor_1,1547718199,6
       sensor_1,1547718199,7
       sensor_1,1547718199,8
       sensor_1,1547718199,9
       sensor_1,1547718199,10
       sensor_1,1547718199,11
       sensor_1,1547718199,12
       sensor_1,1547718199,13
       sensor_1,1547718199,14
       ```

     - 输出

       输入时，会发现，每次到达一个窗口步长（这里为2），就会计算得出一次结果。

       第一次计算前2个数的平均值

       第二次计算前4个数的平均值

       第三次计算前6个数的平均值

       第四次计算前8个数的平均值

       第五次计算前10个数的平均值

       **第六次计算前最近10个数的平均值**

       **第七次计算前最近10个数的平均值**

       ```none
       result> 1.5
       result> 2.5
       result> 3.5
       result> 4.5
       result> 5.5
       result> 7.5
       result> 9.5
       ```

4. 其他可选API代码片段

   ```java
   // 3. 其他可选API
   OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late") {
   };
   
   SingleOutputStreamOperator<SensorReading> sumStream = dataStream.keyBy("id")
     .timeWindow(Time.seconds(15))
     //                .trigger() // 触发器，一般不使用 
     //                .evictor() // 移除器，一般不使用
     .allowedLateness(Time.minutes(1)) // 允许1分钟内的迟到数据<=比如数据产生时间在窗口范围内，但是要处理的时候已经超过窗口时间了
     .sideOutputLateData(outputTag) // 侧输出流，迟到超过1分钟的数据，收集于此
     .sum("temperature"); // 侧输出流 对 温度信息 求和。
   
   // 之后可以再用别的程序，把侧输出流的信息和前面窗口的信息聚合。（可以把侧输出流理解为用来批处理来补救处理超时数据）
   ```

# 7. 时间语义和Watermark

## 7.1 Flink中的时间语义

![image-20220222093120434](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222093120434.png)



- **Event Time：事件创建时间；**
- Ingestion Time：数据进入Flink的时间；
- Processing Time：执行操作算子的本地系统时间，与机器相关；

 *Event Time是事件创建的时间。它通常由事件中的时间戳描述，例如采集的日志数据中，每一条日志都会记录自己的生成时间，Flink通过时间戳分配器访问事件时间戳。*

![image-20220222093157700](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222093157700.png)

- 不同的时间语义有不同的应用场合
- **我们往往更关心事件事件（Event Time）**

![image-20220222093217643](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222093217643.png)

这里假设玩游戏，两分钟内如果过5关就有奖励。用户坐地铁玩游戏，进入隧道前已经过3关，在隧道中又过了2关。但是信号不好，后两关通关的信息，等到出隧道的时候（8:23:20）才正式到达服务器。

 如果为了用户体验，那么应该按照Event Time处理信息，保证用户获得游戏奖励。

- Event Time可以从日志数据的时间戳（timestamp）中提取

  ```shell
  2017-11-02 18:27:15.624 INFO Fail over to rm
  ```

## 7.2 EventTime的引入

 **在Flink的流式处理中，绝大部分的业务都会使用eventTime**，一般只在eventTime无法使用时，才会被迫使用ProcessingTime或者IngestionTime。

 *（虽然默认环境里使用的就是ProcessingTime，使用EventTime需要另外设置）*

 如果要使用EventTime，那么需要引入EventTime的时间属性，引入方式如下所示：

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
// 从调用时刻开始给env创建的每一个stream追加时间特征
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
```

**注：具体的时间，还需要从数据中提取时间戳。**

## 7.3 Watermark

> [Flink流计算编程--watermark（水位线）简介](https://blog.csdn.net/lmalds/article/details/52704170) 

### 7.3.1 概念

- ，先来后到的保障顺序是：

  - WaterMark => 约等于放宽窗口标准
- allowedLateness => 允许迟到（ProcessingTime超时，但是EventTime没超时）
  - sideOutputLateData => 超过迟到时间，另外捕获，之后可以自己批处理合并先前的数据

 我们知道，流处理从事件产生，到流经source，再到operator，中间是有一个过程和时间的，虽然大部分情况下，流到operator的数据都是按照事件产生的时间顺序来的，但是也不排除由于网络、分布式等原因，导致乱序的产生，所谓乱序，就是指Flink接收到的事件的先后顺序不是严格按照事件的Event Time顺序排列的。

![image-20220222191420700](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191420700.png)

那么此时出现一个问题，一旦出现乱序，如果只根据eventTime决定window的运行，我们不能明确数据是否全部到位，但又不能无限期的等下去，此时必须要有个机制来保证一个特定的时间后，必须触发window去进行计算了，这个特别的机制，就是Watermark。

![image-20220222191501573](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191501573.png)



- 当Flink以**Event Time模式**处理数据流时，它会根据**数据里的时间戳**来处理基于时间的算子。

  （比如5s一个窗口，那么理想情况下，遇到时间戳是5s的数据时，就认为[0,5s)时间段的桶bucket就可以关闭了。）

- 实际由于网络、分布式传输处理等原因，会导致乱序数据的产生

- 乱序数据会导致窗口计算不准确

  （如果按照前面说法，获取到5s时间戳的数据，但是2s，3s乱序数据还没到，理论上不应该关闭桶）

------

- 怎样避免乱序数据带来的计算不正确？
- 遇到一个时间戳达到了窗口关闭时间，不应该立即触发窗口计算，而是等待一段时间，等迟到的数据来了再关闭窗口

1. Watermark是一种衡量Event Time进展的机制，可以设定延迟触发

2. Watermark是用于处理乱序事件的，而正确的处理乱序事件，通常用Watermark机制结合window来实现

3. 数据流中的Watermark用于表示”timestamp小于Watermark的数据，都已经到达了“，因此，window的执行也是由Watermark触发的。

4. Watermark可以理解成一个延迟触发机制，我们可以设置Watermark的延时时长t，每次系统会校验已经到达的数据中最大的maxEventTime，然后认定eventTime小于maxEventTime - t的所有数据都已经到达，**如果有窗口的停止时间等于maxEventTime – t，那么这个窗口被触发执行。**

   `Watermark = maxEventTime-延迟时间t`

5. watermark 用来让程序自己平衡延迟和结果正确性

*watermark可以理解为把原本的窗口标准稍微放宽了一点。（比如原本5s，设置延迟时间=2s，那么实际等到7s的数据到达时，才认为是[0,5）的桶需要关闭了）*

有序流的Watermarker如下图所示：（延迟时间设置为0s）

*此时以5s一个窗口，那么EventTime=5s的元素到达时，关闭第一个窗口，下图即W(5)，W(10)同理。*

![image-20220222191522526](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191522526.png)

乱序流的Watermarker如下图所示：（延迟时间设置为2s）

*乱序流，所以可能出现EventTime前后顺序不一致的情况，这里延迟时间设置2s，第一个窗口则为`5s+2s`，当EventTime=7s的数据到达时，关闭第一个窗口。第二个窗口则是`5\*2+2=12s`，当12s这个EventTime的数据到达时，关闭第二个窗口。*

![image-20220222191539801](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191539801.png)

 当Flink接收到数据时，会按照一定的规则去生成Watermark，这条Watermark就等于当前所有到达数据中的maxEventTime-延迟时长，也就是说，**Watermark是基于数据携带的时间戳生成的**，一旦Watermark比当前未触发的窗口的停止时间要晚，那么就会触发相应窗口的执行。

 **由于event time是由数据携带的，因此，如果运行过程中无法获取新的数据，那么没有被触发的窗口将永远都不被触发**。

 上图中，我们设置的允许最大延迟到达时间为2s，所以时间戳为7s的事件对应的Watermark是5s，时间戳为12s的事件的Watermark是10s，如果我们的窗口1是`1s~5s`，窗口2是`6s~10s`，那么时间戳为7s的事件到达时的Watermarker恰好触发窗口1，时间戳为12s的事件到达时的Watermark恰好触发窗口2。

 **Watermark 就是触发前一窗口的“关窗时间”，一旦触发关门那么以当前时刻为准在窗口范围内的所有所有数据都会收入窗中。**

 **只要没有达到水位那么不管现实中的时间推进了多久都不会触发关窗。**

### 7.3.2 Watermark的特点

![image-20220222191614886](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191614886.png)

- watermark 是一条特殊的数据记录
- **watermark 必须单调递增**，以确保任务的事件时间时钟在向前推进，而不是在后退
- watermark 与数据的时间戳相关

### 7.3.3 Watermark的传递

![image-20220222191635427](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220222191635427.png)

1. 图一，当前Task有四个上游Task给自己传输WaterMark信息，通过比较，只取当前最小值作为自己的本地Event-time clock，上图中，当前Task[0,2)的桶就可关闭了，因为所有上游中2s最小，能保证2s的WaterMark是准确的（所有上游Watermark都已经>=2s)。这时候将Watermark=2广播到当前Task的下游。
2. 图二，上游的Watermark持续变动，此时Watermark=3成为新的最小值，更新本地Task的event-time clock，同时将最新的Watermark=3广播到下游
3. 图三，上游的Watermark虽然更新了，但是当前最小值还是3，所以不更新event-time clock，也不需要广播到下游
4. 图四，和图二同理，更新本地event-time clock，同时向下游广播最新的Watermark=4

### 7.3.4 Watermark的引入

 watermark的引入很简单，对于乱序数据，最常见的引用方式如下：

```scala
dataStream.assignTimestampsAndWatermarks( new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.milliseconds(1000)) {
  @Override
  public long extractTimestamp(element: SensorReading): Long = { 
    return element.getTimestamp() * 1000L;
  } 
});
```

 **Event Time的使用一定要指定数据源中的时间戳。否则程序无法知道事件的事件时间是什么(数据源里的数据没有时间戳的话，就只能使用Processing Time了)**。

 我们看到上面的例子中创建了一个看起来有点复杂的类，这个类实现的其实就是分配时间戳的接口。Flink暴露了TimestampAssigner接口供我们实现，使我们可以自定义如何从事件数据中抽取时间戳。

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
// 设置事件时间语义 env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
DataStream<SensorReading> dataStream = env.addSource(new SensorSource()) .assignTimestampsAndWatermarks(new MyAssigner());
```

MyAssigner有两种类型

- AssignerWithPeriodicWatermarks
- AssignerWithPunctuatedWatermarks

以上两个接口都继承自TimestampAssigner。

#### TimestampAssigner

##### AssignerWithPeriodicWatermarks

- 周期性的生成 watermark：系统会周期性的将 watermark 插入到流中
- 默认周期是200毫秒，可以使用 `ExecutionConfig.setAutoWatermarkInterval()` 方法进行设置
- **升序和前面乱序的处理 BoundedOutOfOrderness ，都是基于周期性 watermark 的**。

##### AssignerWithPunctuatedWatermarks

- 没有时间周期规律，可打断的生成 watermark（即可实现每次获取数据都更新watermark）

### 7.3.5 Watermark的设定

- 在Flink中，Watermark由应用程序开发人员生成，这通常需要对相应的领域有一定的了解
- 如果Watermark设置的延迟太久，收到结果的速度可能就会很慢，解决办法是在水位线到达之前输出一个近似结果
- 如果Watermark到达得太早，则可能收到错误结果，不过Flink处理迟到数据的机制可以解决这个问题

 *一般大数据场景都是考虑高并发情况，所以一般使用周期性生成Watermark的方式，避免频繁地生成Watermark。*

------

**注：一般认为Watermark的设置代码，在里Source步骤越近的地方越合适。**

### 7.3.6 测试代码

测试Watermark和迟到数据

java代码（旧版Flink），新版的代码我暂时不打算折腾，之后用上再说吧。

**这里设置的Watermark的延时时间是2s，实际一般设置和window大小一致。**

```java
public class WindowTest3_EventTimeWindow {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // Flink1.12.X 已经默认就是使用EventTime了，所以不需要这行代码
    //        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    env.getConfig().setAutoWatermarkInterval(100);

    // socket文本流
    DataStream<String> inputStream = env.socketTextStream("localhost", 7777);

    // 转换成SensorReading类型，分配时间戳和watermark
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    })
      //              
      //                // 旧版 (新版官方推荐用assignTimestampsAndWatermarks(WatermarkStrategy) )
      // 升序数据设置事件时间和watermark
      //.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
      //  @Override
      //  public long extractAscendingTimestamp(SensorReading element) {
      //    return element.getTimestamp() * 1000L;
      //  }
      //})
      
      // 旧版 (新版官方推荐用assignTimestampsAndWatermarks(WatermarkStrategy) )
      // 乱序数据设置时间戳和watermark
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
        @Override
        public long extractTimestamp(SensorReading element) {
          return element.getTimestamp() * 1000L;
        }
      });

    OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late") {
    };

    // 基于事件时间的开窗聚合，统计15秒内温度的最小值
    SingleOutputStreamOperator<SensorReading> minTempStream = dataStream.keyBy("id")
      .timeWindow(Time.seconds(15))
      .allowedLateness(Time.minutes(1))
      .sideOutputLateData(outputTag)
      .minBy("temperature");

    minTempStream.print("minTemp");
    minTempStream.getSideOutput(outputTag).print("late");

    env.execute();
  }
}
```

#### 并行任务Watermark传递测试

在前面代码的基础上，修改执行环境并行度为4，进行测试

```java
public class WindowTest3_EventTimeWindow {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.setParallelism(4);

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    env.getConfig().setAutoWatermarkInterval(100);

    // socket文本流
    DataStream<String> inputStream = env.socketTextStream("localhost", 7777);

    // 转换成SensorReading类型，分配时间戳和watermark
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    })
      
      // 乱序数据设置时间戳和watermark
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
        @Override
        public long extractTimestamp(SensorReading element) {
          return element.getTimestamp() * 1000L;
        }
      });

    OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late") {
    };

    // 基于事件时间的开窗聚合，统计15秒内温度的最小值
    SingleOutputStreamOperator<SensorReading> minTempStream = dataStream.keyBy("id")
      .timeWindow(Time.seconds(15))
      .allowedLateness(Time.minutes(1))
      .sideOutputLateData(outputTag)
      .minBy("temperature");

    minTempStream.print("minTemp");
    minTempStream.getSideOutput(outputTag).print("late");

    env.execute();
  }
}
```

启动本地socket，输入数据，查看结果

```shell
nc -lk 7777
```

输入：

```shell
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718211,34
sensor_1,1547718212,31.9
sensor_1,1547718212,31.9
sensor_1,1547718212,31.9
sensor_1,1547718212,31.9
```

输出

*注意：上面输入全部输入后，才突然有下面4条输出！*

```shell
minTemp:2> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
minTemp:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
minTemp:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
minTemp:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
```

##### [分析](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=分析)

1. **计算窗口起始位置Start和结束位置End**

   从`TumblingProcessingTimeWindows`类里的`assignWindows`方法，我们可以得知窗口的起点计算方法如下： $$ 窗口起点start = timestamp - (timestamp -offset+WindowSize) % WindowSize $$ 由于我们没有设置offset，所以这里`start=第一个数据的时间戳1547718199-(1547718199-0+15)%15=1547718195`

   计算得到窗口初始位置为`Start = 1547718195`，那么这个窗口理论上本应该在1547718195+15的位置关闭，也就是`End=1547718210`

   ```java
   @Override
   public Collection<TimeWindow> assignWindows(
     Object element, long timestamp, WindowAssignerContext context) {
     final long now = context.getCurrentProcessingTime();
     if (staggerOffset == null) {
       staggerOffset =
         windowStagger.getStaggerOffset(context.getCurrentProcessingTime(), size);
     }
     long start =
       TimeWindow.getWindowStartWithOffset(
       now, (globalOffset + staggerOffset) % size, size);
     return Collections.singletonList(new TimeWindow(start, start + size));
   }
   
   // 跟踪 getWindowStartWithOffset 方法得到TimeWindow的方法
   public static long getWindowStartWithOffset(long timestamp, long offset, long windowSize) {
     return timestamp - (timestamp - offset + windowSize) % windowSize;
   }
   ```

2. **计算修正后的Window输出结果的时间**

   测试代码中Watermark设置的`maxOutOfOrderness`最大乱序程度是2s，所以实际获取到End+2s的时间戳数据时（达到Watermark），才认为Window需要输出计算的结果（不关闭，因为设置了允许迟到1min）

   **所以实际应该是1547718212的数据到来时才触发Window输出计算结果。**

   ```java
   .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
     @Override
     public long extractTimestamp(SensorReading element) {
       return element.getTimestamp() * 1000L;
     }
   });
   
   
   // BoundedOutOfOrdernessTimestampExtractor.java
   public BoundedOutOfOrdernessTimestampExtractor(Time maxOutOfOrderness) {
     if (maxOutOfOrderness.toMilliseconds() < 0) {
       throw new RuntimeException(
         "Tried to set the maximum allowed "
         + "lateness to "
         + maxOutOfOrderness
         + ". This parameter cannot be negative.");
     }
     this.maxOutOfOrderness = maxOutOfOrderness.toMilliseconds();
     this.currentMaxTimestamp = Long.MIN_VALUE + this.maxOutOfOrderness;
   }
   @Override
   public final Watermark getCurrentWatermark() {
     // this guarantees that the watermark never goes backwards.
     long potentialWM = currentMaxTimestamp - maxOutOfOrderness;
     if (potentialWM >= lastEmittedWatermark) {
       lastEmittedWatermark = potentialWM;
     }
     return new Watermark(lastEmittedWatermark);
   }
   ```

3. 为什么上面输入中，最后连续四条相同输入，才触发Window输出结果？

   - **Watermark会向子任务广播**
     - 我们在map才设置Watermark，map根据Rebalance轮询方式分配数据。所以前4个输入分别到4个slot中，4个slot计算得出的Watermark不同（分别是1547718199-2，1547718201-2，1547718202-2，1547718205-2）
   - **Watermark传递时，会选择当前接收到的最小一个作为自己的Watermark**
     - 前4次输入中，有些map子任务还没有接收到数据，所以其下游的keyBy后的slot里watermark就是`Long.MIN_VALUE`（因为4个上游的Watermark广播最小值就是默认的`Long.MIN_VALUE`）
     - 并行度4，在最后4个相同的输入，使得Rebalance到4个map子任务的数据的`currentMaxTimestamp`都是1547718212，经过`getCurrentWatermark()`的计算（`currentMaxTimestamp-maxOutOfOrderness`），4个子任务都计算得到watermark=1547718210，4个map子任务向4个keyBy子任务广播`watermark=1547718210`，使得keyBy子任务们获取到4个上游的Watermark最小值就是1547718210，然后4个KeyBy子任务都更新自己的Watermark为1547718210。
   - **根据Watermark的定义，我们认为>=Watermark的数据都已经到达。由于此时watermark >= 窗口End，所以Window输出计算结果（4个子任务，4个结果）。**

### 7.3.7 窗口起始点和偏移量

> [flink-Window Assingers(窗口分配器)中offset偏移量](https://juejin.cn/post/6844904110941011976)

 时间偏移一个很大的用处是用来调准非0时区的窗口，例如:在中国你需要指定一个8小时的时间偏移。





# 8. Flink状态管理

> [Flink_Flink中的状态](https://blog.csdn.net/dongkang123456/article/details/108430338)
>
> [Flink状态管理详解：Keyed State和Operator List State深度解析](https://zhuanlan.zhihu.com/p/104171679) <= 不错的文章，建议阅读

- 算子状态（Operator State）
- 键控状态（Keyed State）
- 状态后端（State Backends）

## 8.1 状态概述

![image-20220223084612137](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223084612137.png)

- 由一个任务维护，并且用来计算某个结果的所有数据，都属于这个任务的状态
- 可以认为任务状态就是一个本地变量，可以被任务的业务逻辑访问
- **Flink 会进行状态管理，包括状态一致性、故障处理以及高效存储和访问，以便于开发人员可以专注于应用程序的逻辑**

------

- **在Flink中，状态始终与特定算子相关联**
- 为了使运行时的Flink了解算子的状态，算子需要预先注册其状态

**总的来说，有两种类型的状态：**

- 算子状态（Operator State）
  - 算子状态的作用范围限定为**算子任务**（也就是不能跨任务访问）
- 键控状态（Keyed State）
  - 根据输入数据流中定义的键（key）来维护和访问

## 8.2 算子状态 Operator State

![image-20220223084733164](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223084733164.png)



- 算子状态的作用范围限定为算子任务，同一并行任务所处理的所有数据都可以访问到相同的状态。
- 状态对于**同一任务**而言是共享的。（**不能跨slot**）
- 状态算子不能由相同或不同算子的另一个任务访问。

### 算子状态数据结构

- 列表状态(List state)
  - 将状态表示为一组数据的列表
- 联合列表状态(Union list state)
  - 也将状态表示未数据的列表。它与常规列表状态的区别在于，在发生故障时，或者从保存点(savepoint)启动应用程序时如何恢复
- 广播状态(Broadcast state)
  - 如果一个算子有多项任务，而它的每项任务状态又都相同，那么这种特殊情况最适合应用广播状态

### 测试代码

实际一般用算子状态比较少，一般还是键控状态用得多一点。

```java
package apitest.state;

import apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Collections;
import java.util.List;

/**
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2021/2/2 4:05 AM
 */
public class StateTest1_OperatorState {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // socket文本流
    DataStream<String> inputStream = env.socketTextStream("localhost", 7777);

    // 转换成SensorReading类型
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    });

    // 定义一个有状态的map操作，统计当前分区数据个数
    SingleOutputStreamOperator<Integer> resultStream = dataStream.map(new MyCountMapper());

    resultStream.print();

    env.execute();
  }

  // 自定义MapFunction
  public static class MyCountMapper implements MapFunction<SensorReading, Integer>, ListCheckpointed<Integer> {
    // 定义一个本地变量，作为算子状态
    private Integer count = 0;

    @Override
    public Integer map(SensorReading value) throws Exception {
      count++;
      return count;
    }

    @Override
    public List<Integer> snapshotState(long checkpointId, long timestamp) throws Exception {
      return Collections.singletonList(count);
    }

    @Override
    public void restoreState(List<Integer> state) throws Exception {
      for (Integer num : state) {
        count += num;
      }
    }
  }
}
```

输入(本地开启socket后输入)

```shell
sensor_1,1547718199,35.8
sensor_1,1547718199,35.8
sensor_1,1547718199,35.8
sensor_1,1547718199,35.8
sensor_1,1547718199,35.8
```

输出

```shell
1
2
3
4
5
```

## 8.3 键控状态 Keyed State

![image-20220223084802069](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223084802069.png)

- 键控状态是根据输入数据流中定义的键（key）来维护和访问的。
- **Flink 为每个key维护一个状态实例，并将具有相同键的所有数据，都分区到同一个算子任务中，这个任务会维护和处理这个key对应的状态。**
- **当任务处理一条数据时，他会自动将状态的访问范围限定为当前数据的key**。

### 键控状态数据结构

- 值状态(value state)
  - 将状态表示为单个的值
- 列表状态(List state)
  - 将状态表示为一组数据的列表
- 映射状态(Map state)
  - 将状态表示为一组key-value对
- **聚合状态(Reducing state & Aggregating State)**
  - 将状态表示为一个用于聚合操作的列表

### 测试代码

![image-20220223084848091](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223084848091.png)



*注：声明一个键控状态，一般在算子的open()中声明，因为运行时才能获取上下文信息*

- java测试代码

  ```java
  package apitest.state;
  
  import apitest.beans.SensorReading;
  import org.apache.flink.api.common.functions.RichMapFunction;
  import org.apache.flink.api.common.state.*;
  import org.apache.flink.configuration.Configuration;
  import org.apache.flink.streaming.api.datastream.DataStream;
  import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
  
  
  public class StateTest2_KeyedState {
  
    public static void main(String[] args) throws Exception {
      // 创建执行环境
      StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
      // 设置并行度 = 1
      env.setParallelism(1);
      // 从本地socket读取数据
      DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
  
      // 转换成SensorReading类型
      DataStream<SensorReading> dataStream = inputStream.map(line -> {
        String[] fields = line.split(",");
        return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
      });
  
      // 使用自定义map方法，里面使用 我们自定义的Keyed State
      DataStream<Integer> resultStream = dataStream
        .keyBy(SensorReading::getId)
        .map(new MyMapper());
  
      resultStream.print("result");
      env.execute();
    }
  
    // 自定义map富函数，测试 键控状态
    public static class MyMapper extends RichMapFunction<SensorReading,Integer>{
  
      //        Exception in thread "main" java.lang.IllegalStateException: The runtime context has not been initialized.
      //        ValueState<Integer> valueState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("my-int", Integer.class));
  
      private ValueState<Integer> valueState;
  
  
      // 其它类型状态的声明
      private ListState<String> myListState;
      private MapState<String, Double> myMapState;
      private ReducingState<SensorReading> myReducingState;
  
      @Override
      public void open(Configuration parameters) throws Exception {
        valueState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("my-int", Integer.class));
  
        myListState = getRuntimeContext().getListState(new ListStateDescriptor<String>("my-list", String.class));
        myMapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Double>("my-map", String.class, Double.class));
        //            myReducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<SensorReading>())
  
      }
  
      // 这里就简单的统计每个 传感器的 信息数量
      @Override
      public Integer map(SensorReading value) throws Exception {
        // 其它状态API调用
        // list state
        for(String str: myListState.get()){
          System.out.println(str);
        }
        myListState.add("hello");
        // map state
        myMapState.get("1");
        myMapState.put("2", 12.3);
        myMapState.remove("2");
        // reducing state
        //            myReducingState.add(value);
  
        myMapState.clear();
  
  
        Integer count = valueState.value();
        // 第一次获取是null，需要判断
        count = count==null?0:count;
        ++count;
        valueState.update(count);
        return count;
      }
    }
  }
  ```

### 场景测试

假设做一个温度报警，如果一个传感器前后温差超过10度就报警。这里使用键控状态Keyed State + flatMap来实现

- java代码

  ```java
  package apitest.state;
  
  import apitest.beans.SensorReading;
  import org.apache.flink.api.common.functions.RichFlatMapFunction;
  import org.apache.flink.api.common.state.ValueState;
  import org.apache.flink.api.common.state.ValueStateDescriptor;
  import org.apache.flink.api.java.tuple.Tuple3;
  import org.apache.flink.configuration.Configuration;
  import org.apache.flink.streaming.api.datastream.DataStream;
  import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
  import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
  import org.apache.flink.util.Collector;
  
  
  public class StateTest3_KeyedStateApplicationCase {
  
    public static void main(String[] args) throws Exception {
      // 创建执行环境
      StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
      // 设置并行度 = 1
      env.setParallelism(1);
      // 从socket获取数据
      DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
      // 转换为SensorReading类型
      DataStream<SensorReading> dataStream = inputStream.map(line -> {
        String[] fields = line.split(",");
        return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
      });
  
      SingleOutputStreamOperator<Tuple3<String, Double, Double>> resultStream = dataStream.keyBy(SensorReading::getId).flatMap(new MyFlatMapper(10.0));
  
      resultStream.print();
  
      env.execute();
    }
  
    // 如果 传感器温度 前后差距超过指定温度(这里指定10.0),就报警
    public static class MyFlatMapper extends RichFlatMapFunction<SensorReading, Tuple3<String, Double, Double>> {
  
      // 报警的温差阈值
      private final Double threshold;
  
      // 记录上一次的温度
      ValueState<Double> lastTemperature;
  
      public MyFlatMapper(Double threshold) {
        this.threshold = threshold;
      }
  
      @Override
      public void open(Configuration parameters) throws Exception {
        // 从运行时上下文中获取keyedState
        lastTemperature = getRuntimeContext().getState(new ValueStateDescriptor<Double>("last-temp", Double.class));
      }
  
      @Override
      public void close() throws Exception {
        // 手动释放资源
        lastTemperature.clear();
      }
  
      @Override
      public void flatMap(SensorReading value, Collector<Tuple3<String, Double, Double>> out) throws Exception {
        Double lastTemp = lastTemperature.value();
        Double curTemp = value.getTemperature();
  
        // 如果不为空，判断是否温差超过阈值，超过则报警
        if (lastTemp != null) {
          if (Math.abs(curTemp - lastTemp) >= threshold) {
            out.collect(new Tuple3<>(value.getId(), lastTemp, curTemp));
          }
        }
  
        // 更新保存的"上一次温度"
        lastTemperature.update(curTemp);
      }
    }
  }
  ```
  
- 启动socket

  ```shell
  nc -lk 7777
  ```

- 输入数据，查看结果

  - 输入

    ```shell
    sensor_1,1547718199,35.8
    sensor_1,1547718199,32.4
    sensor_1,1547718199,42.4
    sensor_10,1547718205,52.6   
    sensor_10,1547718205,22.5
    sensor_7,1547718202,6.7
    sensor_7,1547718202,9.9
    sensor_1,1547718207,36.3
    sensor_7,1547718202,19.9
    sensor_7,1547718202,30
    ```

  - 输出

    *中间没有输出（sensor_7,9.9,19.9)，应该是double浮点数计算精度问题，不管它*

    ```shell
    (sensor_1,32.4,42.4)
    (sensor_10,52.6,22.5)
    (sensor_7,19.9,30.0)
    ```

## 8.4 状态后端 State Backends

### 8.4.1 概述

- 每传入一条数据，有状态的算子任务都会读取和更新状态。
- 由于有效的状态访问对于处理数据的低延迟至关重要，因此每个并行任务都会在本地维护其状态，以确保快速的状态访问。
- 状态的存储、访问以及维护，由一个可插入的组件决定，这个组件就叫做**状态后端( state backend)**
- **状态后端主要负责两件事：本地状态管理，以及将检查点(checkPoint)状态写入远程存储**

### 8.4.2 选择一个状态后端

- MemoryStateBackend
  - 内存级的状态后端，会将键控状态作为内存中的对象进行管理，将它们存储在TaskManager的JVM堆上，而将checkpoint存储在JobManager的内存中
  - 特点：快速、低延迟，但不稳定
- FsStateBackend（默认）
  - 将checkpoint存到远程的持久化文件系统（FileSystem）上，而对于本地状态，跟MemoryStateBackend一样，也会存在TaskManager的JVM堆上
  - 同时拥有内存级的本地访问速度，和更好的容错保证
- RocksDBStateBackend
  - 将所有状态序列化后，存入本地的RocksDB中存储

### 8.4.3 配置文件

```
flink-conf.yaml
#==============================================================================
# Fault tolerance and checkpointing
#==============================================================================

# The backend that will be used to store operator state checkpoints if
# checkpointing is enabled.
#
# Supported backends are 'jobmanager', 'filesystem', 'rocksdb', or the
# <class-name-of-factory>.
#
# state.backend: filesystem
上面这个就是默认的checkpoint存在filesystem


# Directory for checkpoints filesystem, when using any of the default bundled
# state backends.
#
# state.checkpoints.dir: hdfs://namenode-host:port/flink-checkpoints

# Default target directory for savepoints, optional.
#
# state.savepoints.dir: hdfs://namenode-host:port/flink-savepoints

# Flag to enable/disable incremental checkpoints for backends that
# support incremental checkpoints (like the RocksDB state backend). 
#
# state.backend.incremental: false

# The failover strategy, i.e., how the job computation recovers from task failures.
# Only restart tasks that may have been affected by the task failure, which typically includes
# downstream tasks and potentially upstream tasks if their produced data is no longer available for consumption.

jobmanager.execution.failover-strategy: region

上面这个region指，多个并行度的任务要是有个挂掉了，只重启那个任务所属的region（可能含有多个子任务），而不需要重启整个Flink程序
```

### 8.4.4 样例代码

- 其中使用RocksDBStateBackend需要另外加入pom依赖

  ```xml
  <!-- RocksDBStateBackend -->
  <dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-statebackend-rocksdb_${scala.binary.version}</artifactId>
    <version>${flink.version}</version>
  </dependency>
  ```

- java代码

  ```java
  package apitest.state;
  
  import apitest.beans.SensorReading;
  import org.apache.flink.api.common.restartstrategy.RestartStrategies;
  import org.apache.flink.api.common.time.Time;
  import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
  import org.apache.flink.runtime.state.filesystem.FsStateBackend;
  import org.apache.flink.runtime.state.memory.MemoryStateBackend;
  import org.apache.flink.streaming.api.CheckpointingMode;
  import org.apache.flink.streaming.api.datastream.DataStream;
  import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
  
  
  public class StateTest4_FaultTolerance {
      public static void main(String[] args) throws Exception {
          StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
          env.setParallelism(1);
  
          // 1. 状态后端配置
          env.setStateBackend(new MemoryStateBackend());
          env.setStateBackend(new FsStateBackend("checkpointDataUri"));
          // 这个需要另外导入依赖
          env.setStateBackend(new RocksDBStateBackend("checkpointDataUri"));
  
          // socket文本流
          DataStream<String> inputStream = env.socketTextStream("localhost", 7777);
  
          // 转换成SensorReading类型
          DataStream<SensorReading> dataStream = inputStream.map(line -> {
              String[] fields = line.split(",");
              return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
          });
  
          dataStream.print();
          env.execute();
      }
  }
  ```













# 10. 容错机制

> [Flink-容错机制 | 一致性检查点 | 检查点到恢复状态过程 | Flink检查点算法(Chandy-Lamport) | 算法操作解析 | 保存点简介](https://blog.csdn.net/qq_40180229/article/details/106433621)

## 10.1 一致性检查点(checkpoint)

![image-20220223195456202](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223195456202.png)



- Flink 故障恢复机制的核心，就是应用状态的一致性检查点

- 有状态流应用的一致检查点，其实就是**所有任务的状态**，在某个时间点的一份拷贝（一份快照）；**这个时间点，应该是所有任务都恰好处理完一个相同的输入数据的时候**

  *(5这个数据虽然进了奇数流但是偶数流也应该做快照，因为属于同一个相同数据，只是没有被他处理)*

  *（这里根据奇偶性分流，偶数流求偶数和，奇数流求奇数和，5这里明显已经被sum_odd（1+3+5）处理了，且sum_even不需要处理该数据，因为前面已经判断该数据不需要到sum_even流，相当于所有任务都已经处理完source的数据5了。）*

- 在JobManager中也有个Chechpoint的指针，指向了仓库的状态快照的一个拓扑图，为以后的数据故障恢复做准备

## 10.2 从检查点恢复状态

![image-20220223195521258](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223195521258.png)



- 在执行流应用程序期间，Flink 会定期保存状态的一致检查点

- 如果发生故障， Flink 将会使用最近的检查点来一致恢复应用程序的状态，并重新启动处理流程

  （**如图中所示，7这个数据被source读到了，准备传给奇数流时，奇数流宕机了，数据传输发生中断**）

  ![image-20220223195539957](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223195539957.png)

  - 第二步是从 checkpoint 中读取状态，将状态重置

    *(**读取在远程仓库**(Storage，这里的仓库指状态后端保存数据指定的三种方式之一)**保存的状态**)*

  - 从检查点重新启动应用程序后，其内部状态与检查点完成时的状态完全相同

![image-20220223195649088](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\flink\尚硅谷Flink教程笔记.assets\image-20220223195649088.png)







- 第三步：开始消费并处理检查点到发生故障之间的所有数据

- **这种检查点的保存和恢复机制可以为应用程序状态提供“精确一次”（exactly-once）的一致性，因为所有算子都会保存检查点并恢复其所有状态，这样一来所有的输入流就都会被重置到检查点完成时的位置**

  *（这里要求source源也能记录状态，回退到读取数据7的状态，kafka有相应的偏移指针能完成该操作）*

## 10.3 Flink检查点算法

### 概述

**checkpoint和Watermark一样，都会以广播的形式告诉所有下游。**

------

- 一种简单的想法

  暂停应用，保存状态到检查点，再重新恢复应用（当然Flink 不是采用这种简单粗暴的方式）

- Flink的改进实现

  - 基于Chandy-Lamport算法的分布式快照
  - 将检查点的保存和数据处理分离开，不暂停整个应用

  （就是每个任务单独拍摄自己的快照到内存，之后再到jobManager整合）

------

- 检查点分界线（Checkpoint Barrier）
  - Flink的检查点算法用到了一种称为分界线（barrier）的特殊数据形式，用来把一条流上数据按照不同的检查点分开
  - **分界线之前到来的数据导致的状态更改，都会被包含在当前分界线所属的检查点中；而基于分界线之后的数据导致的所有更改，就会被包含在之后的检查点中**

### [具体讲解](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/尚硅谷Flink入门到实战-学习笔记?id=具体讲解)





# Reference

- [尚硅谷Flink入门到实战-学习笔记](https://ashiamd.github.io/docsify-notes/#/study/BigData/Flink/%E5%B0%9A%E7%A1%85%E8%B0%B7Flink%E5%85%A5%E9%97%A8%E5%88%B0%E5%AE%9E%E6%88%98-%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0)
- [尚硅谷Java版Flink](https://www.bilibili.com/video/BV1qy4y1q728?p=23)
- [Apache Flink](https://flink.apache.org/)