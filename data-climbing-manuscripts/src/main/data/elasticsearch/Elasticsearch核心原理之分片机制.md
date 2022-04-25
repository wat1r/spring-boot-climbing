- 
- 



分片分为主分片（Primary）和副本分片（Replica）。副本分片主要功能如下：

高可用性：副本分片作为数据备份，当某个主分片发生故障时，副本分片能够成为新的主分片，保证服务的可用性。
提高性能：副本分片本身也是一个功能齐全的独立的分片(所以才能够随时取代故障的主分片)，当有查询请求时，既可以在主分片中完成查询，也可以在副本分片中完成查询，当然数据添加、更新的操作只能在主分片中完成。







### Reference

- https://blog.csdn.net/mgxcool/article/details/49250341
- https://elasticsearch.cn/

- [Elasticsearch原理学习（四）分片、副本、缩容与扩容](https://www.jianshu.com/p/f14aadd31e19)

- [Elasticsearch 分片和副本策略](https://juejin.cn/post/6844903862088777736)
- [Elatsicsearch分片和副本相关知识 ](https://www.cnblogs.com/Yemilice/p/10401688.html)
- [Elasticsearch: 权威指南（2.x）](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)
- [Elasticsearch中文文档](https://learnku.com/docs/elasticsearch73/7.3)