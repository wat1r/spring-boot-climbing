

# Neo4j操作教程



#### 启动

```shell
neo4j console
```





#### 访问

```
http://localhost:7474/browser/
```

#### 批量导入数据

数据文件 import/目录下

```properties
==> actors.csv <==
personId:ID,name,:LABEL
keanu,"Keanu Reeves",Actor
laurence,"Laurence Fishburne",Actor
carrieanne,"Carrie-Anne Moss",Actor

==> movies.csv <==
movieId:ID,title,year:int,:LABEL
tt0133093,"The Matrix",1999,Movie
tt0234215,"The Matrix Reloaded",2003,Movie;Sequel
tt0242653,"The Matrix Revolutions",2003,Movie;Sequel

==> roles.csv <==
:START_ID,role,:END_ID,:TYPE
keanu,"Neo",tt0133093,ACTED_IN
keanu,"Neo",tt0234215,ACTED_IN
keanu,"Neo",tt0242653,ACTED_IN
laurence,"Morpheus",tt0133093,ACTED_IN
laurence,"Morpheus",tt0234215,ACTED_IN
laurence,"Morpheus",tt0242653,ACTED_IN
carrieanne,"Trinity",tt0133093,ACTED_IN
carrieanne,"Trinity",tt0234215,ACTED_IN
carrieanne,"Trinity",tt0242653,ACTED_IN
```

启动

> 如果该数据库下有数据，会有覆盖报错信息，删除数据库

```shell
// --database=outofpoverty  解决默认db报错的问题

bin/neo4j-admin.bat import  --database=outofpoverty --nodes import/movies.csv \
--nodes import/actors.csv  \
--relationships import/roles.csv


 bin/neo4j-admin.bat import  --database=actionone --nodes import/action_id.csv --relationships import/action_relation.csv


```

> 坑





如下效果：

```shell
MINGW64 /d/Dev/neo4j-community-3.5.27/data/databases
$ ls
graph.db/  outofpoverty/  store_lock
```

配置conf/neo4j.conf

```properties
dbms.active_database=outofpoverty
```

效果

![image-20210413151209862](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\neo4j\Neo4j操作教程.assets\image-20210413151209862.png)



查询

```java
MATCH (item:node)
// WHERE (item.ActionId='40')
RETURN item.ActionId,item.name
```



![image-20210413164250853](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\neo4j\Neo4j操作教程.assets\image-20210413164250853.png)





## Reference

- [windows下neo4j安装及批量导入数据](https://blog.csdn.net/weixin_43927437/article/details/105149951)

- [使用neo4j-import导入数据及关系](https://www.cnblogs.com/jpfss/p/11289669.html)

- [neo4j批量导入neo4j-import](https://www.cnblogs.com/jpfss/p/11289745.html)