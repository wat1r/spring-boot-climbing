

## 阿里云ECS服务器(Centos7)安装MySQL教程

### 0.前言

- 见前文[阿里云ECS服务器(Centos7)安装Hadoop教程]的环境

- 下载Yum Repository

```shell
wget -i -c http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm
```

### 1.安装

```shell
yum -y install mysql57-community-release-el7-10.noarch.rpm
```

- 安装MySQL服务器,这一步会覆盖掉之前的mariadb

```shell
yum -y install mysql-community-server
```

- 日志

```verilog
Installed:
  mysql-community-libs.x86_64 0:5.7.30-1.el7                     mysql-community-libs-compat.x86_64 0:5.7.30-1.el7                  
  mysql-community-server.x86_64 0:5.7.30-1.el7                  

Dependency Installed:
  mysql-community-client.x86_64 0:5.7.30-1.el7                     mysql-community-common.x86_64 0:5.7.30-1.el7                    

Replaced:
  mariadb-libs.x86_64 1:5.5.64-1.el7                                                                                                

Complete!

```

- 启动MySQL

```shell
systemctl start  mysqld.service
```

- 查看运行状态

```shell
systemctl status mysqld.service
```

- 运行状态

```log
● mysqld.service - MySQL Server
   Loaded: loaded (/usr/lib/systemd/system/mysqld.service; enabled; vendor preset: disabled)
   Active: active (running) since Wed 2020-04-29 09:20:26 CST; 4s ago
     Docs: man:mysqld(8)
           http://dev.mysql.com/doc/refman/en/using-systemd.html
  Process: 2579 ExecStart=/usr/sbin/mysqld --daemonize --pid-file=/var/run/mysqld/mysqld.pid $MYSQLD_OPTS (code=exited, status=0/SUCCESS)
  Process: 2524 ExecStartPre=/usr/bin/mysqld_pre_systemd (code=exited, status=0/SUCCESS)
 Main PID: 2582 (mysqld)
    Tasks: 27
   Memory: 323.7M
   CGroup: /system.slice/mysqld.service
           └─2582 /usr/sbin/mysqld --daemonize --pid-file=/var/run/mysqld/mysqld.pid

Apr 29 09:20:20 centos7.6 systemd[1]: Starting MySQL Server...
Apr 29 09:20:26 centos7.6 systemd[1]: Started MySQL Server.

```

- 查看log中的密码

```verilog
[root@centos7 software]# grep "password"  /var/log/mysqld.log 
2020-04-29T01:20:24.223263Z 1 [Note] A temporary password is generated for root@localhost: lhjg/VBfs92<
```

- 第一次登陆，输入上面的初始密码

```shell
mysql -uroot -p
```

- 修改密码，现在MySQL对简单密码设置了拒绝，123456这种的过不了，大小写字母+符号+数字能通过

```shell
 ALTER USER 'root'@'localhost' IDENTIFIED BY 'new password';
```

- 最后，因为安装了Yum Repository，以后每次yum操作都会自动更新，需要把这个卸载掉

```shell
yum -y remove mysql57-community-release.noarch
```

### 2.Reference

[Centos7安装MySQL](https://www.cnblogs.com/bigbrotherer/p/7241845.html)