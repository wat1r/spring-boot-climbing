



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

