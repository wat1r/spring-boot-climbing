







## 常用的命令

```shell
#激活superset虚拟环境
conda activate superset
#退出superset虚拟环境
conda deactivate
#在虚拟环境下安装mysqlclient驱动
(superset) [~]$ pip install mysqlclient
#查询当前的虚拟环境列表
conda info --envs
#查看镜像源
[]$ cat ~/.condarc 
auto_activate_base: false
channels:
  - http://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/main
  - http://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/free
  - defaults
  - conda-forge
show_channel_urls: true
#删除镜像superset
conda remove -n superset --all
#创建python3.9的虚拟环境
conda create -n superset python=3.9
#安装指定版本的superset
pip install apache-superset==2.0.0 -i https://pypi.douban.com/simple/
#安装指定版本的第三方依赖
pip install cliff==1.12  -i https://pypi.douban.com/simple/
#查看第三方依赖的版本
pip install PySide2==
#查看当前的安装包的版本
pip list  | grep "vine"
#更新某个安装包
pip install --upgrade cryptography==3.2
pip install --upgrade cryptography==3.3.2

#关闭和启动命令（superset）
ps -ef | awk '/gunicorn/ && !/awk/{print $2}' | xargs kill -9
#gunicorn --workers 5 --timeout 120 --bind wh-8-138:8787 superset:app --daemon
gunicorn -w 5 -t 120 -b wh-8-138:8787 "superset.app:create_app()"

```

安装apache-superset成功

![image-20230413113253794](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20230413113253794.png)





## 配置数据库

```

msyql://username:psw@127.0.0.1:3306/test-db
```







## Reference

- [执行superset db upgrade报错：ModuleNotFoundError: No module named 'werkzeug.wrappers.etag'](https://www.cnblogs.com/leo-wong/articles/16578636.html)

- [superset安装出现cryptography.hazmat.backends.openssl.x509](https://blog.csdn.net/XWxDSJ/article/details/128604074)
- [安装superset出现的各种依赖模块找不到](https://blog.csdn.net/qq_35746739/article/details/121257299)

