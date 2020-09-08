## Git命令集锦

### 前言

### 正文

#### 主体git操作

```shell
# 列出所有本地分支:
git branch
# 列出所有远程分支:
git branch -r
# 列出所有本地分支和远程分支:
git branch -a
# 新建一个分支，并切换到该分支:
git checkout -b [branch]
# 切换到指定分支，并更新工作区:
git checkout [branch-name]
# 选择一个commit，合并进当前分支:
git cherry-pick [commit]
#合并指定分支到当前分支:
git merge [branch]
#显示当前分支的最近几次提交:
git reflog



 
#上传本地指定分支到远程仓库:
git push [remote] [branch]
#下载远程仓库的所有变动:
git fetch [remote]
#显示所有远程仓库:
git remote -v
#显示某个远程仓库的信息:
git remote show [remote]
git push origin <本地分支名>/再同步到服务器
#重置当前分支的HEAD为指定commit，同时重置暂存区和工作区，与指定commit一致:
git reset --hard [commit]

#查看分支状态，本地分支远程分支
git status
git branch -a 	
#新建本地分支，push到远程分支
git branch test
git checkout test
git push origin test
#一句命令:
git checkout -b issue-13 && git push origin issue-13
#切到master分支上，去单独的拿feature分支点的62ecb3 代码
git checkout master  
git cherry-pick 62ecb3
# 重命名本地分支并且推到远程仓
# 本地分支重命名
 git branch -m oldName  newName
# 将重命名后的分支推送到远程
git push origin newName


====delete branch
#删除本地分支： 
git branch -d [branchname] 
#删除远程分支:
git push origin --delete [branchname] 
# 删除远程的旧分支
git push --delete origin oldName
#删除分支:
git branch -d [branch-name]
#删除远程分支:
git push origin --delete [branch-name]
     git branch -dr [remote/branch]
## 删除本地分支并且删除远程分支
 git branch -d issue-18  && git push origin -d  issue-18

====log
 git log --pretty=oneline

====tag
# tag 推送到远程分支上，需要另外执行 tag 的推送命令
git tag <tagName>   -m "this is a test " //创建本地tag
git push origin <tagName> //推送到远程仓库
# 存在很多未推送的本地标签，你想一次全部推送的话，可以使用一下的命令：
git push origin --tags  
# 查看本地某个 tag 的详细信息：
git show <tagName>
# 检出标签
git checkout -b <branchName> <tagName>
# 查看远程所有 tag：
git ls-remote --tags origin
# 删除本地tag 并删除 远程tag
git tag -d 1.0.0  && git push origin -d 1.0.0



# 将本地未提交更改合并到另一个Git分支中
git stash
git checkout branch2
git stash list    ＃检查在不同分支中创建的各种存储
git stash apply x   ＃选择正确的一个
#暂时将未提交的变化移除，稍后再移入
git stash
git stash pop


```





### [Reference]()
- [如何将新建的项目完整的提交到gitlab上？](https://www.cnblogs.com/ssqq5200936/p/10749201.html)
- [github Tags和Branch分支相关操作（三）](https://blog.csdn.net/zjws23786/article/details/71159805)
- [Git的tag作用和使用场景以及branch的区别](https://blog.csdn.net/lcgoing/article/details/86241784)
- [版本号命名规则](https://blog.csdn.net/yimcarson/article/details/83894841)
- [搞定Git添加Tag的方法总结](https://www.cnblogs.com/bescheiden/articles/11126319.html)
- [git-merge完全解析](https://www.jianshu.com/p/58a166f24c81)
- [Git错误non-fast-forward后的冲突解决](https://blog.csdn.net/QQ736238785/article/details/79767115)
- [*git* *merge*和*git* *merge* --*no-ff*的区别 - 简书](https://www.baidu.com/link?url=huSmFIXBirdxzyYA8XMoGVAFo-5a12QHLWvMj9SaiKSNk52a6KHlknEXqP-UAmaw&ie=UTF-8&f=8&tn=baidu&wd=git merge --no-ff&oq=git merge --no-ff&rqlang=cn)
-  [如何将本地未提交更改合并到另一个Git分支中](https://blog.csdn.net/u013986317/article/details/106967998/)

- https://talktocomputer.site/blogs/84/