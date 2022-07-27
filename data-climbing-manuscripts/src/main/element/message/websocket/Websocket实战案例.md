

### js

```javascript
VUE_APP_WEBSOCKET_URL = "wss://xxx.com:18171/flow/websocket"  

// ws通信
  createStompClient(context: any, item: any){
    const env = process.env.VUE_APP_ENV
    const url = process.env.VUE_APP_WEBSOCKET_URL || ''
    const socket = new WebSocket(url);//配置ws wss协议时使用
    // const socket = new SockJS(url);//配置 http https时使用
    const options = {
        debug: true,
    };
    console.log('socket----------',socket);
    // env == 'prod' ? options.debug = false : options.debug = true
    const stompClient = Stomp.over(socket, options);

    const on_connect = function () {
      console.log('connected----------');
        //     //订阅
      stompClient.subscribe("/user/"+item.apolloid+"/write", message => {
          // const msg = JSON.parse(message.body);
          const msg = message.body
          console.log('msg========',message)
          context.commit(types.SET_STOMPCLIENT, msg);
      });
      keep_alive();
    };
    let timerId:any ;
    const on_error = function () {
      console.log('on_error----------');
        // router.push("/403");
        cancel_keep_alive();
    };
    const keep_alive = function () {
      console.log('keep_alive----------');
      const timeout = 50000;
      if (socket.readyState == socket.OPEN) {
        socket.send('RE_CONNECT');
      }
      timerId = setTimeout(keep_alive, timeout);
    };
    const cancel_keep_alive = function () {
      if (timerId) {
        clearTimeout(timerId);
      }
    };
    stompClient.connect('glog_flow_paltform', 'glog_flow_paltform', on_connect, on_error, 'glog_flow_paltform');

  }
```



### nginx

因为该域名为https域名，使用了wss协议，配置了如下的nginx：

```properties
        location /flow {
                                    #root   html;
                                    #index  index.html index.htm;
                                    proxy_pass        http://glog-server-https-dev/flow;
                                    proxy_set_header   Host             $host:$server_port;
                                    proxy_set_header   X-Real-IP        $remote_addr;
                                    proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
                                    client_max_body_size 500m;
                                    proxy_http_version 1.1;
                                    proxy_set_header Upgrade $http_upgrade;
                                    proxy_set_header Connection "upgrade";
				
        }
```

- 详细配置建有道云笔记nginx.conf







### backend

```java
import com.shengqugames.dw.glog.flow.platform.service.MessageService;
import com.shengqugames.dw.glog.flow.platform.vo.ws.Message;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "TestController", tags = {"TEST_WS"})
@RestController
@Slf4j
public class TestController {
    @Autowired
    public SimpMessagingTemplate template;
    @Autowired
    private MessageService messageService;

    /**
     * 广播
     *
     * @param msg
     */
    @ResponseBody
    @PostMapping("/pushToAll")
    public void subscribe(@RequestBody Message msg) {
        template.convertAndSend("/topic/all", msg.getContent());
        log.info("get msg :{}", msg.getContent());
    }

    /**
     * 点对点
     */
    @ResponseBody
    @PostMapping("/pushToOne")
    public void queue(@RequestBody Message msg) {
        /*使用convertAndSendToUser方法，第一个参数为用户id，此时js中的订阅地址为
        "/user/" + 用户Id + "/message",其中"/user"是固定的*/
        template.convertAndSendToUser(msg.getTo(), "/write", msg.getContent());
        log.info("get msg :{}", msg.getContent());
    }


    @ResponseBody
    @PostMapping("/push")
    public void push(@RequestBody Message msg) {
        messageService.convertAndSendToUser(msg);
        log.info("get msg :{}", msg.getContent());
    }


}

```

### config

```java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private StompUserInterceptor stompUserInterceptor;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //topic用来广播，user用来实现点对点
        config.enableSimpleBroker("/topic", "/queue","/user");
        config.setApplicationDestinationPrefixes("/app");
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        config.setUserDestinationPrefix("/user");
        config.setPreservePublishOrder(true);
    }

    /**
     * 开放节点
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册两个STOMP的endpoint，分别用于广播和点对点
        //广播
        registry.addEndpoint("/websocket").setAllowedOrigins("*");
//                .withSockJS();
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompUserInterceptor).taskExecutor().corePoolSize(4).maxPoolSize(16)
                .keepAliveSeconds(600);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(128 * 1024 * 1024)
                .setMessageSizeLimit(10 * 1024 * 1024);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(16).keepAliveSeconds(600);
    }


    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // ws 传输数据的时候，数据过大有时候会接收不到，所以在此处设置bufferSize
        container.setMaxTextMessageBufferSize(512 * 1000);
        container.setMaxBinaryMessageBufferSize(512 * 1000);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }


}
```



```java

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.LinkedHashSet;
import java.util.Map;

@Component
@Slf4j
public class StompUserInterceptor implements ChannelInterceptor {


    final String base = "%s000000";

    public static Map<String, LinkedHashSet<String>> existedLoginMap = null;
    public static Map<String, String> lastestLoginRecordMap = null;


    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        String username = (String) accessor.getSessionAttributes().get(SessionConsts.USER_NAME);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

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


    @PreDestroy
    public void destroy() {
        //系统运行结束
    }


}
```











### pom

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
```



### Reference

- [Nginx配置WebSocket 【支持wss与ws连接】](https://blog.csdn.net/qq_35808136/article/details/89677749)

- [Springboot 整合 WebSocket ，使用STOMP协议 ，前后端整合实战 （一）](https://blog.csdn.net/qq_35387940/article/details/119817167)



