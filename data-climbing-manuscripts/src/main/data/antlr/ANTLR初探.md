

## ANTLR初探

##### 1.下载ANTLR放在安装目录下

地址：https://www.antlr.org/download/index.html

```shell
D:\Dev\SrcCode\antlr\antlr-4.5.3-complete.jar
D:\Dev\SrcCode\antlr\antlr4.bat
```

##### 2.编写`bat`脚本

```java
java org.antlr.v4.Tool %*
```

##### 3.添加环境变量到`classpath`

```
.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\tools.jar;D:\Dev\SrcCode\antlr\antlr-4.5.3-complete.jar
```

添加变量路径到`path`

```
D:\Dev\SrcCode\antlr
```

##### 4.添加依赖到项目

```xml
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.5.3</version>
</dependency>
```

##### 5.编写ANTLR文件，LearnAntlr.g4

```shell
grammar LearnAntlr ;         // grammer是规则文件的头，要和文件名一样
@header{                    //header代表生成的代码放在哪个包里面
package com.antlr4;
}
r  : 'hello' ID;           //r代表的是语法树的根结点
ID : [a-z]+ ;
WS : [ \t\r\n]+ -> skip ;  //ID代表未知的值
```

##### 6.执行g4文件

```powershell
D:\Dev\SrcCode\antlr> antlr4 ..\tech-climbing-common\basic-tech-bigdata\src\main\java\com\antlr4\LearnAntlr.g4
```

生成如下文件

![image-20200909082503801](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\antlr\ANTLR初探.assets\image-20200909082503801.png)

##### 7.新建ListenerRewrite继承LearnAntlrBaseListener

```java
public class ListenerRewrite extends LearnAntlrBaseListener {
    @Override
    public void exitR(LearnAntlrParser.RContext ctx) {
        final String a = ctx.getChild(0).getText().toLowerCase();
        final String b = ctx.getChild(1).getText().toLowerCase();
        System.out.println(a + " " + b);
    }
}
```

##### 8.词法语法解析测试

```java
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Antlr4Test {
    public static void main(String[] args) throws IOException {
        //输入文本hello world
        ANTLRInputStream inputStream = new ANTLRInputStream(" hello world");
        //词法分析器
        LearnAntlrLexer lexer = new LearnAntlrLexer(inputStream);
        //新建词法符号缓冲区，用于存储词法分析器生成的词法符号
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        //新建语法分析器，处理词法符号缓冲区内容
        LearnAntlrParser parser = new LearnAntlrParser(tokenStream);
        //正对规则开始词法分析
        LearnAntlrParser.RContext context = parser.r();
        //构建监听器
        ListenerRewrite listener = new ListenerRewrite();
        //使用监听器初始化对词法分析树遍历
        ParseTreeWalker.DEFAULT.walk(listener, context);
    }
}
```

> 输出hello world

##### Reference

- [Spark SQL解析过程以及Antlr4入门](https://baijiahao.baidu.com/s?id=1665087023209206128&wfr=spider&for=pc)
- [[ANTLR4在windows上的安装(java版)](https://www.cnblogs.com/wynjauu/articles/9872822.html)]