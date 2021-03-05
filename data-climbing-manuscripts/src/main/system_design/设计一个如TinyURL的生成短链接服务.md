# 设计一个如TinyURL的生成短链接服务

> ### **Designing a URL Shortening service like TinyURL** 
>
> **Difficulty Level:Easy**

让我们设计一个像TinyURL这样的生成URL短链的服务。此服务将提供重定向到长URL链接的短别名。类似服务：bit.ly, goo.gl, qlink.me等

## 1.为什么需要URL短链？

URL缩短用于为长URL创建较短的别名。我们称这些缩短的别名为“短链接”。当用户点击这些短链接时，会重定向到原始URL。短链接在显示、打印、发送消息或推特时可节省大量空间。此外，用户不太可能错误键入较短的URL。

例如，如果我们通过TinyURL缩短此页：

https://www.educative.io/collection/page/5668639101419520/564905025344512/5668600916475904/

我们会得到：

http://tinyurl.com/jlg8zpc

缩短的URL几乎是实际URL大小的三分之一。

URL缩短用于优化跨设备的链接，跟踪单个链接以分析受众和活动性能，以及隐藏附属的原始URL。

如果你没用过tinyurl.com网站在此之前，请尝试创建一个新的缩短的网址，并花一些时间浏览他们的服务提供的各种选项。这对你理解这一章有很大帮助。