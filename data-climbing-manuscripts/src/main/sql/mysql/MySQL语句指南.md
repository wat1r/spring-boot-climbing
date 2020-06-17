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
```











### Reference

- [安全快速修改Mysql数据库名的5种方法](https://m.jb51.net/article/49293.htm)

- [mysql服务启动、停止、重启](https://www.cnblogs.com/lhj588/p/3268614.html)



