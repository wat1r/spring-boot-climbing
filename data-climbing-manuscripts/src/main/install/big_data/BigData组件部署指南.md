

# BigData组件部署指南



## 1.Linux

### 1.1.Hive

- 查看hive安装的版本

```shell
[ lib]$ pwd
/usr/lib/hive/lib
[ lib]$ ls -lh | grep hive
-rw-r--r-- 1 root root  124K Nov  8  2019 hive-accumulo-handler-2.1.1-cdh6.3.2.jar
lrwxrwxrwx 1 root root    40 Nov 19 15:46 hive-accumulo-handler.jar -> hive-accumulo-handler-2.1.1-cdh6.3.2.jar
-rw-r--r-- 1 root root   46K Nov  8  2019 hive-ant-2.1.1-cdh6.3.2.jar
lrwxrwxrwx 1 root root    27 Nov 19 15:46 hive-ant.jar -> hive-ant-2.1.1-cdh6.3.2.jar
-rw-r--r-- 1 root root  167K Nov  8  2019 hive-beeline-2.1.1-cdh6.3.2.jar
lrwxrwxrwx 1 root root    31 Nov 19 15:46 hive-beeline.jar -> hive-beeline-2.1.1-cdh6.
```





## 60.Macos

- 编辑文件用sudo权限打开，qa!退出编辑

```shell
➜  bin cat /etc/paths
/usr/local/bin
/usr/bin
/bin
/usr/sbin
/sbin
/Applications/Docker.app/Contents/Resources/bin
```

- 找不到命令`zsh: command not found: docker`

```shell
vim .zshrc
```



### 60.1.Redis

```shell
➜  ~ redis-server /usr/local/etc/redis.conf 
4800:C 24 Apr 18:21:05.53  ....
➜  ~ redis-cli
```

- 常用命令

```shell
# 返回存在的keys
keys * 
```







### 60.2.Docker 

> 参考文档https://www.runoob.com/docker/macos-docker-install.html

需要先启动Docker的Server 即图形化界面



```shell
➜  ~ docker --version
Docker version 20.10.5, build 55c4c88


➜  ~ docker info
Client:
 Context:    default
 Debug Mode: false
 Plugins:
  app: Docker App (Docker Inc., v0.9.1-beta3)
  buildx: Build with BuildKit (Docker Inc., v0.5.1-docker)
  scan: Docker Scan (Docker Inc., v0.6.0)

Server:
 Containers: 0
  Running: 0
  Paused: 0
  Stopped: 0
 Images: 0
 Server Version: 20.10.5
 Storage Driver: overlay2
  Backing Filesystem: extfs
  Supports d_type: true
  Native Overlay Diff: true
 Logging Driver: json-file
 Cgroup Driver: cgroupfs
 Cgroup Version: 1

```





设置镜像加速

可以配置多个镜像

```shell
"registry-mirrors":["http://hub-mirror.c.163.com"]
```



![image-20210424184507859](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210424184507859.png)

```shell
~ docker info
Registry Mirrors:
  http://hub-mirror.c.163.com/
 Live Restore Enabled: false
```





### 60.3.ClickHouse

> 基础知识参考链接：https://www.jianshu.com/p/250e9d9788f2

安装ClickHouse镜像

```shell
docker pull yandex/clickhouse-client
docker pull yandex/clickhouse-server
```

启动容器服务，加载镜像

```shell
docker run -d --name ck-server --ulimit nofile=262144:262144  -p 8123:8123 -p 9000:9000 -p 9009:9009 --volume=$HOME/Documents/ck2_database:/var/lib/clickhouse yandex/clickhouse-server
#上面的这条语句没成功，用下面的这个
docker run -d --name ch-server --ulimit nofile=262144:262144 -p 8123:8123 -p 9000:9000 -p 9009:9009 yandex/clickhouse-server
```

volume：冒号两侧的路径建立映射，当容器服务读取冒号后面的虚拟机内路径时，会去读冒号前面的本机路径。加这个参数的作用是自定义配置，这个参数可以比较简单的修改部分配置。

-p:暴露容器中的端口到本机端口中。本机端口：容器端口。不配置的话可以后面除来虚拟机中，别的地方连不上8123端口。
查看启动情况

