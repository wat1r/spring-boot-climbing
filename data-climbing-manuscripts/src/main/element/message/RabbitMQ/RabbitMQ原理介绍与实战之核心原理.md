$

## `RabbitMQ`原理介绍与实战之核心原理

### 0.前言

`RabbitMQ`是基于`(AMQP)Advanced Message Queuing Protocol `即 高级消息队列协议 

AMQP定义:是具有现代特征的二进制协议。是一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。

Erlang语言最初在于交换机领域的架构模式，这样使得RabbitMQ在Broker之间进行数据交互的性能是非常优秀的
Erlang的优点: Erlang有着和原生Socket一样的延迟。

### 1.核心概念

#### 1.1.模块类型

- Server: 又称Broker,接受客户端的连接，实现AMQP实体服务
- Connection: 连接，应用程序与Broker的网络连接
- Channel: 网络信道，几乎所有的操作都在Channel中进行，Channel是进行消息读写的通道。客户端可建立多个Channel，每个Channel代表一个会话任务。
- Message: 消息，服务器和应用程序直接传送的数据，由Properties和Body组成。Properties可以对消息进行修饰，比如消息的优先级、延迟等高级特性；Body则就是消息体内容。
- Virtual Host: 虚拟主机，用户进行逻辑隔离，最上层的消息路由。一个Virtual Host里面可以有若干个Exchange和Queue，同一个Virtual Host里面不能有相同名称的Exchange或Queue
- Exchange: 交换机，接收消息，根据路由键转发消息到绑定的队列， 生产者将消息发送到Exchange，由Exchange将消息路由到一个或多个Queue中（或者丢弃） 。

![1587944887720](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587944887720.png)

- Binding: Exchange和Queue之间的虚拟连接，binding中可以包含routing key
- Routing key: 一个路由规则，虚拟机可用它来确定如何路由一个特定消息
- Queue: 也称为Message Queue，消息队列，保存消息并将它们转发给消费者

#### 1.2.消息分发机制

RabbitMQ中通过Exchange交换器将消息分发到队列，根据不同的Exchange类型，实行不同的分发策略

- fanout：会把消息路由到所有与该交换器绑定的队列中，并忽略RoutingKey。
![1587946702030](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587946702030.png)

- direct：会把消息路由到绑定时bindingKey与消息routingKey完全相等的队列中。
  - 单个绑定direct类型
![1587945139569](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587945139569.png)
  - 多个绑定direct类型
![1587945330457](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587945330457.png)
  - 一个queue上绑定多个direct类型
  

![1587945755223](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587945755223.png)


- topic：与dircet类似，但是可以使用通配符进行模糊匹配。.作为单词分隔符，*匹配单个单词，#匹配所有内容

  	- outing key为一个句点号“. ”分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如“stock.usd.nyse”、“nyse.vmw”、“quick.orange.rabbit”
    - binding key与routing key一样也是句点号“. ”分隔的字符串
    - binding key中可以存在两种特殊字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
  ![1587946307662](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1587946307662.png)
  - 以上图中的配置为例，routingKey=”quick.orange.rabbit”的消息会同时路由到Q1与Q2，routingKey=”lazy.orange.fox”的消息会路由到Q1，routingKey=”lazy.brown.fox”的消息会路由到Q2，routingKey=”lazy.pink.rabbit”的消息会路由到Q2（只会投递给Q2一次，虽然这个routingKey与Q2的两个bindingKey都匹配）；routingKey=”quick.brown.fox”、routingKey=”orange”、routingKey=”quick.orange.male.rabbit”的消息将会被丢弃，因为它们没有匹配任何bindingKey
- header：不依赖于RoutingKey，而是根据消息的headers属性进行匹配。绑定时不再指定bindingKey，而是指定arguments。arguments是一个Map，如果arguments里面有a=1的key/value，消息的headers也需要有a=1才能被路由。

#### 1.3.交互机制

Producer生产消息，创建Exchange，Exchange转发消息，但是不做存储，Consumer要想消费消息的话，需要创建Queue来bind到指定的Exchange上，然后Exchange会发送消息到Queue里，Consumer通过Pull或者Subscribe的方式老消费消息。

![1588947998471](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\RabbitMQ\RabbitMQ原理介绍与实战之核心原理.assets\1588947998471.png)





### 2.部署

#### 2.1.端口

- 4369：epmd，RabbitMQ节点和CLI工具使用的对等发现服务
- 5672、5671：由不带TLS和带TLS的AMQP 0-9-1和1.0客户端使用
- 25672：用于节点间和CLI工具通信（Erlang分发服务器端口），并从动态范围分配（默认情况下限制为单个端口，计算为AMQP端口+ 20000）。除非确实需要这些端口上的外部连接（例如，群集使用联合身份验证或在子网外部的计算机上使用CLI工具），否则这些端口不应公开。有关详细信息，请参见网络指南。
- 35672-35682：由CLI工具（Erlang分发客户端端口）用于与节点进行通信，并从动态范围（计算为服务器分发端口+ 10000通过服务器分发端口+ 10010）分配。有关详细信息，请参见网络指南。
- 15672：HTTP API客户端，管理UI和Rabbitmqadmin （仅在启用了管理插件的情况下）
- 61613、61614：不带TLS和带TLS的STOMP客户端（仅在启用STOMP插件的情况下）
- 1883、8883 ：（不带和带有TLS的MQTT客户端，如果启用了MQTT插件
- 15674：STOMP-over-WebSockets客户端（仅在启用了Web STOMP插件的情况下）
- 15675：MQTT-over-WebSockets客户端（仅当启用了Web MQTT插件时）
- 15692：Prometheus指标（仅在启用Prometheus插件的情况下）

### 关联阅读

