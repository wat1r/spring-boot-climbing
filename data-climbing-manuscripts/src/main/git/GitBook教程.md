## GitBook教程

```shell
node -v
cnpm install gitbook-cli -g


npm install -g gitbook@3.0.0

# 初始化过程比较慢
gitbook -V
CLI version: 2.3.2
Installing GitBook 3.2.3

# 启动服务(到gitbook的项目目录下)
$ gitbook serve

# Serving book on http://localhost:4000

$ SUMMRY下写好目录，gitbook init 生成文件，图片注意相对路径

gitbook install 


# 单命令安装插件-注意gitbook-plugin-前缀
cnpm install gitbook-plugin-search-pro

#命令卸载插件-注意gitbook-plugin-前缀
npm uninstall -g gitbook-plugin-<plugin_name>

# 部署流程
gitbook build
_book文件夹 打包zip文件 
target文件夹 push上去
jenkins发布


# npm查询包的版本
npm view gitbook-plugin-prism versions --json 



```



脚本`deploy.sh`

```shell
#!/bin/sh

echo "=================git pull================="
git pull

echo "=================gitbook build================="
gitbook build

echo "=================zip================="
zip  -r -q _book.zip _book/*

echo "=================move================="
mv  _book.zip ./target

echo "=================deploy================="
git add . && git commit -m "deploy" && git push
```



```powershell
gitbook -V 查看版本号
gitbook init 初始化
gitbook serve 预览
gitbook build 生成
gitbook build --gitbook=2.6.7 生成时指定gitbook的版本, 本地没有会先下载
gitbook uninstall 2.6.7 卸载指定版本号的gitbook
gitbook fetch [version] 获取[版本]下载并安装<版本>
gitbook --help 显示帮助文档
gitbook ls-remote 列出NPM上的可用版本：
```











### Reference

- [gitbook安装与使用之windows下搭建gitbook平台](https://www.cnblogs.com/Lam7/p/6109872.html)
- [gitbook安装与使用（含常用插件和book.json配置详解）](https://blog.csdn.net/fghsfeyhdf/article/details/88403548)
- [使用GitBook+Github编写文档书籍](http://michael728.github.io/2018/09/08/tools-gitbook-hackpythonista-notebook/)