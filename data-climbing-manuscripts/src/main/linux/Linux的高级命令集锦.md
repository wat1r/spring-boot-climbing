## Linux的高级命令集锦









### 9.查询文件(du)

- 查找top10的文件或是文件夹

```shell
du : 计算出单个文件或者文件夹的磁盘空间占用.
sort : 对文件行或者标准输出行记录排序后输出.
head : 输出文件内容的前面部分.
du：
-a：显示目录占用空间的大小，还要显示其下目录占用空间的大小
sort：
-n  : 按照字符串表示的数字值来排序
-r ：按照反序排列
head :
-n : 取出前多少行

以上的问题可以使用命令： du -a | sort -n -r | head -n 10
```

- 查找大于固定值的大文件

```shell
find -type f -size +100M  -print0 | xargs -0 du -h | sort -nr
```

```shell
 find / -type f -size +1024M  -print0 | xargs -0 du -h | sort -nr
```










### 10.net|port

- 查看端口使用情况:`lsof -i:8080`
- ping通ip地址：`ping github.com`
- 测试ip对应的端口是否开启:`telnet 140.82.113.3 22 `
  - 提示不是内部命令的报错信息的话，windows系统的话可能需要开启`telnet`服务

- 命令查看正在运行状态的服务及端口:`netstat -tunpl`





