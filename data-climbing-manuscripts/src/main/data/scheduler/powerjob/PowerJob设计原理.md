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

```java
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
	- 


```















## 部署

[K8S 部署](https://github.com/PowerJob/PowerJob/issues/157)

