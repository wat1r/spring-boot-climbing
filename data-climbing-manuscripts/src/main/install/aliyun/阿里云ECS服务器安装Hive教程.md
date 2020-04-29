## 阿里云ECS服务器(Centos7)安装Hive教程

### 0.前言

- 见前文[阿里云ECS服务器(Centos7)安装Hadoop教程]的环境
- 前置组件版本

```java
java 1.8
hadoop-2.7.5
mysql 5.7
```

### 1.安装

- 下载hive解压重命名

```shell
wget http://archive.apache.org/dist/hive/hive-2.3.0/apache-hive-2.3.0-bin.tar.gz
tar -zxvf apache-hive-2.3.0-bin.tar.gz
mv apache-hive-2.3.0-bin /usr/local/hive
```

- 配置环境变量

```shell
vim /etc/profile
#HIVE_HOME
export HIVE_HOME=/usr/local/hive
export PATH=$PATH:$HIVE_HOME/bin
source /etc/profile
```

- 新建hive-site.xml,添加配置文件,`vim /usr/local/hive/conf/hive-site.xml`
- **这个配置文件有几次错的，下面会讲到**

```xml
<configuration>
<!-- 连接数据库密码 -->
<property>
    <name>javax.jdo.option.ConnectionPassword</name>
    <value>root</value>
</property>
<!-- 连接数据库字符串 -->
<property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
</property>
<!-- 数据库驱动类名 -->
<property>
    <name>javax.jdo.option.ConnectionDriverName</name>
    <value>com.mysql.jdbc.Driver</value>
</property>
<!-- 连接数据库用户 -->
<property>
    <name>javax.jdo.option.ConnectionUserName</name>
    <value>root</value>
    </property>
</configuration>
```



#### 扩展:查找安装的路径

```shell
[root@centos7 usr]# whereis mysql
mysql: /usr/bin/mysql /usr/lib64/mysql /usr/share/mysql /usr/share/man/man1/mysql.1.gz
[root@centos7 usr]# which mysql
/usr/bin/mysql
```

> java/mysql server/mysql connector(驱动)版本对应

![2020-04-29_134513](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\install\aliyun\阿里云ECS服务器安装Hive教程.assets\2020-04-29_134513.png)



- [下载](https://downloads.mysql.com/archives/c-j/)对应匹配的mysql驱动，将其拷贝到`/usr/local/hive/lib/`

```shell
[root@centos7 mysql-connector-java-5.1.30]# pwd
/opt/software/mysql-connector-java-5.1.30
[root@centos7 mysql-connector-java-5.1.30]# cp mysql-connector-java-5.1.30-bin.jar /usr/local/hive/lib/
[root@centos7 mysql-connector-java-5.1.30]# 
```

- 在启动hive前，启动hdfs(usr/local/hadoop/sbin/start-dfs.sh)

- 启动hive发生报错 (/usr/local/hive/bin/hive)

```verilog
hive>  show databases;
FAILED: SemanticException org.apache.hadoop.hive.ql.metadata.HiveException: java.lang.RuntimeException: Unable to instantiate org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient
```

#### 在这个启动过程中的报错

- 原来Hive2需要hive元数据库初始化，需要执行下面的命令

```shell
[root@centos7 bin]# pwd
/usr/local/hive/bin
[root@centos7 bin]# ./schematool -dbType mysql -initSchema
```

- 报错`reateDatabaseIfNotExist=true`

```verilog

Metastore connection URL:	 jdbc:mysql://cluster-1:3306/hive?createDatabaseIfNotExist=true
Metastore Connection Driver :	 com.mysql.jdbc.Driver
Metastore connection User:	 root
org.apache.hadoop.hive.metastore.HiveMetaException: Failed to load driver
Underlying cause: java.lang.ClassNotFoundException : com.mysql.jdbc.Driver
Use --verbose for detailed stacktrace.
```

- 解决方式：mysql的控制台输入：`grant all privileges on *.* to root@'%' identified by 'mysql数据库用户root的密码';` 
- 结果：未能解决
- 解决方式:将如下的`/hive/conf/hive-site.xml`文件的内容修改
- 结果：解决，报错消失

```xml
<!-- 连接数据库字符串 -->
<property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
</property>
修改为
<property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://localhost:3306/hive?useSSL=false</value>
</property>
```

- 报错`Underlying cause: java.sql.SQLException : Access denied for user 'root'@'localhost' (using password: YES)`

- `/hive/conf/hive-site.xml`文件的password配置错了修改成mysql下root用户的密码
- 结果：报错消失

```xml
<!-- 连接数据库密码 -->
<property>
<name>javax.jdo.option.ConnectionPassword</name>
<value>mysql下root用户的登录密码</value>
</property>

```

- 报错：`Unknown database 'hive'`
- 解决方式：mysql中没有hive的这个databases，创建一个，登录mysql建库

```verilog
Underlying cause: com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException : Unknown database 'hive'
SQL Error code: 1049
Use --verbose for detailed stacktrace.
*** schemaTool failed ***
```

```sql
mysql> create database hive;
Query OK, 1 row affected (0.00 sec)
```

- 最后启动成功

```verilog
[root@centos7 bin]# pwd
/usr/local/hive/bin
[root@centos7 bin]# ./schematool -dbType mysql -initSchema
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/usr/local/hive/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/usr/local/hadoop/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Metastore connection URL:	 jdbc:mysql://localhost:3306/hive?useSSL=false
Metastore Connection Driver :	 com.mysql.jdbc.Driver
Metastore connection User:	 root
Starting metastore schema initialization to 2.3.0
Initialization script hive-schema-2.3.0.mysql.sql
Initialization script com
```



- 最终启动hive,成功

```verilog
hive> show databases;
OK
default
Time taken: 6.733 seconds, Fetched: 1 row(s)
hive> 
```

### Reference

[里云服务器centos7.2下基于hadoop2.7安装Hive 2.3.0【成功版】](https://blog.csdn.net/running987/article/details/81541341)