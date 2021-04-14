

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





#### 强联通分量示例

以下 Cypher 语句创建了一个 Twitter 式样的图，其中包含了用户、用户之间的 `FOLLOW` 关系。

```properties
MERGE (nAlice:User {id:"Alice"})
MERGE (nBridget:User {id:"Bridget"})
MERGE (nCharles:User {id:"Charles"})
MERGE (nDoug:User {id:"Doug"})
MERGE (nMark:User {id:"Mark"})
MERGE (nMichael:User {id:"Michael"})
MERGE (nAlice)-[:FOLLOWS]->(nBridget)
MERGE (nAlice)-[:FOLLOWS]->(nCharles)
MERGE (nMark)-[:FOLLOWS]->(nDoug)
MERGE (nMark)-[:FOLLOWS]->(nMichael)
MERGE (nBridget)-[:FOLLOWS]->(nMichael)
MERGE (nDoug)-[:FOLLOWS]->(nMark)
MERGE (nMichael)-[:FOLLOWS]->(nAlice)
MERGE (nAlice)-[:FOLLOWS]->(nMichael)
MERGE (nBridget)-[:FOLLOWS]->(nAlice)
MERGE (nMichael)-[:FOLLOWS]->(nBridget);
```

查询节点以及节点关系

```properties
MATCH (user1:User)-[r:FOLLOWS]-(user2:User)
RETURN user1,r,user2
DELETE user1,user2,r
```

![image-20210414115332770](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\neo4j\Neo4j操作教程.assets\image-20210414115332770.png)



查询两个之间的互相链接

```shell
CALL algo.scc.stream("User","FOLLOWS")
YIELD nodeId, partition
MATCH (u:User) WHERE id(u) = nodeId
RETURN u.id AS name, partition

CALL gds.betweenness.stream("User","FOLLOWS")
YIELD nodeId, partition
MATCH (u:User) WHERE id(u) = nodeId
RETURN u.id AS name, partition


CALL gds.graph.create(
  'myGraph',
  'User',
  'FOLLOWS',
)


新版
---
CALL gds.wcc.stream('myGraph')
YIELD nodeId, componentId
RETURN gds.util.asNode(nodeId).name AS name, componentId
ORDER BY componentId, name
```

![image-20210414120206851](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\neo4j\Neo4j操作教程.assets\image-20210414120206851.png)

![image-20210414120217835](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\scheduler\neo4j\Neo4j操作教程.assets\image-20210414120217835.png)

> 报错: 老的algo已经不维护了，迁移到gds了，新的用gds

```properties
dbms.security.procedures.whitelist=apoc.*
dbms.security.procedures.whitelist=gds.*

dbms.security.procedures.unrestricted=algo.*
dbms.security.procedures.unrestricted=apoc.*
dbms.security.procedures.unrestricted=gds.*
```







## Reference

- [windows下neo4j安装及批量导入数据](https://blog.csdn.net/weixin_43927437/article/details/105149951)
- [使用neo4j-import导入数据及关系](https://www.cnblogs.com/jpfss/p/11289669.html)
- [neo4j批量导入neo4j-import](https://www.cnblogs.com/jpfss/p/11289745.html)
- [neo4j数据库中合并相同节点](https://blog.csdn.net/likeyou1314918273/article/details/105946179\)
- [Preprocessing functions and procedures](https://neo4j.com/docs/graph-algorithms/current/labs-algorithms/preprocessing/)
- http://codingisforeveryone.com.au/wp-content/uploads/2018/05/Neo4j%E9%AB%98%E7%BA%A7%E5%BA%94%E7%94%A8%E6%8A%80%E6%9C%AF%E7%B3%BB%E5%88%97-APOC-2-Expand-Procedures-v1.pdf

- https://neo4j.com/download-center/#algorithms

- https://neo4j.com/docs/graph-data-science/current/
- https://blog.csdn.net/GraphWay
- https://github.com/neo4j-contrib/neo4j-graph-algorithms
- https://neo4j.com/docs/graph-data-science/current/algorithms/wcc/#algorithms-wcc-examples-stream