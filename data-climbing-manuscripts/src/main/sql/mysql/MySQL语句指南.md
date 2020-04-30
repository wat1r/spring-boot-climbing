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





