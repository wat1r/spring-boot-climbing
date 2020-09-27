## Azkaban部署安装教程



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





### 操作指南

> #### 单个job的创建步骤

- 创建job很简单，只要创建一个以.job结尾的文本文件就行了,例如我们创建一个工作，用来打印hello，名字叫做command.job

例如:

```shell
command.job
type=command
command=echo 'hello'
```

- 将 job 资源文件打包，只能打包成zip格式
- 通过 azkaban web 管理平台创建 project 并上传压缩包：可以点击执行或者定时任务

> #### 多job项目创建

举例：

比如导入hive前，需要进行数据清洗，数据清洗前需要上传，上传之前需要从ftp获取日志

定义5个job：

```
1、o2o_2_hive.job：将清洗完的数据入hive库
2、o2o_clean_data.job：调用mr清洗hdfs数据
3、o2o_up_2_hdfs.job：将文件上传至hdfs
4、o2o_get_file_ftp1.job：从ftp1获取日志
5、o2o_get_file_fip2.job：从ftp2获取日志
```

依赖关系：3依赖4和5，2依赖3，1依赖2，4和5没有依赖关系

`o2o_2_hive.job`:

```shell
type=command
# 执行sh脚本，建议这样做，后期只需维护脚本就行了，azkaban定义工作流程
command=echo  'sh /job/o2o_2_hive.sh'
dependencies=o2o_clean_data
```

`o2o_clean_data.job`

```shell
type=command
# 执行sh脚本，建议这样做，后期只需维护脚本就行了，azkaban定义工作流程
command=echo 'sh /job/o2o_clean_data.sh'
dependencies=o2o_up_2_hdfs
```

`o2o_up_2_hdfs.job`

```shell
type=command
#需要配置好hadoop命令，建议编写到shell中，可以后期维护
command=echo 'hadoop fs -put /data/*'
#多个依赖用逗号隔开
dependencies=o2o_get_file_ftp1,o2o_get_file_ftp2
```

`o2o_get_file_ftp1.job`

```shell
type=command
command=echo 'wget "ftp://file1" -O /data/file1'
```

`o2o_get_file_ftp2.job`

```shell
type=command
command=echo 'wget "ftp:file2" -O /data/file2'
```

创建好job后打包成zip上传



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
- https://www.cnblogs.com/frankdeng/p/9284644.html