

## Linux的高级命令集锦

# A.Linux内置命令

## 0.安装命令

```shell
## rpm（RedHat Package Manager）
# rpm 全称 Red-Hat Package Manager。是一种底层的包管理工具，使用rpm 可以进行软件的安装、查询、卸载、升级等工作。在安装软件的时候只会安装指定的软件，而不会安装依赖性文件，若所安装的软件无依赖性或者依赖性文件被解决了，那么就会正常安装，否则会保错。

https://www.cnblogs.com/LiuChunfu/p/8052890.html

## yum
# yum 全称 Yellow dog Updater，Modified，是一个基于rpm的上层软件包管理器。yum在服务器端存有所有的 rpm 包，并将各个包之间的依赖关系记录在文件中。使用 yum 安装 rpm 包的时候，能够从指定的服务器自动下载 rpm 包并且安装，可以自动处理软件包之间的依赖关系，并且一次安装所有依赖的软件包。yum提供了查找、安装、删除某一个 /一组 / 甚至全部软件包的命令。
# yum 拥有 rpm 的功能，还具备了从网络上下载 rpm 包和依赖包的功能

Yum是rpm的前端程序，主要目的是设计用来自动解决rpm的依赖关系

## apt-get

apt-get属于ubuntu、Debian的包管理工具

yum则属于Redhat、Centos包管理工具

## homebrew

是mac的包管理工具
```



---

## 1.awk

### 基础知识

```powershell
AWK是一种处理文本文件的语言，是一个强大的文本分析工具。

之所以叫AWK是因为其取了三位创始人 Alfred Aho，Peter Weinberger, 和 Brian Kernighan 的 Family Name 的首字符。

语法
awk [选项参数] 'script' var=value file(s)
或
awk [选项参数] -f scriptfile var=value file(s)
选项参数说明：

-F fs or --field-separator fs
指定输入文件折分隔符，fs是一个字符串或者是一个正则表达式，如-F:。
-v var=value or --asign var=value
赋值一个用户定义变量。
-f scripfile or --file scriptfile
从脚本文件中读取awk命令。
-mf nnn and -mr nnn
对nnn值设置内在限制，-mf选项限制分配给nnn的最大块数目；-mr选项限制记录的最大数目。这两个功能是Bell实验室版awk的扩展功能，在标准awk中不适用。
-W compact or --compat, -W traditional or --traditional
在兼容模式下运行awk。所以gawk的行为和标准的awk完全一样，所有的awk扩展都被忽略。
-W copyleft or --copyleft, -W copyright or --copyright
打印简短的版权信息。
-W help or --help, -W usage or --usage
打印全部awk选项和每个选项的简短说明。
-W lint or --lint
打印不能向传统unix平台移植的结构的警告。
-W lint-old or --lint-old
打印关于不能向传统unix平台移植的结构的警告。
-W posix
打开兼容模式。但有以下限制，不识别：/x、函数关键字、func、换码序列以及当fs是一个空格时，将新行作为一个域分隔符；操作符**和**=不能代替^和^=；fflush无效。
-W re-interval or --re-inerval
允许间隔正则表达式的使用，参考(grep中的Posix字符类)，如括号表达式[[:alpha:]]。
-W source program-text or --source program-text
使用program-text作为源代码，可与-f命令混用。
-W version or --version
打印bug报告信息的版本。
```

+ 变量与说明
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191212141755986.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dhdDFy,size_16,color_FFFFFF,t_70)




+ 查询 `interventions.txt `文件的制定的第三个字段的类型
  `awk -F'|' '  {if($3 ~"Drug|Other|Biological|Genetic") {print $2,$3 }}' interventions.txt OPS="\t"  | less `

+ 查询`interventions.txt `的各个统计
  `awk  -F'|' 'NR!=1{a[$3]++;} END {for (i in a) print i ", " a[i];}' interventions.txt`

+ 对文件的按`.`分隔后取第一个，去重后输出，源数据举例 `dw.XXXXX`  `dm.XXXXX`
  `awk -F'.' '{print $1}' hive_tables.txt | awk '!x[$0]++' | less`

+ 对文件按`.`分隔够取第一个，去重后输出

```shell
$ awk -F'.' '{a[$1]++;} END {for (i in a ) print i ", " a[i];}' hive_tables.txt
dm, 642
dw, 14338
```

+ 对文件去重，统计

```powershell
awk   '{a[$1]++;} END {for (i in a ) print i ", " a[i];}'  result.txt
${date-89}, 34
${date-91}, 13
${date-45}, 7
${date-365}, 52
${date-180}, 148
${data_time}, 2
```

- 同上

```powershell
$ grep -Eo  '\${[^}]+}' temp.txt |  sort | uniq
${date-1d}
${date-2}
${datetime}
${datetime+2m}
${datetime-2m}
${updatetime+2d}
```



+ 根据`"` 分割，取字段 ：`awk -F'"' '{print $6}'   cartridge_msg  | head -7`
+ 看看统计每个用户的进程的占了多少内存（注：sum的RSS那一列)
  `ps aux | awk 'NR!=1{a[$1]+=$6;} END { for(i in a) print i ", " a[i]"KB";}'`
