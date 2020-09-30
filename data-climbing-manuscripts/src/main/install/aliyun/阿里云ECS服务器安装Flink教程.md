## 阿里云ECS服务器安装Flink教程



`flink-conf.yaml`:

修改下面的地址为阿里云内网地址：

`jobmanager.rpc.port`的默认6123端口，修改成6124端口，有文档说6123是ipv6的，没再确认了

```yaml
jobmanager.rpc.address: 172.XXX.168.XXX
# The RPC port where the JobManager is reachable.
jobmanager.rpc.port: 6124
```

`masters`：内网地址

```yaml
172.XXX.168.XXX:8081
```

问题的关键是需要配置阿里云的安全组，一开始我在阿里云的控制台配置的安全组，一直无法访问`http://47.101.150.220:8081/`,后来发现自己装了个宝塔面板，这个优先级高些，在这里放行8081端口即可，然后访问：

![image-20200930141806162](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\install\aliyun\阿里云ECS服务器安装Flink教程.assets\image-20200930141806162.png)