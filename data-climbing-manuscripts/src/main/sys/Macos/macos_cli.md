



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