+ awk去重：`awk '!x[$0]++' file1awk > file2`
+ sh脚本中杀死进程：`ps -ef | grep java | grep -v consumer |awk '{print $2}' | xargs -p kill -9 `
+ 查询行数相关：` awk 'FNR==16000' file `
+ 查询需要的信息并且加上第一行:`awk  'NR==1;/205613/{print}' Submissions.txt`
+ 查看所有行的记录:` awk '//{print}' passwd.txt`
+ 过滤到`localhost`:` awk '/localhost/{print}' /etc/hosts `
+ 按`~` 分隔后，取field后，去重：`awk -F'~' '!($12 in a){a[$12];print $12}' products_1.txt`
+ 按`~` 分隔后，取field后，去重（去掉第一行）： `awk -F'~' '{if(NR!=1 && !($12 in a) ) {a[$12];print $12}}' products_1.txt`
  `funtions.awk`：计算从1到100的和

```shell
#! /bin/awk -f
#add
function Add(firstNum, secondNum) {
    sum = 0
    for (i = firstNum; i <= secondNum; i++) {
        sum = sum + i;
    }
    return sum
}

function main(num1, num2) {
    result = Add(num1, num2)
    print "Sum is:", result
}

#execute
function
BEGIN {
    main(1, 100)
}
```

`passwd.awk`:以`:` 分割，打印出想要的`field`,`$NF`表示选定每一行的最后一列

```shell
#! /bin/awk -f 

BEGIN {FS=":"} 
/root/ {print "Username is:"$1,"UID is:"$3,"GID is:"$4,"Shell is:"$NF} 

```

`passwd.txt`:文件截取，
执行命令:` awk -f  passwd.awk passwd.txt` OR`  ./passwd.awk passwd.txt`

```
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin

```

判断一个文件中两个字段相同并筛选出：

```powershell
$ cat action_relation.txt | awk  -F'\t' '{if($2==$3) print}' | head
"658"   "669"   "669"   "-1"    "D"     "0"     "1"
```



去重统计

```shell
 less  bigbang-server.log | grep "ready to dispatch" | awk -F'\]:' '{print $2}' | awk -F'##' '{print $1}' | sort | uniq  -c   | less
```





\#grep --color=auto “[[:digit:]]\{3\}\.[[:digit:]]\{3\}\.[[:digit:]]\{3\}\.[[:digit:]]\{3\}”FILE

会显示出如下结果：

212.592.111.303

111.222.333.444





---



## 2.sed

### 基础知识

```shell
语法
sed [-hnV][-e<script>][-f<script文件>][文本文件]
参数说明：

-e<script>或--expression=<script> 以选项中指定的script来处理输入的文本文件。
-f<script文件>或--file=<script文件> 以选项中指定的script文件来处理输入的文本文件。
-h或--help 显示帮助。
-n或--quiet或--silent 仅显示script处理后的结果。
-V或--version 显示版本信息。
动作说明：

a ：新增， a 的后面可以接字串，而这些字串会在新的一行出现(目前的下一行)～
c ：取代， c 的后面可以接字串，这些字串可以取代 n1,n2 之间的行！
d ：删除，因为是删除啊，所以 d 后面通常不接任何咚咚；
i ：插入， i 的后面可以接字串，而这些字串会在新的一行出现(目前的上一行)；
p ：打印，亦即将某个选择的数据印出。通常 p 会与参数 sed -n 一起运行～
s ：取代，可以直接进行取代的工作哩！通常这个 s 的动作可以搭配正规表示法！例如 1,20s/old/new/g 就是啦！
```

- 查看文件的第1到第5行：`sed -n '1,5p' passwd.txt`
- 查看文件的第1到结尾：`sed -n '1,$p' passwd.txt`
- 去除文件的空行：`sed -i '/^$/d' passwd.txt`  慎用   `-i `  ，原地修改
- 去除文件的空行：`cat passwd.txt | awk '{if($0!="") print }' ` 
- 在文件的每行结尾处添加字符串:`sed -i 's/$/_MODIFY/'  file`
- 在文件的每行开头处添加字符串:`sed -i  's/^/HEAD_/g' passwd.txt`
- 替换文件的`_MODIFY`为`_UPDATE`：` sed -e  's/_MODIFY/_UPDATE/' passwd.txt  > passwd_update.txt`
- 替换文件的字符串：` sed -i "s/my/Hao Chen's/g" pets.txt   `   其中 `/g` 是全局模式表示每一行出现的符合规则的都被替换掉
- 删除第2行到末尾行：`sed '2,$d' my.txt`
- 在每一行的开头和结尾都添加

```shell
sed  's/^/git branch -d /; s/$/ \&\& git push origin -d /' git_branch.txt
```





- 如何替换^A（\001）,\002,\003等特殊字符

```java
方法1：符号替换
其中 “^A” 这个符号 ，使用组合按键“ctrl+V+A”获得
sed -i "s/^A/\t/g" test.txt

方法2：编码替换
sed -i "s/\x01/\t/g" test.txt
```

![image-20220210165501575](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\linux\Linux的高级命令集锦.assets\image-20220210165501575.png)



> 找到access.log文件中/req/v2/fetch的 请求，拿到7 14 15列，然后按 "切分， 选取大于1s的输出

```shell
cat access.log | awk -F' ' '/req\/v2\/fetch/{print $7, $14, $NF}'   | awk -F '"' '$4>1' | awk -F' ' '{print $0}' | less
```





