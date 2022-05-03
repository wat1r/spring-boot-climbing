



### 0.前言

相信前一阵gitee图床废了的消息遍布公号圈，所以部署一个稳定的图床显得尤为重要，打开V站，关于图床推荐的帖子也很多，目前平台多了，导致写文章的图片上传问题显得非常费时，遂决定搭建一套图床的方案。

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502103422.png)

查阅了下有比较适合大PV的高阶的解决方案：弄一个机房的主机或者多台高带宽的机器做负载，然后把图片全部迁移过去，也有使用VPS和CDN加速的方案，没有做过多了解了。

### 1.腾讯COS

- 创建存储桶，创建图床需要的文件夹目录

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502100240.png)

- 腾讯COS 密钥管理（在下面PicGo中的配置中需要使用到）

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502100206.png)



### 2.PicGo

- PicGo的下载地址：[链接](https://github.com/Molunerfinn/PicGo/releases)
- PicGo支持的图床：
- `七牛图床` v1.0
- `腾讯云 COS v4\v5 版本` v1.1 & v1.5.0
- `又拍云` v1.2.0
- `GitHub` v1.5.0
- `SM.MS V2` v2.3.0-beta.0
- `阿里云 OSS` v1.6.0
- `Imgur` v1.6.0

网上有很多的图床方案，比如新浪微博，白嫖github的，综合考虑，这里使用的是腾讯的COS（即存储对象），腾讯云`COS`的`json`配置如下：

```json
{
  "secretId": "",
  "secretKey": "",
  "bucket": "", // 存储桶名，v4和v5版本不一样
  "appId": "",
  "area": "", // 存储区域，例如ap-beijing-1
  "path": "", // 自定义存储路径，比如img/
  "customUrl": "", // 自定义域名，注意要加http://或者https://
  "version": "v5" | "v4" // COS版本，v4或者v5
}
```

#### 1. 获取你的APPID、SecretId和SecretKey

访问：https://console.cloud.tencent.com/cam/capi

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502093846.png)

#### 2. 获取bucket名以及存储区域代号

访问：https://console.cloud.tencent.com/cos5/bucket

创建一个存储桶。然后找到你的存储桶名和存储区域代号：

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502093931.png)

v5版本的存储桶名称格式是`bucket-appId`，类似于`xxxx-12312313`。存储区域代码和v4版本的也有所区别，v5版本的如我的是`ap-beijing`，别复制错了。

#### 3. 选择v5版本并点击确定

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502095520.png)

然后记得点击`设为默认图床`，这样上传才会默认走的是腾讯云COS，上传后，可以在腾讯COS中找到上传的图片。

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com//imgs/leetcode/classify/picgo-2.0.gif)

存储桶的图片

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502095731.png)

### 3.Typora

平时的文章主要是使用Typora写的（现在Typora开始收费了），然后是放在gitbub上做托管的

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220502103825.png)



### 4.总结

以上所有就是目前采用的图床方案了。







### Reference

- [PicGo 配置手册](https://picgo.github.io/PicGo-Doc/zh/guide/config.html)
- [PicGo github](https://github.com/Molunerfinn/PicGo/)
- [个人站求推荐图床解决方案](https://www.v2ex.com/t/440330)

- [各位 v 友，你们博客的图床都采用什么方案啊](https://v2ex.com/t/551634)

