## MySQL语句指南

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











#### 查询建表时的语句

```sql
SHOW CREATE TABLE TEST
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

#### 更新某个字段的值

```sql
UPDATE db.functions 
SET func_method = func_name
WHERE
	func_method IS NULL 
	AND engine_type = 'presto'
```









# 











### Reference

- [安全快速修改Mysql数据库名的5种方法](https://m.jb51.net/article/49293.htm)

- [mysql服务启动、停止、重启](https://www.cnblogs.com/lhj588/p/3268614.html)



