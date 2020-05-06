## 阿里云ECS服务器(Centos7)安装Redis教程

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

### 1.安装

- 版本地址：`http://download.redis.io/releases/`

```shell
cd redis-stable	# 进入解压后的文件夹
make MALLOC=libc　#编译安装
cd src && make install 	#/ 进入src目录，进行安装
---
./redis-server		# 直接启动redis 进/usr/local/redis-5.0.8/src/
```



```shell
cd /usr/local/redis-5.0.8/	# 进入 redis安装文件夹
vim redis.conf 	# 修改配置文件
bind 127.0.0.1	--> bind 0.0.0.0	# 命令行模式输入“/bind”查找到该行，并改为任意地址均可连接
daemonize no  --> daemonize yes	# 改为守护进程，即后台运行
requirepass myredisserver	# 设置密码为“myredisserver”
./redis-server /usr/local/redis-5.0.8/redis.conf 
```

#### 设置redis开机自启动

```shell
cd /etc/
mkdir redis	# 在/etc目录下新建redis目录
cp /usr/local/redis-5.0.8/redis.conf /etc/redis/6379.conf		# 将/usr/local/redis-5.0.8/redis.conf文件复制一份到/etc/redis目录下，并命名为6379.conf
cp /usr/local/redis-5.0.8/com.springboot.demo.utils/redis_init_script /etc/init.d/redis		#将redis的启动脚本复制一份放到/etc/init.d目录下
chkconfig --add redis	# 添加到系统服务中
chkconfig redis on		# 执行自启命令
chkconfig redis off    	#关闭开机启动

```





### Reference

[阿里云服务器（ECS）实战--CentOS 7安装redis](https://blog.csdn.net/m0_37903789/article/details/84402930)