```shell
cat hiveassistant3-server-prod-https_access.log | awk -F' ' '/job\/engine\/result/{print $7}' | less  > ../hadoop/hive_server_access_job_engine_result.log
```





```shell
 awk '/(jobid=)(.*)(&que)/{print $1}' hive_server_access_job_engine_result.log | less
```





提取两个字符之间的字符串

```shell
 sed -E 's/.*jobid=(.*)&id=.*/\1/' hive_server_access_job_engine_result.log | less
```





去重

```shell
awk '!x[$0]++' hive_server_access_job_engine_result_job_id_repeative.log > hive_server_access_job_engine_result_job_id.log
```





```shell
sed -E 's/.*\/tasks\/req\/v2\/fetch\/(.*)\?token=.*/\1/' access_august_prod_more_than_2_seconds.log | less

sed -n '/tasks\/req\/v2\/fetch\/ \?token=/p' access_august_prod_more_than_2_seconds.log | less

sed -E 's/.*\/tasks\/req\/v2\/fetch\/(.*)\?token=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<20'  > seg_1.txt
sed -E 's/.*\/req\/v2\/fetch\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<20'  > seg_2.txt
sed -E 's/.*\/req\/v2\/fetch\/hive\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)=36'  > seg_3.txt
sed -E 's/.*\/req\/v2\/fetch\/hive\/page\/(.*)\?operatorId=.*/\1/' access_august_prod_more_than_2_seconds.log | awk 'length($0)<=36'  > seg_4.txt
```



查看某个时间点往后的日志：

```shell
 sed -n '/2021-11-11 05:01:59./,//p' bigbang-server.log  | less
```



```shell
sed -n '/2021-11-11 17:53:04./,//p' bigbang-server.log |  grep "start dispatch instance to worker" | awk -F'worker:' '{print $2}' | sort | uniq -c | less
```



```java
sed -n '/2021-11-11 17:53:04./,//p' bigbang-server.log |  grep "start dispatch instance to worker" | awk -F'worker:' '{print $2}' | sort | uniq -c | less
```



切分

```shell
less bigbang-server.log | grep -oE 'receive tasktracker report info: ##instanceId:(.*) ->  status:5##'| cut -d ":" -f 3| cut -d " " -f 1 |  uniq -c |sort   -r | less
```



- 按统计的次数降序排列 -r 

```shell
less bigbang-server-application-338908527265318528.log | grep -oE 'receive tasktracker report info: ##instanceId:(.*) ->  status:5##'| cut -d ":" -f 3| cut -d " " -f 1 |  uniq -c | sort -r   | head
```



```java
less bigbang-server-application.2021-11-19.log  | grep -oE 'receive tasktracker report info: ##instanceId:(.*) ->  status:5##'| cut -d ":" -f 3| cut -d " " -f 1 | uniq -c | sort | uniq -c | sort -rn |   less 
```





```java
//筛选出1: jobId:{} , instanceId:{} -> {}
//     2: jobId:{} , instanceId:{} -> {} 
less bigbang-server.log | grep -E  "jobId:(.*) , instanceId:"  | less 
```





## 3.vim/vi

```powershell
NORMAL模式下：
在光标的位置按“yy”，复制当前行,然后再光标的行按“p”,粘贴到下一行，原来的往下顺移。
删除当前行-------dd
复制多行----------nyy(比如3yy，复制3行)
删除多行----------ndd
复制多遍----------np
删除光标当前所在的字符--x 

:%s/原字符/替换字符/g 其中%全文的行，g 全文的列
:1,3s/: /@/g  1-3行替换


hjkl表示上下左右，4h表示跳4行
w可以跳到下一个单词的开头
b可以跳到上一个单词的开头

gg可以跳到文件的开头
G可以跳到文件的末尾

Ctrl+U向上翻页
Ctrl+D向下翻页

f+字符 会移动到这一行离光标最近的位置




y(yank) 复制  
  - yaw（all words）可以复制一个单词+p进行粘贴
  - y4j 复制了当前行在内的向下的4行
  - yfr 表示复制当前行到字符r的内容
p(paste)粘贴
c(change)
	- caw删除当前的单词进入输入模式
	- cc删除当前行进入输入模式
	- c4j删除当前下4行进入输入模式
u  撤销



输入模式
i(input)可以进入输入模式
a(append)当前光标的后面
I 本行的开头进行输入
A 本行的末尾进行输入

:wq保存





VISUAL模式
v 进入该模式
shift+v 进入VISUAL_LINE模式





安装插件


https://github.com/junegunn/vim-plug


NERTree
~/.vimrc文件下

set hlsearch
set nu!
syntax on
call plug#begin('~/.vim/plugged')
Plug 'scrooloose/nerdtree'
call plug#end()

" 绑定快捷键
nnoremap <silent> <C-e> :NERDTree<CR>
" map <F3> :NERDTreeMirror<CR>
" map <F3> :NERDTreeToggle<CR>


打开文件后执行 :PlugInstall 可以使用Tab键联想
:NERtree就可以显示某个文件下的所有的文件夹了
进去后使用 o 可以打开当前选中的文件或者文件夹
切换工作台和目录 
ctr+w+h  光标focus左侧树形目录
ctrl+w+l 光标focus右侧文件显示窗口。 
ctrl+w+w，光标自动在左右侧窗口切换 



全部删除：按esc键后，先按gg（到达顶部），然后dG
全部复制：按esc键后，先按gg，然后ggyG
全选高亮显示：按esc键后，先按gg，然后ggvG或者ggVG
单行复制：按esc键后，然后yy
单行删除：按esc键后，然后dd
粘贴：按esc键后，然后p




```



