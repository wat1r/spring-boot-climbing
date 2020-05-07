## WebScoket的核心原理

### 1.WebSocket 与 HTTP

WebSocket 的最大特点就是，服务器可以主动向客户端推送信息，客户端也可以主动向服务器发送信息，是真正的双向平等对话。
HTTP 有 1.1 和 1.0 之说，也就是所谓的 keep-alive ，把多个 HTTP 请求合并为一个，但是 Websocket 其实是一个新协议，跟 HTTP 协议基本没有关系，只是为了兼容现有浏览器，所以在握手阶段使用了 HTTP 。



![image-20200507201205815](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\message\websocket\WebScoket的核心原理.assets\image-20200507201205815.png)

https://www.zhihu.com/question/20215561

https://www.cnblogs.com/nnngu/p/9347635.html