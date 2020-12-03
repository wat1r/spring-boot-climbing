## RabbitMQ做代理中继SpringBoot集成Stomp over Websocket案例

### 0.技术栈

> SpingBoot  2.3.0.RELEASE
>
> Websocket 
>
> Stomp  
>
> RabbitMQ  3.7.14  Erlang 21.3.8

### 1.架构与流程

#### 1.1.架构

使用**代理中继-StompBrokerRelay**,通过TCP将消息传递到外部STOMP代理，以及将消息从代理传递到订阅的客户端。此外，应用程序组件（例如，HTTP请求处理方法，业务服务等）也可以向代理中继或者外部消息代理发送消息，以便向订阅的WebSocket客户端广播消息。

![image-20201203211624305](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ做中继代理SpringBoot集成Stomp over Websocket案例.assets\image-20201203211624305.png)

#### 1.2.业务流程

- 与下面很类似，图见出处

![image-20201203211839632](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ做中继代理SpringBoot集成Stomp over Websocket案例.assets\image-20201203211839632.png)



### 2.实例代码

#### 2.1.pom

```xml
<!-- RabbitMQ-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency
<!-- websocket 相关依赖 -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
  <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-reactor-netty</artifactId>
</dependency>

<!-- 下面这些是处理报错的，tcp nio链接的时候报错 -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty</artifactId>
    <version>0.8.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.0.33.Final</version>
</dependency>
    
```

#### 2.2.RabbitMQConfig

```java
@Configuration
@EnableRabbit
public class RabbitMQConfig {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    public RabbitTemplate rabbitTemplate;
    public CachingConnectionFactory cf;


    //绑定键
    public final static String LOCK_NOTICE = "lock-notice"; 
    public final static String CONSOLE_NOTICE = "console-notice"; 
    public final static String TOPIC_EXCHANGE = "topic-exchange"; //TOPIC_EXCHANGE

	//处理消息返回
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        /* RabbitMQ多节点写法（主主配置） */
        cf = new CachingConnectionFactory();
        cf.setAddresses(rabbitMQProperties.getAddresses());
        cf.setVirtualHost(rabbitMQProperties.getVirtualHost());
        cf.setUsername(rabbitMQProperties.getUsername());
        cf.setPassword(rabbitMQProperties.getPassword());
        rabbitTemplate = new RabbitTemplate(cf);
        rabbitTemplate.setMessageConverter(new 						           					Jackson2JsonMessageConverter(objectMapper));
        /* 此写法是RabbitMQ的单节点写法，不适用于本案例 */
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }


    @Bean
    public Queue lockNoticeQueue() {
        Queue queue = new Queue(RabbitMQConfig.LOCK_NOTICE);
        amqpAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Queue consoleNoticeQueue() {
        return new Queue(RabbitMQConfig.CONSOLE_NOTICE);
    }

    @Bean
    public TopicExchange topicExchange() {
        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE);
        amqpAdmin.declareExchange(topicExchange);
        return topicExchange;
    }

    @Bean
    Binding bindingLockNoticeQueue() {
        return BindingBuilder.bind(lockNoticeQueue()).to(topicExchange()).with(LOCK_NOTICE);
    }


    @Bean
    Binding bindingConsoleNoticeQueue() {
        return BindingBuilder.bind(consoleNoticeQueue()).to(topicExchange()).with(CONSOLE_NOTICE);
    }




    /**
     * 动态创建队列并加入监听 ，适配业务时根据id动态创建queue的写法，本案例不涉及
     *
     * @param queueName
     * @throws IOException
     */

    public void createQueue(String queueName) throws IOException {
        Connection conn = cf.createConnection();
        Channel channel = conn.createChannel(false);
        channel.queueDeclare(queueName, true, false, false, null);
//        //声明交换机
//        channel.exchangeDeclare(rabbitmqUtil.exchangeName, "direct");
//        //绑定队列到交换机
//        channel.queueBind(queueName, topicExchange().getName(), "");
        amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName))
                .to(topicExchange()).with(queueName));
//        //将队列加入监听器
        SimpleMessageListenerContainer container = new 			SimpleMessageListenerContainer(cf);
        container.addQueueNames(queueName);
    }
}

```

#### 2.3.WebSocketConfig

- `@EnableWebSocketMessageBroker`在启用的时候，会涉及到一部分的报错，大体与`jar`的版本冲突有关，见`pom`文件的`netty`相关

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	//
    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;
    //涉及到定向发送`/user`的配置，princple信息需要统一
    @Autowired
    private StompUserInterceptor stompUserInterceptor;
    @Autowired
    private RabbitMQProperties rabbitMQProperties;


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*")
                .addInterceptors(authHandshakeInterceptor)
                .withSockJS();

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompUserInterceptor).taskExecutor().corePoolSize(4).maxPoolSize(16)
                .keepAliveSeconds(600);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
       registration.taskExecutor().corePoolSize(4).maxPoolSize(16).keepAliveSeconds(600);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//       enableSimpleBroker 服务端推送给客户端的路径前缀 ,此处使用了RabbitMQ的消息中继代理，用不上这个function