#### Visual模式与Visual Block 与Visual Line

- 选中双引号的内容：`vi"` 配合`c`即可删除双引号的内容
- 选中多行后，按`<`或者`>`可以多行缩进，`.`可以重复上面的操作

- Ctrl+V进入块选，选中多行，`s`执行删除
- 多行注释代码：`Ctrl+V`后选中多行的开头，`I`后，输入`//`，`esc`后实现多行注释，删除这个注释的时候，多选选中后，按`x`进行删除
- 同上面：在每行的末尾追加字符，`A`+`Esc` ，`$`可以跳到每行的行尾
- `Shift+v`进入行模式

- 按`Ctrl+v`进入块模式，选中部分字符，`:s/http:/https:/g`可以实现替换选中的`http:`为`https:`
- 块模式下，光标回退到开始的位置：`o`  `O`的切换

#### 搜索与查找

- 打开文件后，跳到指定行:`:n`，其中n为要跳转的行号

- 分组替换 : :{作用范围}s/{目标}/{替换}/{替换标志}

```java
原文：
1. France VS Germany
命令：
:s/\(France\)\(\sVS\s\)\(Germany\)/\3\2\1
结果：
1. Germany VS France
```









#### 删除

删除指定位置到文末:`dG`

删除全部文本

```powershell
gg
dG
```

删除xx行至xx行:`: 1,10d`

将从当前位置删除到行尾：`d$`
将从当前向后删除到第一个非空格字符:`d^`
将从当前向后删除到行首:`d0`
将当前字符删除到当前字尾(包括)尾部空格):`dw`
将当前字符删除到当前字符的开头:`db`

#### Msic

命令行复制一个文件到剪切板，并粘贴到`Typora`上,`cat ~/.vimrc | pbcopy`





安装插件`YouCompleteMe`时遇到的报错

> **YouCompleteMe unavailable: requires Vim compiled with Python (3.6.0+) support**







---





## 4.compress/uncompress

### tar

 .**tar.gz** 使用tar命令进行解压

```shell
 tar -zxvf java.tar.gz

解压到指定的文件夹
	tar -zxvf java.tar.gz  -C /usr/java
```

tar参数介绍

```shell
-c：创建新的tar文件。
-x：解开tar文件。
-t：列出tar文件中包含的文件信息。
-r：附加新的文件到tar文件中。
-u：用已打包的文件的较新版本更新tar文件。
-A：将tar文件作为一个整体追加到另一个tar文件中。
-d：将文件系统里的文件和tar文件里的文件进行比较。
-k：保留原有文件不覆盖。
-f：指定要处理的文件名。
-P：使用绝对路径。
-v：显示详细的tar处理过程。
-p：保留备份数据的原本权限与属性。
-m：不使用新的时间戳。
-j：调用bzip2执行压缩或解压缩。
-C：指定路径。
```

### gzip

**gz**文件的解压 gzip 命令

```shell
 	gzip -d java.gz
```

也可使用**zcat** 命令，然后将标准输出 保存文件

```shell
zcat java.gz > java.java
```



### zip

```shell
# 压缩harry文件夹下的所有文件及文件夹
zip -r harry.zip  harry
```



```shell
# 将文件log.txt、test.txt和目录test/压缩到test.zip文件中，命令：
zip -r test.zip log.txt test/ test.txt。
#从压缩包test.zip中删除log.txt文件，命令：
zip -d test.zip log.txt。
# 更新压缩包mytxt.zip中的t3.txt文件，命令：
zip -f mytxt.zip t3.txt。

-r：压缩目录和文件。
-q：压缩文件时不显示压缩过程的详细信息。
-d：从现有的ZIP文件中删除指定的文件或目录。
-f：用于刷新（更新）现有ZIP文件中的指定文件
```



## 5.查看系统信息

```shell
# 查看系统配置：

uname -a：显示系统信息，包括内核名称、主机名、内核版本号、内核发行日期等。
lsb_release -a：显示Linux标准基础（LSB）和特定发行版的信息。注意：并非所有Linux发行版都支持这个命令。
cat /etc/os-release 或 cat /etc/*release*：查看操作系统名称、版本号等信息。
hostnamectl：显示关于系统主机名、内核、操作系统等信息。
hwinfo --cpu：显示CPU的详细信息（需要安装hwinfo包）。
lscpu：显示CPU架构信息。
free -m：查看内存使用情况。
df -h：查看磁盘空间使用情况。

# 查看CPU信息：

lscpu：显示CPU的详细信息，包括型号、核心数、线程数等。
cat /proc/cpuinfo：显示CPU的详细信息，包括每个CPU核心的详细信息。
top 或 htop：在运行时查看CPU使用情况，htop是top的一个增强版本，提供了更友好的界面。

# 查看系统版本：

cat /etc/os-release：查看操作系统的名称、版本号等。
lsb_release -a：在支持LSB的发行版中，可以显示更详细的发行版信息。
```









## 6.curl

