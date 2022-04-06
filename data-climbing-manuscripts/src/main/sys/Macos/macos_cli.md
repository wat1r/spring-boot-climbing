





```shell
查看npm过往的版本
npm view npm versions
查看可用的node版本
brew search node

#尝试了降低node版本，匹配安装gitbook，最后成功
brew install node@10

# 添加环境变量
vim ~/.zshrc

# node
export PATH="/usr/local/opt/node@12/bin:$PATH"

# 尝试了在/etc/profile里添加path 无效

# 查看gitbook版本
gitbook -V
CLI version: 2.3.2
GitBook version: 3.2.3

```



- Mac OS X 如何使用类似 ubuntu 下的 realpath

```shell
1. 安装 coreutils

$ brew install coreutils

安装完后，可以使用 realpath / grealpath

2. 在 .bash_profile 追加一个 function

function realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}


```





### Reference

- [Mac 定时关机、重启、休眠命令](https://www.jianshu.com/p/ec888c3e33dd)

- [技巧：为 macOS 的 Spotlight 重建索引，解决搜索结果不正确的问题](https://zhuanlan.zhihu.com/p/27595455)
- [Mac Alfred不能搜索文件或文件夹的解决方法](https://www.xinshouzhanzhang.com/macalfred.html)

- [系统 10.13.1 (17B48)， Alfred 有些文件和文件夹搜索不到， Spotlight 索引已重置](https://www.v2ex.com/t/406125)

- [Alfred 4常见问题:Alfred找不到我要寻找的文件或应用程序怎么办？](https://mac.orsoon.com/news/838369.html)
