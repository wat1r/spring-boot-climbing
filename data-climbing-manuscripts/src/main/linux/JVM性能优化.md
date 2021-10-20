## 命令



### jstat命令

- 按线程pid查询jstat状态

```powershell
 jstat -gc 14983
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
6144.0 6656.0  0.0    0.0   793600.0 793600.0 16410112.0 16410088.7 96216.0 88967.9 11776.0 10750.8   7431  234.789 4502  66867.926 67102.715
```









### top命令

- 查找当前线程pid的运行情况

```powershell
top -c -p 49390
```

- 按线程名称进行过滤

```powershell
 top -bc |grep name_of_process 
```





## 工具









## Reference

- [Linux使用jstat命令查看jvm的GC情况](https://www.cnblogs.com/qmfsun/p/5601734.html)
- [在LINUX中使用TOP -C命令来过滤基于进程名称列出的进程](http://www.dovov.com/linuxtop-c.html)