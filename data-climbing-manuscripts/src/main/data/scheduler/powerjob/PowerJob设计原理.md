

## Small-Bang推演











### 总体框架流程

- `server`负责任务的启调，`worker`负责任务的实际执行，支持**内建处理器/外部处理器/SHELL脚本/Python脚本**等方式执行，`server`与`worker`之间通过`akka`，通信，`worker`尽量目的单一，也就是执行，并上报结果(成功/失败)给`sever`，由`server`端来做数据库持久化(这个思想`DolphinScheduler`在最新开源的版本中移除了`worker`的数据库交互操作)

- 对工作流任务(即有前置依赖的任务),考虑采用`DAG`（有向无环图）的方式来安排整个任务的节点，主流的调度框架都有这种设计体现在里面(`PowerJob`，`DolphinScheduler`)，原`metasys`有`DAG`的关系图谱在里面，在UI操作界面上没有很好第体现，做任务编排的时候没有将关联的节点安排成一个`DAG`进行全盘考虑，新的如果采用`DAG`的方式设计，`UI`可以比较方便的展示，开发者在`UI`页面操作，可以宏观地知道这个工作流任务

### 工作流

-  情况1：

![image-20210513165156180](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210513165156180.png)





- 情况2:

  - $Node \: C$为关键节点

  ![image-20210513165253562](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210513165253562.png)



### 关于工作流任务

- 筛选出待处理的工作流任务，生成工作流,处在`WAITING`状态（等待调度），组装`DAG`信息，推入时间轮等待执行，生成工作流实例，读取工作流的`DAG`信息，筛选出符合条件的工作流任务中的节点`Node`,设置状态为`RUNNING`,并将工作流设置为`RUNNING`，持久化，执行这些符合条件的节点任务; 重新计算下一次调度时间并更新

#### 如何生成任务实例？

- 上方

#### 任务生成时，状态如何设置？

 工作流部分完成，部分运行，当前的工作流处于`RUNNING`，工作流中的所有节点都完成后，工作流才处于`FINISH`

#### 何时提交实例？

 `dispatchService.dispatch(jobInfo, node.getInstanceId())`

#### 实例完成后，如何拉起后续实例？

在DAG图中检查：需要满足以下条件才可以拉起工作流中的后续工作流实例

- 当前节点是非完成状态
- 前置依赖节点为空或者均处于完成状态
  - 带上步长、单位等参数判断工作流实例是否已经完成
  - 可考虑使用`jexl`表达式引擎来判断这些前置依赖条件是否满足

![image-20210517152249828](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210517152249828.png)



![image-20210517152308919](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210517152308919.png)







### 重跑

**理论上，重试与重跑效果是一样的。但是重试是程序自动的，而重跑是人工手动干预的。**

![image-20210517152152942](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210517152152942.png)



- 节点可以被强制失效，或者不是很重要的节点（一旦失败会影响后续节点的执行）,可以设置失败时跳过，在这个`Node`的类中，进行重跑逻辑设置

#### 单个节点的重跑

- 依赖性比较弱，容易实现

#### 批量重跑

- 

#### 某个节点后的所有节点

- 

#### 跳过成功实例，仅执行失败实例

- 筛选出失败实例，执行的时候选





## PowerJob设计原理

### 特性

#### 传统的调度原理

> 植根于Quartz

```sql
select * from job_info where next_trigger_time < now()+15s
```



![image-20210406183920486](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406183920486.png)

#### 无锁化调度

- 引入分组依据`AppName`,以应用集群作为server调度的单位
- 每一个worker集群在运行时只会链接某一台server
- 每一个server实例只会调度当前与自己保持心跳的worker关联的`AppName`下的所有任务

```sql
select * from job_info where app_name ="XXX" and  next_trigger_time < now()+15s
```

### 

![image-20210406184442754](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406184442754.png)



#### 分布式计算MapReduce

![image-20210406184538375](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406184538375.png)



![1593492662657-a6b073fa-e4e8-40d6-9c0a-1b60701d51ee](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1593492662657-a6b073fa-e4e8-40d6-9c0a-1b60701d51ee.png)<img src="D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1595900957124-ad051dbd-d09a-4645-b3a5-3c97328781a1.png" alt="1595900957124-ad051dbd-d09a-4645-b3a5-3c97328781a1" style="zoom:200%;" />

![1596210218087-c3d40463-b354-4ce2-b92b-c782032589c3](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1596210218087-c3d40463-b354-4ce2-b92b-c782032589c3.png)



#### Fragment

```
powerjob-worker : ProcessorRunnable
    - run() --> innerRun() : 
		构造上下文,上报执行信息,广播执行,最终任务特殊处理(BROADCAST/MAP_REDUCE)，提交执行
TaskTracker: initProcessor
	分配Processor(EMBEDDED_JAVA/PYTHON/SHELL) ：
selectTaskTracker：
     - 两种策略：HEALTH_FIRST / RANDOM
ProcessorTracker: destory():
	- 移除container，关闭执行的线程池，gc, 关闭定时线程池
	- WorkerActor: 监听  onReceiveServerDeployContainerRequest 
```









### 部署

[K8S 部署](https://github.com/PowerJob/PowerJob/issues/157)

