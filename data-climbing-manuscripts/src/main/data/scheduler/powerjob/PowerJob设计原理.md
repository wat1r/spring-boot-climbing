# PowerJob设计原理







## 特性

### 传统的调度原理

> 植根于Quartz

```sql
select * from job_info where next_trigger_time < now()+15s
```



![image-20210406183920486](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406183920486.png)

### 无锁化调度

- 引入分组依据`AppName`,以应用集群作为server调度的单位
- 每一个worker集群在运行时只会链接某一台server
- 每一个server实例只会调度当前与自己保持心跳的worker关联的`AppName`下的所有任务

```sql
select * from job_info where app_name ="XXX" and  next_trigger_time < now()+15s
```

### 

![image-20210406184442754](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406184442754.png)



### 分布式计算MapReduce

![image-20210406184538375](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210406184538375.png)



![1593492662657-a6b073fa-e4e8-40d6-9c0a-1b60701d51ee](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1593492662657-a6b073fa-e4e8-40d6-9c0a-1b60701d51ee.png)<img src="D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1595900957124-ad051dbd-d09a-4645-b3a5-3c97328781a1.png" alt="1595900957124-ad051dbd-d09a-4645-b3a5-3c97328781a1" style="zoom:200%;" />

![1596210218087-c3d40463-b354-4ce2-b92b-c782032589c3](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\1596210218087-c3d40463-b354-4ce2-b92b-c782032589c3.png)



### Fragment

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





### 工作流

-  情况1：

![image-20210513165156180](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210513165156180.png)





- 情况2:

  - $Node \: C$为关键节点

  ![image-20210513165253562](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\powerjob\PowerJob设计原理.assets\image-20210513165253562.png)













### 3.3 关于工作流任务

何时更新任务的下次期望执行时间？

- 新建任务或者编辑任务时候，会根据cron表达式和当前时间，计算任务的下次期望时间，并登记到action_next_trigger_time表。

- 如果任务下线，会删除action_next_trigger_time表中该任务的记录。

- 生成任务实例，插入task表后，会计算下次期望执行时间，并更新action_next_trigger_time表。

 

#### 如何生成任务实例？

 

生成任务实例，是指生成记录插入到task表，状态为INIT或者READY。在之前metasys上，对于日调度的任务，每天一次，一般是凌晨生成当前的所有任务实例，如果依赖条件满足，则置为READY，否则为INIT，插入task表。这种模式已不再适合当前需求。

​    由于工作流任务的最小粒度为5分钟，可以每分钟扫描action_next_tigger_time表，获取触发时间不晚于（当前时间+5分钟）的任务列表，生成任务实例插入task表，插入完成后，计算该任务下次期望执行时间，并更新action_next_tigger_time表。

 

#### 任务生成时，状态如何设置？

-  

 

 

#### 何时提交实例？

 

   

 

#### 实例完成后，如何拉起后续实例？

#####  在DAG图中检查：需要满足以下条件才可以拉起工作流中的后续工作流实例

- 当前节点是非完成状态
- 前置依赖节点为空或者均处于完成状态
  - 带上步长、单位等参数判断工作流实例是否已经完成
  - 可考虑使用`jexl`表达式来判断这些前置依赖条件是否满足



### 3.4.重跑



#### 单个节点的重跑



#### 批量重跑



#### 某个节点后的所有节点



#### 跳过成功实例，仅执行失败实例

















## 部署

[K8S 部署](https://github.com/PowerJob/PowerJob/issues/157)

