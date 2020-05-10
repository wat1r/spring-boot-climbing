





## RabbitMQ原理介绍与实战之几种工作模式

> SpringBoot整RabbitMQ的三种模式（Direct/Topic/Fanout）

### 0.前言

#### 0.1.版本信息

- 阿里云ECS的Centos7.6系统，docker部署的RabbitMQ,启动命令:

```shell
docker run -d  --name rabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 -p 25672:25672 -p 61613:61613 -p 1883:1883 rabbitmq:management
```

#### 0.2.项目结构



#### 0.2.项目结构

```shell
.
|-- pom.xml
|-- spring-boot-rabbitmq-direct.iml
|-- src
|   |-- main
|   |   |-- java
|   |   |   `-- com
|   |   |       `-- frankcooper
|   |   |           |-- RabbitmqDemoApplication.java
|   |   |           |-- direct
|   |   |           |   |-- config
|   |   |           |   |   `-- DirectConfig.java
|   |   |           |   |-- receive
|   |   |           |   |   `-- DirectReceiver.java
|   |   |           |   `-- send
|   |   |           |       `-- DirectSender.java
|   |   |           |-- fanout
|   |   |           |   |-- config
|   |   |           |   |   `-- FanoutConfiguration.java
|   |   |           |   |-- receive
|   |   |           |   |   `-- FanoutReceiver.java
|   |   |           |   `-- send
|   |   |           |       `-- FanoutSender.java
|   |   |           `-- topic
|   |   |               |-- config
|   |   |               |   `-- TopicConfiguration.java
|   |   |               |-- receive
|   |   |               |   `-- TopicReceiver.java
|   |   |               `-- send
|   |   |                   `-- TopicSender.java
|   |   `-- resources
|   |       |-- application-direct.yml
|   |       |-- application-fanout.yml
|   |       |-- application-topic.yml
|   |       `-- application.yml
|   `-- test
|       `-- java
|           `-- com
|               `-- frankcooper
|                   |-- direct
|                   |   `-- RabbitDirectTest.java
|                   |-- fanout
|                   |   `-- RabbitFanoutTest.java
|                   `-- topic
|                       `-- RabbitTopicTest.java
`-- target
    |-- classes
    |   |-- application-direct.yml
    |   |-- application-fanout.yml
    |   |-- application-topic.yml
    |   |-- application.yml
    |   `-- com
    |       `-- frankcooper
    |           |-- RabbitmqDemoApplication.class
    |           |-- direct
    |           |   |-- config
    |           |   |   `-- DirectConfig.class
    |           |   |-- receive
    |           |   |   `-- DirectReceiver.class
    |           |   `-- send
    |           |       `-- DirectSender.class
    |           |-- fanout
    |           |   |-- config
    |           |   |   `-- FanoutConfiguration.class
    |           |   |-- receive
    |           |   |   `-- FanoutReceiver.class
    |           |   `-- send
    |           |       `-- FanoutSender.class
    |           `-- topic
    |               |-- config
    |               |   `-- TopicConfiguration.class
    |               |-- receive
    |               |   `-- TopicReceiver.class
    |               `-- send
    |                   `-- TopicSender.class
    |-- generated-sources
    |   `-- annotations
    |-- generated-test-sources
    |   `-- test-annotations
    |-- maven-archiver
    |   `-- pom.properties
    |-- maven-status
    |   `-- maven-compiler-plugin
    |       |-- compile
    |       |   `-- default-compile
    |       |       |-- createdFiles.lst
    |       |       `-- inputFiles.lst
    |       `-- testCompile
    |           `-- default-testCompile
    |               |-- createdFiles.lst
    |               `-- inputFiles.lst
    |-- spring-boot-rabbitmq-direct-1.0-SNAPSHOT.jar
    `-- test-classes
        `-- com
            `-- frankcooper
                |-- direct
                |   `-- RabbitDirectTest.class
                |-- fanout
                |   `-- RabbitFanoutTest.class
                `-- topic
                    `-- RabbitTopicTest.class

58 directories, 42 files

```

- 启动类：`RabbitmqDemoApplication.java`

#### 0.3.项目地址

- [spring-boot-rabbitmq](https://github.com/wat1r/spring-boot-climbing/tree/master/spring-boot-rabbitmq/spring-boot-rabbitmq-direct)

#### 0.4.运行流程

- 启动`RabbitmqDemoApplication.java`,提前要部署好`RabbitMQ`的服务，暴露端口
- 运行Test中的分类的`RabbitDirectTest`等文件

### 1.Direct

![image-20200510144247914](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510144247914.png)

- 发送的效果

![image-20200510144508666](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510144508666.png)

- 接收的效果

![image-20200510144449880](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510144449880.png)

### 2.Topic

- 同上

![image-20200510145244383](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510145244383.png)



![image-20200510145307465](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510145307465.png)



### 3.Fanout

```java
@Component
public class FanoutReceiver {
    @RabbitListener(queues = "fanout-queue1")
    public void receiver01(String str){
        System.out.println(str);
    }

    @RabbitListener(queues = "fanout-queue2")
    public void receiver02(String str){
        System.out.println(str);
    }
}
```



```java
@Component
public class FanoutSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String name){
        String str = "hello,"+name;
        amqpTemplate.convertAndSend("amq.fanout","",str);
    }
}
```

- 结果是所有在`amq.fanout`这个`Exchange`下的`queue`都收到了消息

![image-20200510150709155](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510150709155.png)



![image-20200510150626609](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ原理介绍与实战之几种工作模式.assets\image-20200510150626609.png)



### 关联阅读



