



## 阿里云ECS服务器(Centos7)安装Hadoop教程

### 0.前言

- 机器与操作系统

```shell
当前操作系统版本信息
[root@centos7 bin]# cat /proc/version
Linux version 3.10.0-957.21.3.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-36) (GCC) ) #1 SMP Tue Jun 18 16:35:19 UTC 2019
版本当前操作系统内核信息
[root@centos7 bin]# uname -a
Linux centos7.6 3.10.0-957.21.3.el7.x86_64 #1 SMP Tue Jun 18 16:35:19 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux
```

- java环境

```shell
[root@centos7 bin]# java -version
java version "1.8.0_251"
Java(TM) SE Runtime Environment (build 1.8.0_251-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.251-b08, mixed mode)
```

### 1.安装Hadoop

#### 版本

```shell
hadoop-2.7.5
```

- 下载Hadoop

```shell
wget http://archive.apache.org/dist/hadoop/core/hadoop-2.7.5/hadoop-2.7.5.tar.gzs
```

- 安装Hadoop到/usr/local目录下,重命名该目录

```shell
tar -zxf hadoop-2.7.5.tar.gz -C /usr/local
mv ./hadoop-2.7.5/ ./hadoop
```

- 配置环境变量到/etc/profile

```shell
#HADOOP_HOME
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
exportHADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
```

- 执行`source /etc/profile`使其生效
- 测试hadoop是否安装成功

```shell
[root@centos7 bin]# hadoop version
Hadoop 2.7.5
Subversion https://shv@git-wip-us.apache.org/repos/asf/hadoop.git -r 18065c2b6806ed4aa6a3187d77cbe21bb3dba075
Compiled by kshvachk on 2017-12-16T01:06Z
Compiled with protoc 2.5.0
From source with checksum 9f118f95f47043332d51891e37f736e9
This command was run using /usr/local/hadoop/share/hadoop/common/hadoop-common-2.7.5.jar
```

- 修改

```shell
/usr/local/hadoop/etc/hadoop/core-site.xml
/usr/local/hadoop/etc/hadoop/hdfs-site.xml
```

- core-site.xml

```xml
<configuration>
    <property>
        <name>hadoop.tmp.dir</name> 
        <value>file:/usr/local/hadoop/tmp</value>
        <description>Abase for other temporary directories.</description>
    </property>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

- hdfs-site.xml

```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/usr/local/hadoop/tmp/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/usr/local/hadoop/tmp/dfs/data</value>
    </property>
</configuration>
```

- 格式化NN(NameNode)

```shell
/usr/local/hadoop/bin/hdfs namenode -format
```

输出如下的信息

```verilog
20/04/28 20:32:29 INFO common.Storage: Storage directory /usr/local/hadoop/tmp/dfs/name has been successfully formatted.
20/04/28 20:32:29 INFO namenode.FSImageFormatProtobuf: Saving image file /usr/local/hadoop/tmp/dfs/name/current/fsimage.ckpt_0000000000000000000 using no compression
20/04/28 20:32:29 INFO namenode.FSImageFormatProtobuf: Image file /usr/local/hadoop/tmp/dfs/name/current/fsimage.ckpt_0000000000000000000 of size 321 bytes saved in 0 seconds.
20/04/28 20:32:30 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
20/04/28 20:32:30 INFO util.ExitUtil: Exiting with status 0
20/04/28 20:32:30 INFO namenode.NameNode: SHUTDOWN_MSG: 
```

- 启动NameNode和DataNode进程

```shell
/usr/local/hadoop/sbin/start-dfs.sh
```

上述启动提示需要配置root密码，未果，配置免密登录

#### 配置免密登录

1) Generate ssh key without password

```
$ ssh-keygen -t rsa -P ""
```

2) Copy id_rsa.pub to authorized-keys

```
$  cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
```

3) Start ssh localhost

```
$ ssh localhost
```

输出log

```verilog
[root@centos7 .ssh]# ssh localhost
Last failed login: Tue Apr 28 20:37:25 CST 2020 from 127.0.0.1 on ssh:notty
There were 8 failed login attempts since the last successful login.
Last login: Tue Apr 28 19:45:13 2020 from 61.172.240.228

Welcome to Alibaba Cloud Elastic Compute Service !

```

- 再次启动sbin/start-dfs.sh报错`Error:JAVA_HOME is not set and could not be found `

```shell
vim /usr/local/hadoop/etc/hadoop/hadoop-env.sh
替换成：export JAVA_HOME=/usr/java/jdk1.8.0_251
```

- 再次启动,服务启动了

```shell
[root@centos7 hadoop]# jps
3248 NameNode
3521 SecondaryNameNode
3668 Jps
3367 DataNode
```



- 测试执行jar

```shell
./bin/hadoop jar ./share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.5.jar
```









