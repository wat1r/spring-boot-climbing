





```shell
# 查看文本内容
1.查看指定行数的文本
hdfs dfs -cat /aaaaa/空白文本.txt | shuf -n 5
2.查看指定头数的文本
hdfs dfs -cat /aaaaa/空白文本.txt | head -100
3.查看指定尾数的文本
hdfs dfs -cat /aaaaa/空白文本.txt | tail-100
4.查看文本行数
hdfs dfs -cat /path/txt | wc -l
```

