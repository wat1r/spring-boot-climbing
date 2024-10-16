##  MySQL语句指南

### 安装

[CentOS7安装mysql详细过程（tar解压方式）](https://blog.csdn.net/yin_zh0522/article/details/107383010)

[Centos7环境下安装MySQL8（基于tar.gz压缩文件安装）](https://blog.csdn.net/threelifeadv/article/details/122610308)

### 四大操作

```shell
#1.DDL（Data Definition Language）数据库定义语言statements are used to define the database structure or schema.

DDL是SQL语言的四大功能之一。
用于定义数据库的三级结构，包括外模式、概念模式、内模式及其相互之间的映像，定义数据的完整性、安全控制等约束
DDL不需要commit.
CREATE
ALTER
DROP
TRUNCATE
COMMENT
RENAME

#2.DML（Data Manipulation Language）数据操纵语言statements are used for managing data within schema objects.

由DBMS提供，用于让用户或程序员使用，实现对数据库中数据的操作。
DML分成交互型DML和嵌入型DML两类。
依据语言的级别，DML又可分成过程性DML和非过程性DML两种。
需要commit.
SELECT
INSERT
UPDATE
DELETE
MERGE
CALL
EXPLAIN PLAN
LOCK TABLE

#3.DCL（Data Control Language）数据库控制语言  授权，角色控制等
GRANT 授权
REVOKE 取消授权

#4.TCL（Transaction Control Language）事务控制语言
SAVEPOINT 设置保存点
ROLLBACK  回滚
SET TRANSACTION
```





### DDL操作

#### 创建用户、赋权

```mysql
-- 创建新用户
CREATE USER 'hadoop'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'root'@'%' IDENTIFIED BY '123456';

-- 赋予所有权限在特定数据库上
GRANT ALL PRIVILEGES ON database_name.* TO 'hadoop'@'localhost';

-- 赋予特定权限在特定数据库上
GRANT SELECT, INSERT, UPDATE ON database_name.* TO 'hadoop'@'localhost';

-- 刷新权限，使更改生效
FLUSH PRIVILEGES;
```

删除用户，移除权限

```mysql
-- 移除特定权限在特定数据库上
REVOKE SELECT, INSERT, UPDATE ON database_name.* FROM 'hadoop'@'localhost';

-- 移除所有权限在特定数据库上
REVOKE ALL PRIVILEGES ON database_name.* FROM 'hadoop'@'localhost';

-- 刷新权限，使更改生效
FLUSH PRIVILEGES;

-- 删除用户
DROP USER 'hadoop'@'localhost';

-- 刷新权限，使更改生效
FLUSH PRIVILEGES;
```

查看用户权限

```mysql
# 查看当前用户权限
show grants;

show grants for hadoop@localhost; 

```



[mysql用户权限管理：查看用户权限、授予用户权限、收回用户权限](https://blog.csdn.net/chushiyan/article/details/107586971)





### Meta表

#### 查询表的字段

```mysql
SELECT
    CONCAT(COLUMN_NAME )
FROM
    information_schema.COLUMNS
WHERE
    TABLE_SCHEMA = 'jn_power'
AND TABLE_NAME = 'rpt_cap_ammeter_2018';
```



#### 登录

```shell
mysql -u root -pxxxxx -h 127.0.0.1 -P 23306
mysql –uusername [–h主机名或者IP地址] –ppassword
mysql -h主机地址 -u用户名 －p用户密码 
```





#### Grants

```linux
grant
创建一个可以从任何地方连接服务器的一个完全的超级用户，但是必须使用一个口令something做这个
mysql> grant all privileges on *.* to user@localhost identified by ’something’ with
增加新用户
格式：grant select on 数据库.* to 用户名@登录主机 identified by “密码”
GRANT ALL PRIVILEGES ON *.* TO monty@localhost IDENTIFIED BY ’something’ WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO monty@”%” IDENTIFIED BY ’something’ WITH GRANT OPTION;
删除授权：
mysql> revoke all privileges on *.* from root@”%”;
mysql> delete from user where user=”root” and host=”%”;
mysql> flush privileges;
创建一个用户custom在特定客户端it363.com登录，可访问特定数据库fangchandb
mysql >grant select, insert, update, delete, create,drop on fangchandb.* to custom@ it363.com identified by ‘ passwd’
重命名表:
mysql > alter table t1 rename t2;
```







#### 建库

```mysql
CREATE DATABASE
IF NOT EXISTS hiveassistant2_test;
```



#### 删除数据库

```mysql
mysql>drop datase hive;
```





#### 重命名数据库名称

#####  **mysqldump导出数据再导入**

```mysql
mysqldump -uxxxx -pxxxx -h xxxx db_name > db_name_dump.SQL
mysql -uxxxx -pxxxx -h xxxx -e “CREATE DATABASE new_db_name”
mysql -uxxxx -pxxxx -h xxxx new_db_name < db_name_dump.SQL
mysql -uxxxx -pxxxx -h xxxx -e “DROP DATABASE db_name”
```



#### mysql 查询表字段信息（字段名、描述、类型、长度)

```mysql
SELECT 
  COLUMN_NAME 列名, 
  COLUMN_TYPE 数据类型, 
  if(IS_NULLABLE='YES','是','否') 是否为空,
  COLUMN_DEFAULT 默认值,
  COLUMN_COMMENT 备注
FROM 
 INFORMATION_SCHEMA.COLUMNS 
where 
table_schema ='guns'    -- 数据库名称 
AND 
table_name  = 'sys_user'    -- 表名
```



```mysql
SHOW FULL COLUMNS 
FROM
	functions
```



#### 添加字段

```mysql
ALTER TABLE db.functions ADD func_demo VARCHAR(2000) DEFAULT NULL COMMENT '函数示例';
ALTER TABLE hiveassistant2_private.history_log ADD op_type int(11) DEFAULT NULL COMMENT '操作类型';
ALTER TABLE hiveassistant2_private.functions ADD func_type_en varchar(50) DEFAULT NULL COMMENT '函数类型英文';
ALTER TABLE hiveassistant2_dev.functions ADD func_type_en varchar(50) DEFAULT NULL COMMENT '函数类型英文';
ALTER TABLE hiveassistant2_private.track_action ADD user_name varchar(100) DEFAULT NULL COMMENT '用户名';
ALTER TABLE hiveassistant2_dev.query_pages ADD source tinyint(1) DEFAULT 0 COMMENT '来源，0或者1:收藏页，2:工作空间';
ALTER TABLE hiveassistant2_dev.query_pages ADD seed_id int(11) DEFAULT NULL COMMENT '有值的时候是查询收藏页面的id';



ALTER TABLE hiveassistant2_private.track_action ADD operate_id int(11) DEFAULT NULL COMMENT '操作编号，细粒度划分操作的动作';
```

#### 添加索引

```sql
ALTER TABLE hiveassistant2_private.track_action ADD INDEX operate_id_index (operate_id);
ALTER TABLE hiveassistant2_private.track_action ADD INDEX page_id_event_type_index (page_id,event_type);
ALTER TABLE hiveassistant2_private.history_log ADD INDEX job_id_index (job_id);

```



#### 修改字段的Comment

```sql
ALTER TABLE `smallbang-ljy`.job_info MODIFY script varchar(10240) COMMENT "test";
```



#### 修改字段名称

```mysql
alter table em_day_data change f_day_house11 f_day_hour11 int(11) not null
alter table `smallbang-dev`.workflow_params change relation_status constraint_status  tinyint(6) NOT NULL

```











#### 查询建表时的语句

```sql
SHOW CREATE TABLE TEST
```

#### 查询blob类型的数据转string

```sql
select CONVERT(code USING utf8) from table WHERE id =XXX
```



#### mysql 查询表的字段名称，字段类型

```sql
select column_name,column_comment,data_type 
from information_schema.columns 
where table_name='查询表名称' and table_schema='数据库名称'
```



#### 删除表数据

```sql
DELETE FROM hiveassistant2_test.functions
WHERE engine_type='hive';
```

#### 删除表中的某个字段

```sql
ALTER TABLE db.table DROP COLUMN currency_unit_type;
```



#### 清空数据库中表的所有数据

```sql
SELECT CONCAT('truncate table ',TABLE_NAME,';') AS a FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'data_studio_dev' ;
```



#### mysql根据逗号将一行数据拆分成多行数据

- https://blog.csdn.net/qq_43511677/article/details/107210163

```mysql
SELECT
a.id,
	substring_index(
		substring_index(
			a.rel_tenant_ids,
			',',
			b.help_topic_id + 1
		),
		',' ,- 1
	) AS tenant_id
FROM
	sys_user a
JOIN mysql.help_topic b ON b.help_topic_id < (
	length(a.rel_tenant_ids) - length(
		REPLACE (a.rel_tenant_ids, ',', '')
	) + 1
)
WHERE a.id = 32
ORDER BY tenant_id asc
```







#### 更新某个字段的值

```sql
UPDATE db.functions 
SET func_method = func_name
WHERE
	func_method IS NULL 
	AND engine_type = 'presto'
	
	
update name=cast(name as binary);
```



![image-20210415174808346](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\sql\mysql\MySQL语句指南.assets\image-20210415174808346.png)





#### 表明大小写模式

```mysql
show variables like '%lower_case_table_name%';
```

#### SQL_MODE

```mysql
select @@session.sql_mode;
```

临时设置session mysqld设置永久的配置



### 分组topN

- 按 每一天的，`job_id` 下按`elapsed_time` 取top3

```sql
SELECT id
	,`job_id`
	,elapsed_time
FROM (
	SELECT id
		,`job_id`
		,elapsed_time
		,(
			@num: = IF (
				@group = `job_id`
				,@num + 1
				,IF (
					@group : = `job_id`
					,1
					,1
					) ) ) row_number FROM (
				SELECT *
				FROM instance_info
				WHERE actual_trigger_time >= '2021-11-21 00:00:01'
					AND actual_trigger_time <= '2021-11-21 23:59:59'
				) t CROSS JOIN (
				SELECT @num: = 0
					,@group: = NULL
				) c ORDER BY `job_id`
				,elapsed_time DESC
				,id ) AS x WHERE x.row_number <= 3;
				)
			)
	)
	
	等价于
	
	SELECT id,
          ROW_NUMBER() over(partition by  job_id   order by     elapsed_time DESC) RowNum
   FROM instance_info_1201
	
```





#### 递归查询根节点

```sql
SELECT
	* 
FROM
	(
	SELECT
		@r AS _id,
		( SELECT @r := Pre_Action_ID FROM action_relation WHERE Action_ID = _id ) AS Pre_Action_ID,
		@l := @l + 1 AS lvl 
	FROM
		( SELECT @r := 24001, @l := 0 ) vars,
		action_relation m 
	WHERE
		@r <> 0 
	) a 
WHERE
	a.Pre_Action_ID IS NOT NULL
	
```





#### 删除原来的索引，让起始值从1开始

```mysql
alter table system_app_info drop id; -- 删除原来的主键值
alter table system_app_info add id int not null primary key auto_increment first;
```

- 或者通过如下的方式：

```mysql
truncate table test;
--然后navicat设计表中改小索引
```















### Reference

- [安全快速修改Mysql数据库名的5种方法](https://m.jb51.net/article/49293.htm)
- [mysql服务启动、停止、重启](https://www.cnblogs.com/lhj588/p/3268614.html)
- [mysql show variables sql_mode_详解mysql的sql_mode模式](https://blog.csdn.net/weixin_33582311/article/details/113299848)
- [低版本mysql 利用@变量实现row_number() over(partition by order by )排序功能](https://blog.csdn.net/shammy_feng/article/details/112308170)
- [mysql拆分字符串作为查询条件](https://www.cnblogs.com/sunankang/p/16445918.html)
- [【MySQL精通】七、MySQL创建用户、赋予权限](https://zhuanlan.zhihu.com/p/656667425)
- [MySQL8.0 ERROR 1410 (42000): You are not allowed to create a user with GRANT](https://blog.csdn.net/gmaaa123/article/details/127690504)
- [Linux下MySQL服务状态查看的几种办法](https://zhuanlan.zhihu.com/p/668438225)





