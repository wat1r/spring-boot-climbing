## MySQL语句指南

### Meta表

#### 查询表的字段

```mysql
SELECT
    CONCAT(COLUMN_NAME,"," )
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



#### 重命名数据库名称

#####  **mysqldump导出数据再导入**

```mysql
mysqldump -uxxxx -pxxxx -h xxxx db_name > db_name_dump.SQL
mysql -uxxxx -pxxxx -h xxxx -e “CREATE DATABASE new_db_name”
mysql -uxxxx -pxxxx -h xxxx new_db_name < db_name_dump.SQL
mysql -uxxxx -pxxxx -h xxxx -e “DROP DATABASE db_name”
```







### Reference

- [安全快速修改Mysql数据库名的5种方法](https://m.jb51.net/article/49293.htm)
- 



