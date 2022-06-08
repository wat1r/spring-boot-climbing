## The 使用VMware Fusion在macos环境搭建Centos





安装Maven

- [centos7.6安装maven](https://blog.csdn.net/weixin_43824267/article/details/112168050)



安装MySQL

- [Centos7安装Mysql5.7最全教程](https://blog.csdn.net/weixin_43451430/article/details/115553108)



安装Flink集群

- [Flink（二）CentOS7.5搭建Flink1.6.1分布式集群](https://www.cnblogs.com/frankdeng/p/9400627.html)
- https://flink.apache.org/downloads.html
- [Flink JobManager HA模式部署（基于Standalone）](https://www.cnblogs.com/liugh/p/7482571.html)
- https://flink.apache.org/downloads.html#additional-components
- [flink安装以及运行自带wordcount示例（单机版，无hadoop环境）](https://blog.csdn.net/wiborgite/article/details/83418999)

> 报错

- [Could not create FileSystem for highly available storage path (hdfs://node7-1/flink/ha/flinkCluster)](https://blog.csdn.net/lkm0522/article/details/109358615)
- [安装flink以及flink启动异常：start-cluster.sh](https://www.jianshu.com/p/ef73441b6caa)
- [Flink集群之flink集群的启动问题：start-cluster.sh](https://blog.csdn.net/qq_39338967/article/details/105214920)
- [The number of live datanodes 0 has reached the minimum number 0.](https://blog.csdn.net/hongchenshijie/article/details/103404899)

安装Zookeeper

- http://zookeeper.apache.org/
- [Zookeeper 集群安装配置，超详细，速度收藏！](https://cloud.tencent.com/developer/article/1458839)

- [Zookeeper3.4.9分布式集群安装](https://www.cnblogs.com/liugh/p/6671460.html)



安装Kafka

- https://kafka.apache.org/downloads
- [Centos7 安装kafka集群](https://blog.csdn.net/qq_25763595/article/details/107731706)
- [centos上的kafka集群搭建](https://blog.csdn.net/zhang5324496/article/details/121940840)



安装ElasticSearch

- [Elasticsearch集群搭建（基于Elasticsearch7.5.1）](https://segmentfault.com/a/1190000021589726)
- [Linux搭建es集群详细教程（最终版）](https://blog.csdn.net/qq_50227688/article/details/115379121)



```java

vim /etc/

TYPE="Ethernet"
PROXY_METHOD="none"
BROWSER_ONLY="no"
BOOTPROTO="dhcp"
DEFROUTE="yes"
IPV4_FAILURE_FATAL="no"
IPV6INIT="yes"
IPV6_AUTOCONF="yes"
IPV6_DEFROUTE="yes"
IPV6_FAILURE_FATAL="no"
IPV6_ADDR_GEN_MODE="stable-privacy"
NAME="ens33"
UUID="a925ebd8-6a58-480f-b7c9-ce4d223f0661"
DEVICE="ens33"
ONBOOT="yes"
IPADDR=192.168.168.170
GATEWAY=192.168.168.2
NETMASK=255.255.255.0
DNS1=192.168.0.1

range 192.168.168.128 192.168.168.254;

ip = 192.168.168.2
netmask = 255.255.255.0


ssh-keygen -t rsa
ssh-copy-id hadoop168
ssh-copy-id hadoop169
ssh-copy-id hadoop170

yum remove *openjdk*
yum remove  copy-jdk-configs-3.3-2.el7.noarch


vim /etc/sudoers

hadoop  ALL=(ALL)    ALL
frankcooper  ALL=(ALL)    ALL



/usr/local/bin下创建xsync文件 写入如下脚本 chmod u+x xsync 

#!/bin/bash
#1 获取输入参数个数，如果没有参数，直接退出
pcount=$#
if [ $pcount -lt 1 ]
then
    echo Not Enough Arguement!
    exit;
fi

#2. 遍历集群所有机器
# 也可以采用：
# for host in hadoop{168..170};
for host in hadoop168 hadoop169 hadoop170
do
    echo ====================    $host    ====================
    #3. 遍历所有目录，挨个发送
    for file in $@
    do
        #4 判断文件是否存在
        if [ -e $file ]
        then
            #5. 获取父目录
            pdir=$(cd -P $(dirname $file); pwd)
            echo pdir=$pdir
            
            #6. 获取当前文件的名称
            fname=$(basename $file)
            echo fname=$fname
            
            #7. 通过ssh执行命令：在$host主机上递归创建文件夹（如果存在该文件夹）
            ssh $host "mkdir -p $pdir"
            
			#8. 远程同步文件至$host主机的$USER用户的$pdir文件夹下
            rsync -av $pdir/$fname $USER@$host:$pdir
        else
            echo $file does not exists!
        fi
    done
done

vi /etc/profile

export JAVA_HOME=/opt/jdk1.8.0_162
export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/sbin


<!-- 指定HDFS中NameNode的地址 --> 
     <property>
     <name>fs.defaultFS</name>
         <value>hdfs://hadoop168:9000</value>
     </property>
<!-- 指定hadoop运行时产生文件的存储目录 --> <property> 
     <name>hadoop.tmp.dir</name>
     <value>/opt/hadoop-2.7.2/data/full/tmp</value>
     </property>


     <!-- 设置dfs副本数，不设置默认是3个 -->
     <property>
         <name>dfs.replication</name>
         <value>2</value>
     </property>
     <!-- 设置secondname的端口 -->
     <property>
         <name>dfs.namenode.secondary.http-address</name>
         <value>hadoop169:50090</value>
     </property>

<!-- 指定mr运行在yarn上 -->
<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>


<!-- reducer获取数据的方式 -->
<property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
</property>
<!-- 指定YARN的ResourceManager的地址 -->
<property>
    <name>yarn.resourcemanager.hostname</name>
    <value>hadoop169</value>
</property>
<property>
    <name>yarn.nodemanager.vmem-check-enabled</name>
    <value>false</value>
    <description>Whether virtual memory limits will be enforced for containers</description>
</property>
<property>
    <name>yarn.nodemanager.vmem-pmem-ratio</name>
    <value>4</value>
    <description>Ratio between virtual memory to physical memory when setting memory limits
for containers</description>
</property>


export HADOOP_HOME=/opt/hadoop-2.7.2
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin


关闭几个节点的防火墙
[root@hadoop169 hadoop]# systemctl stop firewalld
[root@hadoop169 hadoop]# systemctl disable firewalld




访问主节点的50070端口
http://192.168.168.168:50070/

http://192.168.168.169:8088


1）各个服务组件逐一启动

分别启动hdfs 组件：

hadoop-deamon.sh start | stop namenode | datnode | secondarynamenode
启动yarn：

yarn-deamon.sh start | stop resourcemanager | nodemanager
2) 各个模块分开启动(常用)

start | stop-dfs.sh
start | stop-yarn.sh
3) 全部启动

start | stop-all.sh

hadoop169上的ResourceManager start-yarn.sh


ALTER USER 'root'@'localhost' IDENTIFIED BY '>X:kdX%fS6?i';


create table phone_info(pid INT, name CHAR(20), price INT);


export NODE_HOME=/opt/node-v16.14.2
export PATH=$PATH:${NODE_HOME}/bin



export MAVEN_HOME=/opt/apache-maven-3.8.1
export PATH=$PATH:${MAVEN_HOME}/bin

export FLINK_HOME=/opt/flink-1.12.7
export PATH=$PATH:${FLINK_HOME}/bin

export ZOOKEEPER_HOME=/opt/zookeeper-3.6.1
export PATH=$PATH:${ZOOKEEPER_HOME}/bin

export KAFKA_HOME=/opt/kafka_2.11-2.4.0
export PATH=$PATH:${KAFKA_HOME}/bin




flink webui访问
http://192.168.168.168:8081/

high-availability: zookeeper
high-availability.zookeeper.quorum: hadoop168:2181,hadoop169:2181,hadoop170:2181
high-availability.storageDir: hdfs:///flink/ha/
high-availability.zookeeper.path.root: /flink
high-availability.cluster-id: /flinkCluster
state.backend: filesystem
state.checkpoints.dir: hdfs:///flink/checkpoints
state.savepoints.dir: hdfs:///flink/checkpoints


server.1=hadoop168:2888:3888
server.2=hadoop169:2888:3888
server.3=hadoop170:2888:3888


zoo.cfg 

dataDir=/opt/zookeeper-3.6.1/data
dataLogDir=/opt/zookeeper-3.6.1/logs

server.1=hadoop168:2888:3888
server.2=hadoop169:2888:3888
server.3=hadoop170:2888:3888

下载zookeeper带bin的gz包

7.1 启动Zookeeper集群
分别在Leader及两个Follower上执行命令：
zkServer.sh start
7.2 查看Zookeeper状态
执行如下命令：
zkServer.sh status
7.3 停止Zookeeper

zkServer.sh stop
7.4 重启Zookeeper
zkServer.sh restart



WordCount

[root@hadoop168 flink-1.12.7]# flink run -m hadoop168:8081 ./examples/batch/WordCount.jar --input hdfs:///data/input/hello.txt  --output hdfs:///data/output/flink_wc_hello.txt
[root@hadoop168 flink-1.12.7]# hdfs dfs -cat /data/output/flink_wc_hello.txt
  
  
  wget \
https://github.com/streamxhub/streamx/releases/download/v1.2.2/st\
reamx-console-service-\
1.2.2-bin.tar.gz

http://hadoop168:10000/



cluster.sh
flink的集群启动的时候报进入安全模式  
  

case $1 in
"start"){
        for i in hadoop168
        do
                echo "-------start $i hadoop cluster-------"
                ssh $i "sh /opt/hadoop-2.7.2/sbin/start-dfs.sh"
        done
        for i in hadoop169
        do
                echo "-------start $i yarn-------"
                ssh $i "sh /opt/hadoop-2.7.2/sbin/start-yarn.sh"
        done
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------start $i zookeeper-------"
                ssh $i "/opt/zookeeper-3.6.1/bin/zkServer.sh start"
        done
#       for i in hadoop168
#       do
 #              echo "-------start $i flink cluster-------"
  #             ssh $i " source /etc/profile; cd /opt/flink-1.12.7/bin; ./start-cluster.sh"
#       done

};;
"stop"){
    for i in hadoop168
    do
            echo "-------stop $i hadoop cluster-------"
            ssh $i "sh /opt/hadoop-2.7.2/sbin/stop-dfs.sh"
    done
    for i in hadoop169
    do
            echo "-------stop $i yarn-------"
            ssh $i "sh /opt/hadoop-2.7.2/sbin/stop-yarn.sh"
    done
    for i in hadoop168 hadoop169 hadoop170
    do
            echo "-------stop $i zookeeper-------"
            ##停止目录以及命令
            ssh $i "/opt/zookeeper-3.6.1/bin/zkServer.sh stop"
    done
    for i in hadoop168
    do
        echo "-------stop $i flink cluster-------"
        ssh $i " source /etc/profile; cd /opt/flink-1.12.7/bin; ./stop-cluster.sh"
    done
};;
esac


## streamx start | stop
## /opt/streamx-console-service-1.2.2/bin startup.sh shutdown.sh
  

  一键启停zk
  
  #!/bin/bash

case $1 in
"start"){
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------启动 $i zookeeper-------"
                ##come on  JMX_PORT是一个监听窗口后面的是启动目录以及命令
                ssh $i "/opt/zookeeper-3.6.1/bin/zkServer.sh start"
        done
};;
"stop"){
                #in后面的是你完全分布式的三个虚拟机的名字
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------停止 $i zookeeper-------"
                ##停止目录以及命令
                ssh $i "/opt/zookeeper-3.6.1/bin/zkServer.sh stop"
        done
};;
esac
  
  
  
  
# broker在集群的唯一标志，类似身份证。可以是任意integer值，但必须唯一 169节点为2 170节点为3
broker.id=1
#依赖的zookeeper集群地址
zookeeper.connect=hadoop168:2181,hadoop169:2181,hadoop170:2181
#broker节点的ip地址，以及对外服务端口.PLAINTEXT在这里既是变量名，也是通讯协议名
# 每个节点用自己的ip地址 
listeners=PLAINTEXT://192.168.168.168:9092
# kafka的数据存储路径
log.dir=/opt/kafka_2.11-2.4.0/data/kafka-log


############## 上面是必须配置的 ######## 下面的配置是实现内外网隔离，较少用到。#########

# 监听器名称和安全协议的映射配置。
# ⽐如，可以将内外⽹隔离，即使它们都使⽤SSL。
# 这么配置的意思是INTERNAL和EXTERNAL都使用了SSL协议，也可以定义其他的协议。
# listener.security.protocol.map=INTERNAL:SSL,EXTERNAL:SSL

# 内部broker间的通信使用的监听器，EXTERNAL在上面是定义了SSL协议
# inter.broker.listener.name=EXTERNAL
# 暴露给外网的服务地址，如果没有配置，则用上面的listeners配置值
# advertised.listeners=://192.168.168.169:9092

kafka-server-start.sh -daemon /opt/kafka_2.11-2.4.0/config/server.properties

./kafka-topics.sh --create --zookeeper 192.168.168.168:2181 --replication-factor 1 --partitions 1 --topic test

./kafka-console-consumer.sh --bootstrap-server 192.168.168.168:9092 --topic test --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.168.169:9092 --topic test --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.168.170:9092 --topic test --from-beginning


./bin/kafka-console-producer.sh --broker-list 192.168.168.169:9092 --topic test
  
  
./bin/kafka-console-producer.sh --broker-list 192.168.168.168:9092 --topic hotitems
  
  
  一键启停kafka集群
  
  #!/bin/bash

case $1 in
"start"){
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------start $i kafka-------"
                ssh $i "source /etc/profile; cd /opt/kafka_2.11-2.4.0/bin; ./kafka-server-start.sh -daemon /opt/kafka_2.11-2.4.0/config/server.properties"
        done
};;
"stop"){
                #in后面的是你完全分布式的三个虚拟机的名字
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------stop $i kafka-------"
                ##停止目录以及命令
                ssh $i "cd /opt/kafka_2.11-2.4.0/bin; ./kafka-server-stop.sh"
        done
};;
esac
  
  
单节点启动/停用单个服务
HDFS：hdfs --daemon start/stop namenode/datanode/secondarynamenode
Yarn：yarn --daemon start/stop resourcemanager nodemanager
  
  
  elastic/elastic123
  
  chown -R elastic:elastic  ../elasticsearch-7.8.0/
    
# 分开启动    
    
#!/bin/bash

case $1 in
"start"){
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------start $i elasticsearch-------"
                ssh $i "source /etc/profile; cd /opt/elasticsearch-7.8.0; ./bin/elasticsearch"
        done
};;
"stop"){
                #in后面的是你完全分布式的三个虚拟机的名字
        for i in hadoop168 hadoop169 hadoop170
        do
                echo "-------stop $i elasticsearch-------"
                ##停止目录以及命令
                
        done
};;
esac
  
```



hadoop 访问主节点的50070端口
http://192.168.168.168:50070/

http://192.168.168.169:8088

flink webui访问
http://192.168.168.168:8081/

Streamx webui访问

http://hadoop168:10000/



- 节点资源规划表

|               | Version    | hadoop168                                                 | hadoop169                                                 | hadoop170          |
| ------------- | ---------- | --------------------------------------------------------- | --------------------------------------------------------- | ------------------ |
| HDFS          | 2.7.2      | NameNode<br />DataNode                                    | SecondaryNameNode<br />DataNode                           | DataNode           |
| Yarn          | 2.7.2      | NodeManager                                               | ResourceManager<br />NodeManager                          | NodeManager        |
| ZooKeeper     | 3.6.1      | QuorumPeerMain                                            | QuorumPeerMain                                            | QuorumPeerMain     |
| Kafka         | 2.11-2.4.0 | Kafka                                                     | Kafka                                                     | Kafka              |
| Flink         | 1.12.7     | StandaloneSessionClusterEntrypoint<br />TaskManagerRunner | StandaloneSessionClusterEntrypoint<br />TaskManagerRunner | TaskManagerRunner  |
| ElasticSearch | 7.8.0      | ElasticSearch                                             | ElasticSearch                                             | ElasticSearch      |
|               |            |                                                           |                                                           |                    |
| StreamX       | 1.2.2      | :white_check_mark:                                        |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |
| JDK           | 1.8        | :white_check_mark:                                        | :white_check_mark:                                        | :white_check_mark: |
| MySQL         | 5.7.38     | :white_check_mark:                                        |                                                           |                    |
| Node          | 16.14.2    | :white_check_mark:                                        |                                                           |                    |
| Maven         | 3.8.1      | :white_check_mark:                                        |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |
|               |            |                                                           |                                                           |                    |





- 环境：

  - jdk1.8

  -  centos7.6
  - elasticsearch7.8

config/elasticsearch.yml

- hadoop168:

```yml
cluster.name: elasticsearch-cluster
node.name: hadoop168
node.master: true
node.data: true
node.max_local_storage_nodes: 3
path.data: /opt/elasticsearch-7.8.0/data
path.logs: /opt/elasticsearch-7.8.0/logs
network.host: 192.168.168.168
http.port: 9200
transport.tcp.port: 9300
discovery.seed_hosts: ["192.168.168.168:9300", "192.168.168.169:9300", "192.168.168.170:9300"]
cluster.initial_master_nodes: ["192.168.168.168", "192.168.168.169", "192.168.168.170"]
xpack.monitoring.collection.enabled: true

```

- hadoop169:

```yml
cluster.name: elasticsearch-cluster
node.name: hadoop169
node.master: true
node.data: true
node.max_local_storage_nodes: 3
path.data: /opt/elasticsearch-7.8.0/data
path.logs: /opt/elasticsearch-7.8.0/logs
network.host: 192.168.168.169
http.port: 9200
transport.tcp.port: 9300
discovery.seed_hosts: ["192.168.168.168:9300", "192.168.168.169:9300", "192.168.168.170:9300"]
cluster.initial_master_nodes: ["192.168.168.168", "192.168.168.169", "192.168.168.170"]
xpack.monitoring.collection.enabled: true
```

- hadoop170

```yml
cluster.name: elasticsearch-cluster
node.name: hadoop170
node.master: true
node.data: true
node.max_local_storage_nodes: 3
path.data: /opt/elasticsearch-7.8.0/data
path.logs: /opt/elasticsearch-7.8.0/logs
network.host: 192.168.168.170
http.port: 9200
http.port: 9200
transport.tcp.port: 9300
discovery.seed_hosts: ["192.168.168.168:9300", "192.168.168.169:9300", "192.168.168.170:9300"]
cluster.initial_master_nodes: ["192.168.168.168", "192.168.168.169", "192.168.168.170"]
xpack.monitoring.collection.enabled: true
```

报错信息：

```log
[2022-05-06T20:39:16,429][INFO ][o.e.n.Node               ] [hadoop168] initialized
[2022-05-06T20:39:16,429][INFO ][o.e.n.Node               ] [hadoop168] starting ...
[2022-05-06T20:39:16,666][INFO ][o.e.t.TransportService   ] [hadoop168] publish_address {192.168.168.168:9300}, bound_addresses {192.168.168.168:9300}
[2022-05-06T20:39:16,974][INFO ][o.e.b.BootstrapChecks    ] [hadoop168] bound or publishing to a non-loopback address, enforcing bootstrap checks
[2022-05-06T20:39:26,987][WARN ][o.e.c.c.ClusterFormationFailureHelper] [hadoop168] master not discovered yet, this node has not previously joined a bootstrapped (v7+) cluster, and this node must discover master-eligible nodes [192.168.168.168, 192.168.168.169, 192.168.168.170] to bootstrap a cluster: have discovered [{hadoop168}{2Lz7sEkTTRWz61t3JmQMBg}{eE-SlHtOQCOKNcowSyrW2Q}{192.168.168.168}{192.168.168.168:9300}{dilmrt}{ml.machine_memory=1910075392, xpack.installed=true, transform.node=true, ml.max_open_jobs=20}]; discovery will continue using [192.168.168.169:9300, 192.168.168.170:9300] from hosts providers and [{hadoop168}{2Lz7sEkTTRWz61t3JmQMBg}{eE-SlHtOQCOKNcowSyrW2Q}{192.168.168.168}{192.168.168.168:9300}{dilmrt}{ml.machine_memory=1910075392, xpack.installed=true, transform.node=true, ml.max_open_jobs=20}] from last-known cluster state; node term 0, last-accepted version 0 in term 0
```

上面的warning日志不影响集群，不要使用启动，分开启动



http://192.168.168.168:9200/_cat/health?v

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220506213948.png)





### Reference

- [VMware Workstation Pro 官方下载地址合集 更新 VMware-workstation-full-16.2.0](https://cyhour.com/1482/)

- [Mac VMware Fusion CentOS7配置静态IP](https://www.cnblogs.com/itbsl/p/10998696.html)

- [Hadoop集群配置免密SSH登录方法](https://www.cnblogs.com/shireenlee4testing/p/10366061.html)

- [Mac-搭建Hadoop集群](https://www.cnblogs.com/taojietaoge/p/10803537.html)
- [大数据环境搭建步骤详解（Hadoop，Hive，Zookeeper，Kafka，Flume，Hbase，Spark等安装与配置）](https://blog.csdn.net/pig2guang/article/details/85313410#Hadoop_131)

