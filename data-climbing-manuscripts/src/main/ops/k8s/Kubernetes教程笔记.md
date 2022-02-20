# Kubernetes教程笔记

## 组件介绍

![image-20220219150122968](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219150122968.png)



- 资源管理器
  - Apache MESOS（Twitter）分布式资源管理框架
  - Docker SWARM
  - Kubernetes（Google）borg系统 GO语言
    - 优点：轻量级，消耗资源小，开源，弹性伸缩，负载均衡IPVS 

[Kubernetes 结构 .xmind](../../../../../../../Downloads/Kubernetes 结构 .xmind) 

- 课程内容
  - 基础概念： 什么是 Pod   控制器类型  K8S 网络通讯模式 
  - Kubernetes：  构建 K8S 集群
  - 资源清单：资源   掌握资源清单的语法   编写 Pod   掌握 Pod 的生命周期***
  - Pod 控制器：掌握各种控制器的特点以及使用定义方式
  - 服务发现：掌握 SVC 原理及其构建方式
  - 存储：掌握多种存储类型的特点 并且能够在不同环境中选择合适的存储方案（有自己的简介）
  - 调度器：掌握调度器原理   能够根据要求把Pod 定义到想要的节点运行
  - 安全：集群的认证  鉴权   访问控制 原理及其流程 
  - HELM：Linux yum    掌握 HELM 原理   HELM 模板自定义  HELM 部署一些常用插件
  - 运维：修改Kubeadm 达到证书可用期限为 10年     能够构建高可用的 Kubernetes 集群

- 服务分类：
  - 有状态服务：DBMS
  - 无状态服务：LVS APACHE
- API SERVER：所有服务访问统一入口
- ControllerManager：维持副本期望数目
- Scheduler：负责介绍任务，选择合适的节点进行分配任务
- ETCD ：键值对数据库 存储K8S集群所有重要信息（持久化）
- Kubelet：与容器引擎交互实现容器的生命周期管理
- Kube-prox: 写入规则至 IPTABLES /IPVS 实现服务映射访问的
- COREDNS：可以为集群中的SVC创建一个域名IP的对应关系解析
- DASHBOARD: 为K8S提供 B/S结构访问
- INGRESS CONTREOLLER:官方实现四层代理，这个可以实现七层
- FEDERATION: 提供一个可以跨集群中心K8S统一管理功能
- Prometheus：集群的监控
- ELK：日志的统一介入平台

- Borg系统架构原理

  ![image-20220219154522368](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219154522368.png)

![image-20220219155724467](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219155724467.png)





## Pod的概念

- ReplicationController & ReplicaSet & Deployment

> ReplicationController 用来确保容器应用的副本数始终保持在用户定义的副本数，即如果有容器异常退出，会自动创建新的 Pod 来替代；而如果异常多出来的容器也会自动回收。在新版本的 Kubernetes 中建议使用 ReplicaSet 来取代 ReplicationControlleReplicaSet 跟 ReplicationController 没有本质的不同，只是名字不一样，并且ReplicaSet 支持集合式的 selector虽然 ReplicaSet 可以独立使用，但一般还是建议使用 Deployment 来自动管理ReplicaSet ，这样就无需担心跟其他机制的不兼容问题（比如 ReplicaSet 不支持rolling-update 但 Deployment 支持）

> Deployment 为 Pod 和 ReplicaSet 提供了一个声明式定义 (declarative) 方法，用来替代以前的 ReplicationController 来方便的管理应用。典型的应用场景包括：* 定义 Deployment 来创建 Pod 和 ReplicaSet* 滚动升级和回滚应用* 扩容和缩容* 暂停和继续 Deployment

- HPA（HorizontalPodAutoScale）

> Horizontal Pod Autoscaling 仅适用于 Deployment 和 ReplicaSet ，在 V1 版本中仅支持根据 Pod的 CPU 利用率扩所容，在 v1alpha 版本中，支持根据内存和用户自定义的 metric 扩缩容

- StatefullSet

> StatefulSet 是为了解决有状态服务的问题（对应 Deployments 和 ReplicaSets 是为无状态服务而设计），其应用场景包括：
>
> - 稳定的持久化存储，即 Pod 重新调度后还是能访问到相同的持久化数据，基于 PVC 来实现 
> - 稳定的网络标志，即 Pod 重新调度后其 PodName 和 HostName 不变，基于 Headless Service（即没有 Cluster IP 的 Service ）来实现
> -  有序部署，有序扩展，即 Pod 是有顺序的，在部署或者扩展的时候要依据定义的顺序依次依次进行（即从 0 到 N-1，在下一个 Pod 运行之前所有之前的 Pod 必须都是 Running 和 Ready 状态），基于 init containers 来实现
> - 有序收缩，有序删除（即从 N-1 到 0）

