# Windows操作脚本与常用技巧



## 1.win10 开机执行脚本，批处理执行

![image-20210412093138529](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\sys\windows\Windows操作教程.assets\image-20210412093138529.png)

```shell
C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp
```

- `start.bat`

```shell
@echo off
start "" "D:\Dev\JetBrains\IntelliJ IDEA 2019.3.4\bin\idea64.exe"
start "" "D:\Dev\Microsoft VSCode\Code.exe"
start "" "D:\Program Files (x86)\Notepad++\notepad++.exe"
start "" "D:\Program Files (x86)\Typora\Typora.exe"
start "" "D:\Program Files (x86)\NetSarang\Xshell.exe"
start "" "D:\Program Files (x86)\FastStone Capture\FSCapture.exe"
start "" "D:\Program Files (x86)\Navicat Premium 12\navicat.exe"
```

## 2.Win10定时开关机

![image-20210412094107738](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\sys\windows\Windows操作教程.assets\image-20210412094107738.png)

搜索「任务计划程序」创建任务

- 开机脚本：`gina  -s -t 3600`

![image-20210412094338838](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\sys\windows\Windows操作教程.assets\image-20210412094338838.png)

- 关机脚本：`C:\Windows\System32\shutdown.exe  -s`

![image-20210412094435573](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\sys\windows\Windows操作教程.assets\image-20210412094435573.png)