[Linux curl命令详解](https://www.cnblogs.com/duhuo/p/5695256.html)



返回结果，如果是json打印，pretty展示

方法1：

```shell
curl -k -u elastic:gV4pgxNCTi5y*80GmoqN https://localhost:9200?pretty=true
```

方法2：

```python
curl https://news-at.zhihu.com/api/4/news/latest | python -m json.tool
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  3901  100  3901    0     0  33333      0 --:--:-- --:--:-- --:--:-- 33629
{
    "date": "20171014",
    "stories": [
        {
            "ga_prefix": "101417",
            "id": 9651211,
            "images": [
                "https://pic3.zhimg.com/v2-471f6f1170fcb7d491ba54404acaf30a.jpg"
            ],
            "multipic": true,
            "title": "\u8fd9\u4e9b\u6709\u6545\u4e8b\u7684 DOTA \u804c\u4e1a\u9009\u624b\u5916\u53f7\uff08\u56fd\u5916\u7bc7\uff09",
            "type": 0
        }]
}        
  
```











## 7.nl

- NONE

----





## 8.grep

- -o命令精髓

```powershell
grep -Eo  '\${(\w+)?(-)?(\d+)?(\w+)?}' action.txt  > result.txt
# ${date}
# ${date-30}
# ${date-30}
# ${date-1}
```









## 9.查询文件(du)

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

- 查找当前文件夹下文件大小：

  ```shell
  $ du -sh *
  332K    $Recycle.Bin
  4.0K    AMTAG.BIN
  4.0K    Config.Msi
  0       Documents and Settings
  1.7G    Drivers
  
  ```

  





---


## 10.net|port

- 查看端口使用情况:`lsof -i:8080`

- ping通ip地址：`ping github.com`

- 测试ip对应的端口是否开启:`telnet 140.82.113.3 22 `

- 提示不是内部命令的报错信息的话，windows系统的话可能需要开启`telnet`服务

- 命令查看正在运行状态的服务及端口:`netstat -tunpl`

- 使用cmd命令查看端口号占用情况，例如查看端口 8014，可以看出进程号为10728；

  ```shell
  netstat -ano | findstr 端口号
  ```

- `netstat -nupl`(UDP类型的端口)

- `netstat -ntpl` (TCP类型的端口)

- `netstat -anp | grep 1992`查询1992端口程序，查不到pid的话可能和用户权限有关，

- 使用 lsof 命令来查看某一端口是否开放。查看端口可以这样来使用，我就以80端口为例：
  `lsof -i:80`

- 查询端口占用：`netstat -ano` 或 `netstat -ano | grep 8080`

- 查询这个端口的`PID`被哪个进程占用:`tasklist|findstr "9088"`

-  直接强制杀死指定端口:`taskkill /pid 4136 -t -f`

```shell
 taskkill //F  //pid 2236
成功: 已终止 PID 为 2236 的进程。
```

- 查看默认端口：`ss -lntp`
- 通过端口号找到服务名

```shell
[root@centos7 flink]# netstat -antup | grep 8080
tcp        0      0 0.0.0.0:8080            0.0.0.0:*               LISTEN      15502/java          
[root@centos7 flink]# ll /proc/15502/cwd 
lrwxrwxrwx 1 root root 0 Sep 30 10:47 /proc/15502/cwd -> /usr/local/spark
```

- 查看详细信息

```java
[root@centos7 flink]# netstat -nltp
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 0.0.0.0:42214           0.0.0.0:*               LISTEN      11024/java          
tcp        0      0 0.0.0.0:46727           0.0.0.0:*               LISTEN      11310/java      
```

- 查看某进程占用的端口号

```java
 netstat -anp
 [root@www ~]# netstat -anp | grep syslog
 udp    0   0 0.0.0.0:514         0.0.0.0:*                31483/syslogd 
```

- 查看某端口被占用的进程

```java
 [root@nbatest ~]# netstat -altp |grep 9999
 tcp    0   0 0.0.0.0:9999        0.0.0.0:*          LISTEN   16315/gate_applicat
```

- 得到PID后，使用netstat命令查询端口占用

```
netstat -nap | grep [pid]
```

- telnet
  - [Linux书签（03）用linux telnet命令监测服务器端口号](https://blog.csdn.net/itanping/article/details/96481668)







## 11.ls命令

```shell
$ ls -lhS
//ls -lhS  按照由大到小排序
//ls -Slhr   按照从小到大排序

> ls -alc # 按创建时间排序
> ls -alu # 按访问时间排序
```



## 12.du

- 查看当前文件夹下文件大小

```shell
1、（方法一）ls -lht会列出当前目录下每个文件的大小，同时也会给出当前目录下所有文件大小总和

     【查看谬个文件的大小，】

2、（方法二）du -sh *也会列出当前文件夹下所有文件对应的大小

     【把*替换为具体的文件名，会给出具体文件的大小】
     
du -s /usr/* | sort -rn   从大到小

du -s /usr/* | sort -n    从小到大

```

## 13.mv

- 批量修改文件名

```java
for f in `ls`; do mv $f `echo $f | sed -E 's/(.*)--(.*).mp4/\1.mp4/'`; done
  
for f in `ls`; do mv $f `echo $f | sed -E 's/(.*)BV1uK411P7JE(.*).mp4/\1.mp4/'`; done
```

- 筛选文件并移动文件

```shell
find . -name '10-*.dat' -exec mv {} ../ \;
 => -exec mv {} /mnt/mp3 \; - 运行mv命令。
 => {} - 字符 '{}' 代表find到的所有内容。
=>../表示当前用户目录的上一级目录
 => \; - 结束 /bin/mv 命令。
 
 find . -name  "*.mkv"   -exec  mv {} ~/Data/09Movies \;
 
 #当前目录下的文件，排除当前目录下的子目录
 find . -maxdepth 1  -name  "*.mkv"   -exec  mv  {} ./03movies \;
 
```

- find+mv筛选批量移动文件
- https://stackoverflow.com/questions/22388480/how-to-pipe-the-results-of-find-to-mv-in-linux



## 14.yum

```shell
# 查看yum的版本信息命令
yum info yum
# 查看具体安装包信息
rpm -qi centos-release-6-10.el6.centos.12.3.x86_64
# 查找yum可以安装的安装包
yum search ftp


命令：
#查看软件包
  yum list all                     ##列出yum源仓库里面的所有可用的安装包 
  yum list installed               ##列出所有已经安装的安装包  
  yum list available               ##列出没有安装的安装包
 #安装软件
  yum install softwarename         ##安装指定的软件
  yum reinstall softarename        ##重新安装指定的软件
  yum localinstall 第三方software   ##安装第三方文件并且会解决软件的依赖关系
  yum remove  softwarename         ##卸装指定的软件
 #查找软件的信息  
  yum info software                ##查看软的信息
  yum search keywords              ##根据关键字查找到相关安装包软件的信息
  yum whatprovides filename        ##查找包含指定文件的相关安装包
 #对于软件组
   yum groups list             ##列出软件组
   yum groups install         ##安装一个软件组
   yum group remove           ##卸载一个软件组

#开始之前看一下查看一下当前的配置里面有什么已经安装好的yum源仓库 
yum repolist

```

[快速查看yum的版本信息和yum仓库地址，安装包版本信息](https://blog.csdn.net/SwTesting/article/details/84718260)
[Linux系统的yum命令及yum源的设定](https://blog.csdn.net/hello_xiaozhuang/article/details/80112380)





## 16.xargs

```shell
jps  | grep "bigbang-server" | grep -v grep | awk '{print $1}'  | xargs echo
```









## 17.ssh

```shell
//指定端口号登录
ssh   hadoop@116.XXX.20.XXX:58422
```







## 18.防火墙

---

```shell
# 1:查看防火状态
systemctl status firewalld
service  iptables status

# 2:暂时关闭防火墙
systemctl stop firewalld
service  iptables stop

# 3:永久关闭防火墙
systemctl disable firewalld
chkconfig iptables off

# 4:重启防火墙
systemctl enable firewalld
service iptables restart


# 永久性打开某一端口
[root@centos7 elasticsearch-head]#  firewall-cmd --zone=public --add-port=9100/tcp --permanent
# 查看端口开启状态
[root@centos7 elasticsearch-head]# firewall-cmd --query-port=9100/tcp
no
# 重启防火墙
[root@centos7 elasticsearch-head]# firewall-cmd --reload

```







## 19.find

> 在使用find ／ -name 时，会打印很多含“permission denied”错误无用信息
>
> 意思是把 标准错误输出 重定向到 标准输出，grep -v 的意思是“获取相反”，具体参考grep命令

```shell
find / -name art  2>&1 | grep -v "Permission denied"
```



#### 查找linux下的java进程：

```shell
ps -ef | grep java
cd /proc/173957
ll cwd
[ 173957]$ ll cwd
lrwxrwxrwx 1 hadoop hadoop 0 May 14 14:06 cwd -> /app/hadoop/jerry/powerjob/server
```

```powershell
/proc/pid/cmdline 包含了用于开始进程的命令 ； -> cat cmdline 可以找到程序启动入口
/proc/pid/cwd包含了当前进程工作目录的一个链接 ；
/proc/pid/environ 包含了可用进程环境变量的列表 ；
/proc/pid/exe 包含了正在进程中运行的程序链接；
/proc/pid/fd/ 这个目录包含了进程打开的每一个文件的链接；
/proc/pid/mem 包含了进程在内存中的内容；
/proc/pid/stat包含了进程的状态信息；
/proc/pid/statm 包含了进程的内存使用信息。
```

- 查询多个日志文件下的关键词，并显示来源自哪个文件

```shell
find . -type f -name "*" | xargs grep "579f3ab7-02ee-454b-acf1-65068757874f"
```







## 20.Maven命令

- 输出Maven项目的目录结构:`tree >> D:/tree.txt`
- 输出maven依赖结构：mvn dependency:tree >D:/aa.txt 
- 跳过测试构建:`mvn clean install -DskipTests`





## 21.Xshell修改配色

- 临时性修改`grep`结果，下面是部分配色：

```
0 black
31 red
32 green
33 yellow
34 blue
35 purple
36 cyan
37 white
```

```shell
// vim ~/.bash_profile
$ export GREP_OPTIONS='--color=auto' GREP_COLOR='31'
$ ps axu | grep java
```





## 22.screen

```shell
screen -S yourname -> 新建一个叫yourname的session
screen -ls         -> 列出当前所有的session
screen -r yourname -> 回到yourname这个session
screen -d yourname -> 远程detach某个session
screen -d -r yourname -> 结束当前session并回到yourname这个session

session下使用如下命令
ctrl+a(C-a) 
C-a d -> detach，暂时离开当前session，将目前的 screen session (可能含有多个 windows) 丢到后台执行，并会回到还没进 screen 时的状态，此时在 screen session 里，每个 window 内运行的 process (无论是前台/后台)都在继续执行，即使 logout 也不影响。 


screen -X -S [session # you want to kill] quit
如  screen -X -S 4588 quit

```



## 23.alias

```shell
~/.bash_profile中添加后别名的设置永久生效

# Aliases
if [ -f ~/.alias ]; then
  . ~/.alias
fi


# 设置别名
[roc@roclinux ~]$ alias vi='vim'
```





## 24.查看内外网

- 查看外网ip

```shell
curl ifconfig.co
或
curl cip.cc
```

- 查看内网ip

```java
1、10开头的IP都是内网IP。即10.0.0.0 到 10.255.255.255是内网IP。不少自家拉的带宽路由分配的都是10开头的IP，这类都是内网IP。

2、以下IP段的地址都是内网IP地址。
（1）10.0.0.0 到 10.255.255.255
（2）172.16.0.0 到172.31.255.255
（3）192.168.0.0 到192.168.255.255
```







## 25.nc

- [nc命令详解](https://www.cnblogs.com/romatic/p/13344769.html?ivk_sa=1024320u)

- [【linux】linux间传输文件的简便方式 nc传输文件 nc命令教程](https://blog.csdn.net/qq_36268103/article/details/121894234)
- [nc - 网络工具箱中的「瑞士军刀」](https://www.cnblogs.com/wanng/p/nc-command.html)

```shell
服务端（接收方）
注意 : 接收文件的是服务端 可以理解为项目中的前端往服务端发送数据 接收数据的是服务端
输入命令: nc -l -v 5555 > streamx-console-service-1.2.2-release.tar.gz

客户端（发送方）输入命令
nc -v 192.168.168.168 5555 < streamx-console-service-1.2.2-release.tar.gz


```





## 26.$



```shell
$# 是启动脚本时携带的参数个数
-ne 是不等于
这个语句的意思是“如果shell的启动参数不等于1个”
$# 表示提供到shell脚本或者函数的参数总数；
$1 表示第一个参数。

 -ne 表示 不等于

另外：
整数比较
-eq     等于,如:if ["$a" -eq "$b" ]
-ne     不等于,如:if ["$a" -ne "$b" ]
-gt     大于,如:if ["$a" -gt "$b" ]
-ge    大于等于,如:if ["$a" -ge "$b" ]
-lt      小于,如:if ["$a" -lt "$b" ]
-le      小于等于,如:if ["$a" -le "$b" ]
<  小于(需要双括号),如:(("$a" < "$b"))
<=  小于等于(需要双括号),如:(("$a" <= "$b"))
>  大于(需要双括号),如:(("$a" > "$b"))
>=  大于等于(需要双括号),如:(("$a" >= "$b"))
```

- [Shell 脚本中 '$' 符号的多种用法](https://blog.csdn.net/jake_tian/article/details/97274630)







## 31.top

- [top命令详解](https://www.jianshu.com/p/8a6754f919c5)

```shell
查看某一进程占用的内存
top -pid 17434 -l 1 | tail -n 1 | awk '{print $0}'
说明：【-l 1】表示只执行1次，【tail -n 1】表示取最后一行结果，【awk '{print $8}'】表示取第8列的值（即内存值）。
Ps：根据应用名（如：Infoflow）查看进程号
ps -ef | grep Infoflow | grep -v grep | awk '{print $2}'
```



## 32.iftop

```shell
#查看路由表
netstat -rt
#监控网卡
sudo iftop -i en0
sudo iftop -i utun2
```





## 33.ipset

- [Linux常用命令-ipset](https://www.linuxtop.cn/command/ipset.html)



## 34.iostat

[Linux iostat命令详解](https://www.cnblogs.com/ftl1012/p/iostat.html)



## 41.Auth

- [Linux下查看用户列表](https://blog.csdn.net/rainbow702/article/details/50985672)







# B.Docker

Docker 是一个开源的容器化平台，允许开发者将应用及其依赖打包到一个轻量级、可移植的容器中，然后发布到任何支持 Docker 的系统上。以下是一些 Docker 的基础命令：

1. **安装 Docker**：访问 Docker 官方网站获取安装指南。

2. **启动 Docker 服务**：
   ```bash
   systemctl start docker
   ```

3. **设置 Docker 开机自启**：
   ```bash
   systemctl enable docker
   ```

4. **拉取一个镜像**：
   ```bash
   docker pull [OPTIONS] NAME[:TAG|@DIGEST]
   ```
   例如，拉取最新的 Ubuntu 镜像：
   ```bash
   docker pull ubuntu:latest
   ```

5. **查看当前已下载的镜像**：
   ```bash
   docker images
   ```

6. **运行一个容器**：
   ```bash
   docker run [OPTIONS] IMAGE[:TAG|@DIGEST] [COMMAND] [ARG...]
   ```
   例如，运行一个 Ubuntu 容器并启动一个 shell：
   ```bash
   docker run -it ubuntu:latest /bin/bash
   ```

7. **列出正在运行的容器**：
   ```bash
   docker ps
   ```

8. **停止一个运行中的容器**：
   ```bash
   docker stop CONTAINER
   ```

9. **重启一个容器**：
   ```bash
   docker restart CONTAINER
   ```

10. **删除一个容器**：
    ```bash
    docker rm CONTAINER
    ```

11. **删除一个镜像**：
    ```bash
    docker rmi IMAGE
    ```

12. **查看容器的日志**：
    ```bash
    docker logs CONTAINER
    ```

13. **进入一个运行中的容器**：
    ```bash
    docker exec -it CONTAINER /bin/bash
    ```

14. **构建一个镜像**：
    ```bash
    docker build -t NAME .
    ```
    或者使用 Dockerfile：
    ```bash
    docker build -t NAME .
    ```

15. **保存镜像为 tar 文件**：
    ```bash
    docker save -o FILENAME IMAGE
    ```

16. **从 tar 文件加载镜像**：
    ```bash
    docker load -i FILENAME
    ```

17. **搜索 Docker Hub 上的镜像**：
    ```bash
    docker search NAME
    ```

18. **退出容器**：
    ```bash
    exit
    ```

19. **查看 Docker 的信息**：
    ```bash
    docker info
    ```

20. **查看 Docker 的版本**：
    ```bash
    docker version
    ```

这些命令覆盖了 Docker 的基本操作，包括镜像和容器的创建、运行、停止、删除等。在使用 Docker 时，建议阅读 Docker 的官方文档以获得更深入的理解和学习高级功能。







---

## 50.番外

+ 输出文件的行数的几种方法：
  + ` awk 'END{print NR}' Applications.txt `	 
  + `wc -l test1.sh`
  + `sed -n '$=' Applications.txt`
+ 清空文件:`echo -n >  file`
+ 查询目录下文件的个数：`ls -lh| grep -c "^-"`
+ 比较 A，B文件的差集：`awk 'NR==FNR{ a[$1]=$1 } NR>FNR{ if(a[$1] == ""){ print $1}}'   delete_file_total_20200102_uniq   result_total.txt> file2.txt`

- 命令行复制一个文件到剪切板，并粘贴到`Typora`上,`cat ~/.vimrc | pbcopy`











## Reference

- [Linux中查询当前用户的命令总结](https://blog.csdn.net/csdn1198402320/article/details/84335639)
- [SED 简明教程](https://coolshell.cn/articles/9104.html)
- [awk 使用教程 - 通读篇（30分钟入门](https://cloud.tencent.com/developer/article/1159061)
- [AWK 简明教程](https://coolshell.cn/articles/9070.html)
- [Linux Shell常用技巧(四) awk](https://www.cnblogs.com/mchina/archive/2012/06/30/2571308.html)
- [每天一个linux命令(11)：nl命令](https://www.cnblogs.com/peida/archive/2012/11/01/2749048.html)
- [Linux 查询端口被占用命令](https://www.cnblogs.com/ming-blogs/p/11101423.html)
- [Xshell-grep高亮显示匹配项](https://www.jianshu.com/p/9944f96ea378)
- https://www.cnblogs.com/weifeng1463/p/9858048.html
- https://www.cnblogs.com/tianyajuanke/archive/2012/04/25/2470002.html
- [linux下查找java进程所在的目录](https://blog.csdn.net/glw0223/article/details/88823513)
- https://blog.csdn.net/glw0223/article/details/88823513
- https://blog.csdn.net/feit2417/article/details/82935801
- [Linux中的screen命令使用](https://blog.csdn.net/han0373/article/details/81352663)
- [用 echo 管道命令给sudo自动输入密码](https://blog.csdn.net/xushx_bigbear/article/details/12966625?%3E)
- [Linux中常用的查看系统信息的命令](https://www.linuxprobe.com/linux-cat-system.html
- [VIM超详细用法以及思维导图](https://zhuanlan.zhihu.com/p/77283813)
- [use_vim_as_ide](https://gitcode.net/mirrors/yangyangwithgnu/use_vim_as_ide)
- [Fuzzy finder(fzf+vim) 使用全指南](https://zhuanlan.zhihu.com/p/41859976)
- [sed替换^A（\001）,\002,\003等特殊字符](https://blog.csdn.net/qq_16018407/article/details/78901809)
- [Linux shell利用sed如何批量更改文件名详解](https://cloud.tencent.com/developer/article/1721039)
- [linux shell中"2>&1"含义](https://www.cnblogs.com/zhenghongxin/p/7029173.html)
- [shell中>/dev/null 2>&1](https://www.cnblogs.com/520playboy/p/6275022.html)
- [2>/dev/null和>/dev/null 2>&1和2>&1>/dev/null的区别](https://blog.csdn.net/longgeaisisi/article/details/90519690)
- [批量重命名的三种方法：内置 + sed 或 rename + 正则表达式【Mac&Linux】](https://blog.csdn.net/qq_33177268/article/details/119766446)
- [将Linux命令的结果作为下一个命令的参数](https://blog.csdn.net/permike/article/details/51957003)
- [Linux基础之cd无法进入xargs管道输出的目录问题解决方法](https://cloud.tencent.com/developer/article/1377673)
- [shell 文件表达式 参数[-e -d -f -h -eq -ne -lt -ne.....]](https://blog.csdn.net/u014472548/article/details/99831875)