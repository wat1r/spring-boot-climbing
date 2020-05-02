## SpringBoot集成Apollo配置中心

### 0.前言



> https://www.cnblogs.com/huanchupkblog/p/10509427.html



- 根据此[官方教程](https://github.com/nobodyiam/apollo-build-scripts),有几个注意点：
  
  - 当访问`localhost:8080/8070`这两个端口时，打不开网页，因为此处用到的是阿里云ecs的服务器，访问了`http://自己的阿里云ecs服务器公网ip地址:8080/8070`，访问不了，需要在ecs的控制台添加入方向的端口规则，如图
  

![2020-04-30_113554](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\implement\SpringBoot集成Apollo配置中心.assets\2020-04-30_113554.png)

- `demo.sh`中的脚本不需要修改的,如下部分，都保持`localhost`，期间启动脚本时会有一些报错，官方文档提示不需要理会

```shell
config_server_url=http://localhost:8080
admin_server_url=http://localhost:8090
eureka_service_url=$config_server_url/eureka/
portal_url=http://localhost:8070
```











