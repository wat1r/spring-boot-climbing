## ANTLR4案例解析





### 常用命令

```shell
#生成识别器
java -cp antlr-4.5.1-complete.jar org.antlr.v4.Tool Hello.g4

#编译由antlr生成的java代码
javac -cp .;antlr-4.5.1-complete.jar Hello*.java

#看识别期间创建的记号
java -cp .;antlr-4.5.1-complete.jar org.antlr.v4.gui.TestRig Hello s -tokens

Hello World # 输入并回车
EOF         # Linux系统输入Ctrl+d, windows系统输入ctrl+z, 并回车

#以LISP风格的文本信息查看记号
hello-world>java -cp .;antlr-4.5.1-complete.jar org.antlr.v4.gui.TestRig Hello s -tree  
Hello World
^Z
(s Hello World)

#以可视化的方式查看语法分析树
java -cp .;antlr-4.5.1-complete.jar org.antlr.v4.gui.TestRig Hello s -gui

以下是TestRig可用的所有参数：

-tokens 打印出记号流。
-tree 以LISP风格的文本形式打印出语法分析树。
-gui 在对话框中可视化地显示语法分析树。
-ps file.ps 在PostScript中生成一个可视化的语法分析树表示，并把它存储在file.ps文件中。
-encoding encodingname 指定输入文件的编码。
-trace 在进入/退出规则前打印规则名字和当前的记号。
-diagnostics 分析时打开诊断消息。此生成消息仅用于异常情况，如二义性输入短语。
-SLL 使用更快但稍弱的分析策略。


```

