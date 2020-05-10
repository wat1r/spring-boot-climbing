## 阿里云ECS服务器安装Elasticsearch教程

### 1.安装基础版本的Elasticsearch

#### 版本

```shell
elasticsearch  6.8.6版本
```



#### 安装

- 下载

```shell
tar -zxvf  elasticsearch-6.8.6.tar.gz -C /usr/local/
```

- 由于不能使用root用户，创建elastic:elastic的用户组与用户

```shell
[root@centos7 local]# groupadd elastic 
[root@centos7 local]# useradd -g elastic elastic 
[root@centos7 local]# passwd elastic
China@666
[root@centos7 local]# vim /etc/security/limits.conf 
elastic soft nofile 65536
elastic hard nofile 65536
[root@centos7 local]# vim /etc/sysctl.conf
vm.max_map_count=262144
[root@centos7 local]#  sysctl -p
```



配置文件`elasticsearch.yml `

```shell
[root@centos7 config]# pwd
/usr/local/elasticsearch/config
[root@centos7 config]# vim elasticsearch.yml 
```

```shell
#这是集群名字，起名为elasticsearch
##es启动后会将具有相同集群名字的节点放到一个集群下。
cluster.name:  elasticsearch
##
##节点名字。
node.name: "node1"
##
#
#/usr/local/elasticsearch/config
## 数据存储位置，配置之后该目录会自动生成
path.data: /usr/local/elasticsearch/data
##
## 日志文件的路径，配置之后该目录会自动生成
path.logs: /usr/local/elasticsearch/logs
##
##
##设置绑定的ip地址,可以是ipv4或ipv6的,默认为0.0.0.0
##network.bind_host: xxxxxx
##
##设置其它节点和该节点交互的ip地址,如果不设置它会自动设置,值必须是个真实的ip地址
##network.publish_host: xxxxxx
##
##同时设置bind_host和publish_host上面两个参数，该地址为默认地址
network.host: 0.0.0.0
##
##
## 设置节点间交互的tcp端口,默认是9300
##transport.tcp.port: 9300
##
## 设置是否压缩tcp传输时的数据，默认为false,不压缩
transport.tcp.compress: true
##
## 设置对外服务的http端口,默认为9200
http.port: 9200
##
## 使用http协议对外提供服务,默认为true,开启
##http.enabled: false
##
##discovery.zen.ping.unicast.hosts:["节点1的 ip","节点2 的ip","节点3的ip"]
##这是一个集群中的主节点的初始列表,当节点(主节点或者数据节点)启动时使用这个列表进行探测
##
##指定集群中的节点中有几个有master资格的节点。
##对于大集群可以写(2-4)。
#discovery.zen.minimum_master_nodes: 1
##解决head的集群健康值问题，后续会安装head插件
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization,X-Requested-With,Content-Length,Content-Type

```

- 启动执行 -d为后台执行

```shell
[root@centos7 bin]# pwd
/usr/local/elasticsearch/bin
[root@centos7 bin]# ./elasticsearch -d 
```

```verilog
[2020-05-10T19:41:53,831][INFO ][o.e.n.Node               ] [node1] started
[2020-05-10T19:41:53,875][WARN ][o.e.x.s.a.s.m.NativeRoleMappingStore] [node1] Failed to clear cache for realms [[]]
[2020-05-10T19:41:53,977][INFO ][o.e.g.GatewayService     ] [node1] recovered [0] indices into cluster_state
[2020-05-10T19:41:54,320][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.triggered_watches] for index patterns [.triggered_watches*]
[2020-05-10T19:41:54,519][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.watch-history-9] for index patterns [.watcher-history-9*]
[2020-05-10T19:41:54,601][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.watches] for index patterns [.watches*]
[2020-05-10T19:41:54,697][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.monitoring-logstash] for index patterns [.monitoring-logstash-6-*]
[2020-05-10T19:41:54,818][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.monitoring-es] for index patterns [.monitoring-es-6-*]
[2020-05-10T19:41:54,922][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.monitoring-beats] for index patterns [.monitoring-beats-6-*]
[2020-05-10T19:41:55,021][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.monitoring-alerts] for index patterns [.monitoring-alerts-6]
[2020-05-10T19:41:55,098][INFO ][o.e.c.m.MetaDataIndexTemplateService] [node1] adding template [.monitoring-kibana] for index patterns [.monitoring-kibana-6-*]
[2020-05-10T19:41:55,404][INFO ][o.e.l.LicenseService     ] [node1] license [1d03326f-3e70-44cd-8beb-f6bc0fc60bb9] mode [basic] - valid

```

- 测试

```shell
 curl http://localhost:9200/?pretty
```

- 配置ECS的安全组 添加 入方向 9200 9300 端口
- 公网ip外网访问，加载不出来，执行如下命令

```shell
iptables -I INPUT -p tcp --dport 9200 -j ACCEPT
```

![image-20200510205633667](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\install\aliyun\阿里云ECS服务器安装Elasticsearch教程.assets\image-20200510205633667.png)

### 2.安装Head插件

- 下载head插件

```shell
[root@centos7 software]# wget  https://codeload.github.com/mobz/elasticsearch-head/zip/master 
```









### Reference

- https://www.cnblogs.com/yijialong/p/9707238.html

