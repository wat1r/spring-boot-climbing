Ant

## 记一次`git push`失败的案例

- 这两天遇到的，在`IDEA`做`git push`时，出现如下的错误提示

```shell
Connection reset by 140.82.113.3 port 22
fatal: Could not read from remote repository.
Please make sure you have the correct access rights
and the repository exists.
```

- 大致如下图
![微信图片_20200428220707](D:\Dev\SrcCode\spring-boot-climbing\记一次git push失败的案例.assets\微信图片_20200428220707.png)

### 针对这个错误做了下面的几件事

#### 1.修改防火墙或者干脆关闭防火墙

- 在控制面板中找到-防火墙，添加入站规则`22`端口，后续又相继添加了`23` 端口，结果失败，还是继续报错，大部分的解错博客都是讲得如下方法
  ![微信图片_20200428220717](D:\Dev\SrcCode\spring-boot-climbing\记一次git push失败的案例.assets\微信图片_20200428220717.png)

#### 2.删除了`~/.ssh`下的`know_hosts`文件

- 失败

#### 3.添加`hosts`文件对于`github`地址的映射

```shell
74.125.237.1 dl-ssl.google.com
173.194.127.200 groups.google.com
140.82.113.3 github.com
199.232.69.194 github.global.ssl.fastly.net
47.101.150.220 aliyun.com
```

- 执行了`ipconfig /flushdns`,结果还是失败

#### 4.删除本机的公钥私钥,重新生成`rsa_pub`粘贴到github的setting里

```shell
git config --global user.name "github用户名"
git config --global user.email "github邮箱"

ssh-keygen -t rsa -C  "github邮箱"
```

- 结果还是失败了

#### 5.其它

- 执行命令

```
git config remote.origin.url git@github.com:wat1r/geek-algorithm-leetcode.git
```

- 失败

#### 6.最终

- 开始怀疑是不是网络问题

- 因为用的是路由器发出的`WIFI`,对比了手机发出来的热点`WIFI`的效果，应该是这个根源问题：
![2020-04-28_224047](D:\Dev\SrcCode\spring-boot-climbing\记一次git push失败的案例.assets\2020-04-28_224047.png)

#### 7.后续

- 这个报错还是没有消失，只是用这个网络在提交的时候无法成功

```verilog
Connection reset by 140.82.113.3 port 22
fatal: Could not read from remote repository.
Please make sure you have the correct access rights
and the repository exists.
```

- 有遇到此类的问题的，微信朋友圈留言(公众号的留言板还未开)