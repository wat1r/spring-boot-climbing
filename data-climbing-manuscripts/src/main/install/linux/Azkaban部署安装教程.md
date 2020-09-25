







```
keytool -keystore keystore -alias jetty -genkey -keyalg RSA
azkabanpassword
azkabanpassword
```

![image-20200923134924949](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\install\linux\Azkaban部署安装教程.assets\image-20200923134924949.png)

停止`AzkabanWebServer`与`AzkabanExecutorServer`进程

```shell
ps -ef | egrep 'AzkabanWebServer|AzkabanExecutorServer' | grep -v grep | awk '{print $2}' | xargs kill -9
```





### Reference

- https://azkaban.readthedocs.io/en/latest/

- https://www.cnblogs.com/tsxylhs/p/7737016.html
- https://www.jianshu.com/p/701329d3ccd1
- https://my.oschina.net/wenzhenxi/blog/1863491
- https://blog.csdn.net/h1025372645/article/details/96867191
- https://blog.csdn.net/qq_37334135/article/details/78340122

- https://www.cnblogs.com/zlslch/p/7124205.html
- https://blog.csdn.net/qq_37334135/article/details/78340122
- https://yq.aliyun.com/articles/365708?scm=20140722.184.2.173
- https://blog.csdn.net/JavaDestiny/article/details/90091118