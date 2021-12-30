





```linux
# ┌───────────── minute (0 - 59)
# │ ┌───────────── hour (0 - 23)
# │ │ ┌───────────── day of the month (1 - 31)
# │ │ │ ┌───────────── month (1 - 12)
# │ │ │ │ ┌───────────── day of the week (0 - 6) (Sunday to Saturday;
# │ │ │ │ │                                   7 is also Sunday on some systems)
# │ │ │ │ │
# │ │ │ │ │
# * * * * * <command to execute>

其中，每个字段格式：可以包含`逗号(,：表示离散点)`、`减号(-：表示时间段a-b)`。
```

### Quartz

```lua
1.  Seconds
2.  Minutes
3.  Hours
4.  Day-of-Month
5.  Month
6.  Day-of-Week
7.  Year (可选字段)
```