-  DaemonSet

> DaemonSet 确保全部（或者一些）Node 上运行一个 Pod 的副本。当有 Node 加入集群时，也会为他们新增一个 Pod 。当有 Node 从集群移除时，这些 Pod 也会被回收。删除 DaemonSet 将会删除它创建的所有 Pod使用 DaemonSet 的一些典型用法：
>
> -  运行集群存储 daemon，例如在每个 Node 上运行 glusterd、ceph。
> - 在每个 Node 上运行日志收集 daemon，例如fluentd、logstash。
> - 在每个 Node 上运行监控 daemon，例如 Prometheus Node Exporter

- Job，Cronjob

> Job 负责批处理任务，即仅执行一次的任务，它保证批处理任务的一个或多个 Pod 成功结束Cron Job 管理基于时间的 Job，即：
>
> - 在给定时间点只运行一次
> - 周期性地在给定时间点运行



### Kubernetes的网络模型

- Kubernetes 的网络模型假定了所有 Pod 都在一个可以直接连通的扁平的网络空间中，这在GCE（Google Compute Engine）里面是现成的网络模型，Kubernetes 假定这个网络已经存在。而在私有云里搭建 Kubernetes 集群，就不能假定这个网络已经存在了。我们需要自己实现这个网络假设，将不同节点上的 Docker 容器之间的互相访问先打通，然后运行 Kubernetes



- 同一个 Pod 内的多个容器之间：lo各 Pod 之间的通讯：Overlay NetworkPod 与 Service 之间的通讯：各节点的 Iptables 规则



- Flannel 是 CoreOS 团队针对 Kubernetes 设计的一个网络规划服务，简单来说，它的功能是让集群中的不同节点主机创建的 Docker 容器都具有全集群唯一的虚拟IP地址。而且它还能在这些 IP 地址之间建立一个覆盖网络（Overlay Network），通过这个覆盖网络，将数据包原封不动地传递到目标容器内

![image-20220219165709009](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219165709009.png)

- ETCD 之 Flannel 提供说明：
  -  存储管理 Flannel 可分配的 IP 地址段资源
  - 监控 ETCD 中每个 Pod 的实际地址，并在内存中建立维护 Pod 节点路由表

- 同一个 Pod 内部通讯：同一个 Pod 共享同一个网络命名空间，共享同一个 Linux 协议栈

- Pod1 至 Pod2

  -  Pod1 与 Pod2 不在同一台主机，Pod的地址是与docker0在同一个网段的，但docker0网段与宿主机网卡是两个完全不同的IP网段，并且不同Node之间的通信只能通过宿主机的物理网卡进行。将Pod的IP和所在Node的IP关联起来，通过这个关联让Pod可以互相访问

  -  Pod1 与 Pod2 在同一台机器，由 Docker0 网桥直接转发请求至 Pod2，不需要经过 Flannel

- Pod 至 Service 的网络：目前基于性能考虑，全部为 iptables 维护和转发

- Pod 到外网：Pod 向外网发送请求，查找路由表, 转发数据包到宿主机的网卡，宿主网卡完成路由选择后，iptables执行Masquerade，把源 IP 更改为宿主网卡的 IP，然后向外网服务器发送请求

- 外网访问 Pod：Service



## K8S集群安装

- master/node1/node2
  - fannel : master的NotReady -> Ready



```shell
kubectl get pod -o wide
kubectl get svc
kubectl edit svc
修改ClusterIP为NodePort类型 可以web访问
kubectl describe myapp-pod
kubectl log myapp-pod -c test
```



## 资源清单

- 集群资源分类

  - 名称空间级别：kubeadm k8s kube-system  kubectl get pod -n default

  ![image-20220219193621352](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219193621352.png)

  - 集群级别：role
  - 元数据型：HPA

- 资源清单
  - yaml文件来创建pod，称之为资源清单
- 字段解释

![image-20220219194455054](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219194455054.png)

![image-20220219194949471](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219194949471.png)

- 容器生命周期

![image-20220219195900430](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219195900430.png)



![image-20220219201659066](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220219201659066.png)

```shell
kubectl delete svc nginx-deployment
kubectl create -f ini-pod.yaml
```

- 检测探针



## 资源控制器

- 自主Pod

- 控制器管理的Pod：在生命周期内，始终维持副本数

![image-20220220092751548](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220220092751548.png)

![image-20220220095309938](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220220095309938.png)



扩容

```shell

```

## Service

![image-20220220103708847](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220220103708847.png)



- Nginx-ingress



看到6-7





