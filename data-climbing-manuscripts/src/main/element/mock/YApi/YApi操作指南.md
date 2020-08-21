### 简介

> 旨在为开发、产品、测试人员提供更优雅的接口管理服务。可以帮助开发者轻松创建、发布、维护 API

### 步骤

#### 创建集合并在执行的时候可以设置环境



![image-20200820172945383](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\mock\YApi\YApi操作指南.assets\image-20200820172945383.png)

>  Step3的请求：

```json
{
  "engine": "PRESTO",
  "limit": 10,
  "source": "ANALYZE_BENCH",
  "sql": "create table {{$.3996.body.name}}.t_ctas_temp_presto_no_column_1  comment 't_ctas_temp_presto_no_column_1'  as select *  from {{$.4076.body.name}}.test_create_table_0515 where part_date < '2019-08-30' "
}
```

#### 在环境设置中可以配置多环境部署

> 可设置环境变量，global前缀

![image-20200820173248236](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\mock\YApi\YApi操作指南.assets\image-20200820173248236.png)

#### 环境变量来自全局的环境变量



![image-20200820173538851](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\mock\YApi\YApi操作指南.assets\image-20200820173538851.png)

#### 在响应的Test中可设置assert

![image-20200820173615478](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\mock\YApi\YApi操作指南.assets\image-20200820173615478.png)



### 扩展

#### 变量参数

YApi 提供了强大的变量参数功能，你可以在测试的时候使用前面接口的 `参数` 或 `返回值` 作为 `后面接口的参数`，即使接口之间存在依赖，也可以轻松 **一键测试~**

> Tips: 参数只能是测试过程中排在前面的接口中的变量参数

格式：

```
$.{key}.{params|body}.{path}
```

如上述的`{{$.3996.body.name}}`  `3996`是请求的`id` 即`key`

#### Mock占位符

> 基于 [mockjs](http://mockjs.com/)，跟 Mockjs 区别是 yapi 基于 json + 注释 定义 mock 数据，无法使用 mockjs 原有的函数功能



### Reference

- [YApi Doc](https://hellosean1025.github.io/yapi/index.html)
- [mockjs](http://mockjs.com/)

