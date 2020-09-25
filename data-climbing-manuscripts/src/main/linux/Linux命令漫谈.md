## Linux命令漫谈





#### `grep -v grep`

> 目的：去除包含grep的进程行 ，避免影响最终数据的正确性

- `grep` 是查找含有指定文本行的意思，比如`grep test` 就是查找含有`test`的文本的行
- `grep -v`是反向查找的意思，比如` grep -v grep` 就是查找不含有` grep` 字段的行

 我们想要找出哪个进程的`id`

可以使用如下语段：

```shell
ps -ef | grep "mongod" | grep -v "grep" | awk '{print $2}'
```

杀掉有关`swoole`得进程：

```shell
ps -aux|grep "swoole"|awk '{print "kill -9 "$2}'
```



#### `grep` `egrep` `fgrep`区别

```shell
# grep:
传统的 grep 程序, 在没有参数的情况下, 只输出符合 RE 字符串之句子. 常见参数如下:
-v: 逆反模示, 只输出"不含" RE 字符串之句子.
-r: 递归模式, 可同时处理所有层级子目录里的文件.
-q: 静默模式, 不输出任何结果(stderr 除外. 常用以获取 return value, 符合为 true, 否则为false .)
-i: 忽略大小写.
-w: 整词比对, 类似 <word> .
-n: 同时输出行号.
-c: 只输出符合比对的行数.
-l: 只输出符合比对的文件名称.
-o: 只输出符合 RE 的字符串. (gnu 新版独有, 不见得所有版本都支持.)
-E: 切换为 egrep .

# egrep:
为 grep 的扩充版本, 改良了许多传统 grep 不能或不便的操作. 比方说:
\- grep 之下不支持 ? 与 + 这两种 modifier, 但 egrep 则可.
\- grep 不支持 a|b 或 (abc|xyz) 这类"或一"比对, 但 egrep 则可.
\- grep 在处理 {n,m} 时, 需用 { 与 } 处理, 但 egrep 则不需.
诸如此类的... 我个人会建议能用 egrep 就不用 grep 啦... ^_^

# fgrep:
不作 RE 处理, 表达式仅作一般字符串处理, 所有 meta 均失去功能.
```









### Reference

- https://www.cnblogs.com/applelife/p/10481537.html