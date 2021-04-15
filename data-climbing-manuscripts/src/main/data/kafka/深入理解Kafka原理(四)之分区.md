## 深入理解Kafka原理(四)之分区





### 如何选择 `Partiton` 的数量

在创建 `topic` 的时候可以指定 `partiton` 数量，也可以在常见完后手动修改。但`partiton` 数量只能增加不能减少。中途增加`partiton`会导致各个`partition`之间数据量的不平等。
`Partition` 的数量直接决定了该 `Topic` 的并发处理能力。但也并不是越多越好。`Partition` 的数量对消息延迟性会产生影响。
一般建议选择 `broker` `num` * `consumer` `num` ，这样平均每个 `consumer` 会同时读取`broker`数目个 `partition` , 这些 `partiton` 压力可以平摊到每台 ``broker`` 上。

[]: 
[]: 