//        registry.enableSimpleBroker("/topic", "/queue");
        registry.enableStompBrokerRelay("/exchange", "/queue", "/topic")
                .setRelayHost(rabbitMQProperties.getHost())
                .setRelayPort(rabbitMQProperties.getStomp().getPort())
                .setClientLogin(rabbitMQProperties.getUsername())
                .setClientPasscode(rabbitMQProperties.getPassword())
                .setSystemLogin(rabbitMQProperties.getUsername())
                .setSystemPasscode(rabbitMQProperties.getPassword())
                .setVirtualHost(rabbitMQProperties.getVirtualHost())
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
//        registry.setApplicationDestinationPrefixes("/app");
    }

}
```

关于API的一些解释：

- enableStompBrokerRelay()：开启外部消息代理并指定代理前缀，前缀是代理指定的而非自定义。**使用rabbitmq作为消息代理合法的代理前缀有：/temp-queue, /exchange, /topic, /queue, /amq/queue, /reply-queue/**.
- setRelayHost：设置消息代理主机地址，默认:127.0.0.1
- setRelayPort：设置消息代理端口号，默认61613
- setClientLogin/setClientPasscode：设置客户端连接到消息代理的用户名/密码，默认guest/guest
- setSystemLogin/setSystemPasscode：设置客户端连接到消息代理的用户名/密码，默认guest/guest
- setUserRegistryBroadcast：当有客户端注册时将其广播到其他服务器并指定pub-sub目的地
- setUserDestinationBroadcast：将当前服务端点无法发送到user dest的消息广播到其他服务端点处理
- setSystemHeartbeatReceiveInterval：配置服务端websocket会话接收stomp消息代理心跳时间间隔(0代表不接收)
- setSystemHeartbeatSendInterval：配置服务端websocket会话向stomp消息代理发送心跳时间间隔(0代表不接收)
- setApplicationDestinationPrefixes：设置路由到@MessageMapping等注解方法的控制层的消息前缀
- setUserDestinationPrefix：设置点对点消息前缀

#### 2.4.application-dev.yml

- `ip-1` 与`ip-2`  `RabbitMQ`的多节点的部署，配合`RabbitMQConfig`的连接方式
- `ip-3`是做了一层`Nginx`的代理，因为`Web`端`Stomp`只能单节点连接，做了一个统一的出口，`Server`端也做了同样的配置，内外网`ip`的区别
- `port: 61613` 利用`RabbitMQ`为中继代理的端口，不写的话默认也是`61613`

```yaml
spring:
  # RabbitMQ
  rabbitmq:
    virtualHost: test-vhost
    addresses: ip-1:5672,ip-:5672
    username: guest
    password: guest
    host: ip-3
    stomp:
      port: 61613
```

#### 2.5.RabbitMQProperties

```java
@Data
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQProperties {
    private String virtualHost;
    private String addresses;
    private String username;
    private String password;
    private String host;
    private Port stomp = new Port();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Port {
        private Integer port;
    }
}
```

#### 2.6.StompUserInterceptor

```java
@Component
@Slf4j
public class StompUserInterceptor implements ChannelInterceptor {
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String userid = (String) accessor.getSessionAttributes().get(SessionConsts.USERID);
        String username = (String) accessor.getSessionAttributes().get(SessionConsts.USER_NAME);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            StompUser user = new StompUser();
            user.setUsername(userid);
            user.setUsername(username);
            accessor.setUser(user);
            log.info(String.format("goodbye, %d , %s" + userid, username));
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info(String.format("goodbye, %d , %s" + userid, username));
        }
        return message;
    }

    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    }

    public void afterSendCompletion(
            Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
    }

    public boolean preReceive(MessageChannel channel) {
        return true;
    }

    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return message;
    }

    public void afterReceiveCompletion(Message<?> message, MessageChannel channel,
                                       Exception ex) {
    }
}
```

#### 2.7.AuthHandshakeInterceptor

```java
@Component
@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        HttpSession session = null;
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            session = serverRequest.getServletRequest().getSession(false);
        }
        if (session == null || session.getAttribute(SessionConsts.USER_NAME) == null) {
            log.error("未登录无法进行Websocket连接");
            throw new Exception("先登录");
        }
        String USER_NAME = String.valueOf(session.getAttribute(SessionConsts.USER_NAME));
        attributes.put(SessionConsts.USER_NAME, USER_NAME);
        attributes.put(SessionConsts.RESPONSE, response);
        log.info("websocket handshake, ", USER_NAME);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }
}

```

#### 2.8.Web

```javascript
  //ip-4是nginx做的出口ip，15674是 Stomp Over Websocket的默认端口 和ip-3内外网
  VUE_APP_WEBSOCKET_URL = "ws://ip-4:15674/ws"
  let socket = new WebSocket(process.env.VUE_APP_WEBSOCKET_URL);
  let stompClient = Stomp.over(socket);
      var on_connect = function () {
        console.log('connected');
        let queueName = "/exchange/topic-exchange/lock-notice";
        stompClient.subscribe(queueName, message => {
            console.log("stompClient----msg...", message);
        });
    };
    var on_error = function (e) {
        console.log('error');
        console.log('websocket 断开: ' + e.code + ' ' + e.reason + ' ' + e.wasClean)
    };
    stompClient.connect('guest', 'guest', on_connect, on_error, 'test-vhost');