```shell
➜  Resources docker ps
CONTAINER ID   IMAGE                      COMMAND            CREATED          STATUS          PORTS                                                                    NAMES
2864fdf80b8f   yandex/clickhouse-server   "/entrypoint.sh"   56 seconds ago   Up 52 seconds   0.0.0.0:8123->8123/tcp, 0.0.0.0:9000->9000/tcp, 0.0.0.0:9009->9009/tcp   ch-server
➜  Resources
```

进入容器校验一下

```shell
➜  Resources docker exec -it ch-server /bin/bash
root@2864fdf80b8f:/# clickhouse-client
ClickHouse client version 21.4.4.30 (official build).
Connecting to localhost:9000 as user default.
Connected to ClickHouse server version 21.4.4 revision 54447.

2864fdf80b8f :) show databases;

SHOW DATABASES

Query id: dc6cd7d7-c423-4ffd-90fc-46ff0219fa36

┌─name────┐
│ default │
│ system  │
└─────────┘

2 rows in set. Elapsed: 0.003 sec.

2864fdf80b8f :)
```





一些测试命令

```shell
CREATE TABLE arrays_test (s String, arr Array(UInt8)) ENGINE = Memory

INSERT INTO arrays_test VALUES ('Hello', [1,2]), ('World', [3,4,5]), ('Goodbye', [])

SELECT * FROM arrays_test

select s , arr from arrays_test ARRAY join arr

#制定别名
SELECT s, arr, a FROM arrays_test ARRAY JOIN arr AS a

SELECT s, arr, a, num, mapped FROM arrays_test ARRAY JOIN arr AS a, arrayEnumerate(arr) AS num, arrayMap(x -> x + 1, arr) AS mapped
 


```



### 60.4.Mysql

> 查看mysql的版本信息

```shell
➜  ~ brew  info mysql 
mysql: stable 8.0.23 (bottled)
Open source relational database management system
https://dev.mysql.com/doc/refman/8.0/en/
Conflicts with:
  mariadb (because mysql, mariadb, and percona install the same binaries)
  percona-server (because mysql, ma
  
安装5.7版本mysql
➜  ~ brew install mysql@5.7

```

上面的安装失败了采用dmg的方式安装

```shell
2021-04-24T11:48:20.760596Z 1 [Note] A temporary password is generated for root@localhost: !103t(S-u6%=

If you lose this password, please consult the section How to Reset the Root Password in the MySQL reference manual.
```

查看安装版本

```shell
➜  bin mysql -V
mysql  Ver 14.14 Distrib 5.7.29, for macos10.14 (x86_64) using  EditLine wrapper
➜  bin
```

启动mysql

![image-20210424201258050](/Users/frankcooper/Library/Application Support/typora-user-images/image-20210424201258050.png)



修改密码

```java
mysql> alter user 'root'@'localhost' identified by 'root';
mysql> update mysql.user set authentication_string=PASSWORD('123456') where user='root';


```





### 60.5.brew



改成中科大的源：

```shell
# 进入 brew 的仓库根目录
cd "$(brew --repo)"

# 修改为中科大的源
git remote set-url origin https://mirrors.ustc.edu.cn/brew.git
```

同理，修改 homebrew-cask、homebrew-core、homebrew-services 的远程仓库地址

```shell
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-cask"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-cask.git

cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
```

修改完仓库地址后，更新一下，加上 `-v` 参数可以看到当前跑的进度：

```shell
brew update -v
```

http://www.manongjc.com/detail/15-jxncezljoldhtop.html







### 60.6.Kafka

启动
启动zookeeper
kafka是基于zookeeper的，启动kafka之前，需要先启动zookeeper

 ```shell
/usr/local/Cellar/kafka/3.7.0/bin/zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &

/usr/local/etc/zookeeper
#这个目录下
/usr/local/Cellar/zookeeper/3.7.0/bin
#启动/停止
./zkServer start
./zkServer stop
ps aux | grep zookeeper 查看进程
或
➜  bin jps
17381 Jps
357
17305 QuorumPeerMain
 ```

https://blog.csdn.net/weixin_33207551/article/details/86521905



安装kafka

```shell
brew install kafka
/usr/local/Cellar/kafka/2.8.0/bin
./kafka-server-start /usr/local/etc/kafka/server.properties &
#检测是否启动成功
ps aux | grep kakfka
```



https://www.cnblogs.com/mysticbinary/p/13848497.html