```

### 3.Q&A

#### 3.1.Lost Connection

![image-20201203205026682](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ做中继代理SpringBoot集成Stomp over Websocket案例.assets\image-20201203205026682.png)



![image-20201203205126815](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ做中继代理SpringBoot集成Stomp over Websocket案例.assets\image-20201203205126815.png)

`code`是1000，正常退出，查阅了些文档，去RabbitMQ的控制台查看（http://ip-1:15672）,`queue`是正常的

![image-20201203210055673](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\rabbitmq\RabbitMQ做中继代理SpringBoot集成Stomp over Websocket案例.assets\image-20201203210055673.png)

**使用rabbitmq作为消息代理合法的代理前缀有：/temp-queue, /exchange, /topic, /queue, /amq/queue, /reply-queue/**，这些head固定写法

**/exchange/[exchangename]/[routing_key]**

通过交换机订阅/发布消息，交换机需要手动创建，也可以使用rabbitmq默认的几个交换机，参数说明：
a. /exchange：固定值
b. exchangename：交换机名称
c. routing_key：路由键[可选]，可以是"/exchange/[exchangename]/"不能省略末尾"/"，这样路由键为空串

对于接收者端，订阅该destination会创建一个唯一的、自动删除的随机queue，并根据routing_key将该 queue 绑定到所给的 exchangename，实现对该队列的消息订阅。
对于发送者端，消息就会被发送到定义的 exchangename中，并且指定了 routing_key。

#### 附：

 RabbitMQ 四种目的地用法

#####  【1】 /queue/queuename

 使用默认交换机订阅/发布消息，默认由stomp自动创建一个持久化队列，参数说明
 a. /queue：固定值
 b. queuename：自动创建一个持久化队列
 对于接收者端，订阅队列queuename的消息
 对于发送者端，向queuename发送消息
 [对于 SEND frame，destination 只会在第一次发送消息的时候会定义的共享 queue]

#####  【2】 /amq/queue/queuename

 和上文的”/queue/queuename”相似，区别在于队列不由stomp自动进行创建，队列不存在失败
 这种情况下无论是发送者还是接收者都不会产生队列。 但如果该队列不存在，接收者会报错。

 以上两种为队列用法 若打开两个接收页面 则发送的消息会被两个页面轮流接收

#####  【3】 /exchange/exchangename/[routing_key]

 通过交换机订阅/发布消息，交换机需要手动创建，参数说明
 a. /exchange：固定值
 b. exchangename：交换机名称
 c. [routing_key]：路由键，可选
 对于接收者端，该 destination 会创建一个唯一的、自动删除的随机queue， 并根据 routing_key将该 queue 绑定到所给的 exchangename，实现对该队列的消息订阅。
 对于发送者端，消息就会被发送到定义的 exchangename中，并且指定了 routing_key。

#####  【4】 /topic/routing_key

 通过amq.topic交换机订阅/发布消息，订阅时默认创建一个临时队列，通过routing_key与topic进行绑定
 a. /topic：固定前缀
 b. routing_key：路由键
 对于接收者端，会创建出自动删除的、非持久的队列并根据 routing_key路由键绑定到 amq.topic 交换机 上，同时实现对该队列的订阅。
 对于发送者端，消息会被发送到 amq.topic 交换机中。

#### 3.2.RabbitMQ涉及的端口

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

### 4.Reference

- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)

- [How do I use convertAndSendToUser() with an external broker such as RabbitMQ in Spring4?](https://stackoverflow.com/questions/28015942/how-do-i-use-convertandsendtouser-with-an-external-broker-such-as-rabbitmq-in)

- [Springboot 整合Websocket+Stomp协议+RabbitMQ做消息代理 实例教程](https://blog.csdn.net/qq_35387940/article/details/108276136)
- [SpringBoot 整合WebSocket 简单实战案例](https://blog.csdn.net/qq_35387940/article/details/93483678)

- [CloseEvent](https://developer.mozilla.org/zh-CN/docs/Web/API/CloseEvent)

- [如何使用Nginx作为RabbitMQ的websocket函数的反向代理？](https://www.thinbug.com/q/38655355)

- [springboot集成stomp websocket基于rabbitmq消息代理实现](https://blog.csdn.net/w47_csdn/article/details/81196078)
- [RabbitMQ系列教程（十三）Spring AMQP API详解](https://blog.csdn.net/warybee/article/details/103265699)
- [spring websocket 官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket)
- [stomp over websocket 翻译](https://segmentfault.com/a/1190000006617344)
- [stomp 协议规范翻译](https://blog.csdn.net/hinstenyhisoka/article/details/54311814)
- [stomp 协议官方文档](http://stomp.github.io/stomp-specification-1.2.html)












