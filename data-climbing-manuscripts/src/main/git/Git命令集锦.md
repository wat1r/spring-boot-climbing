## Git命令集锦

### 前言

### 正文
+  列出所有本地分支:`   git branch`
+ 列出所有远程分支:`git branch -r`
+ 列出所有本地分支和远程分支:`git branch -a`
+ 新建一个分支，并切换到该分支:`git checkout -b [branch]`
+ 切换到指定分支，并更新工作区:`git checkout [branch-name]`
+ **选择一个commit，合并进当前分支**:`git cherry-pick [commit]`
+ 合并指定分支到当前分支:`git merge [branch]`
+ 显示当前分支的最近几次提交:`git reflog`
+ 删除分支:`git branch -d [branch-name]`
+ 删除远程分支:
	+ `git push origin --delete [branch-name]`
	+ `git branch -dr [remote/branch]`
+ 上传本地指定分支到远程仓库:`git push [remote] [branch]`
+ 下载远程仓库的所有变动:`git fetch [remote]`
+ 显示所有远程仓库:`git remote -v`
+ 显示某个远程仓库的信息:
	+ `git remote show [remote]`
	+ `git push origin <本地分支名>/再同步到服务器`
+ 重置当前分支的HEAD为指定commit，同时重置暂存区和工作区，与指定commit一致:`git reset --hard [commit]
`
+ 暂时将未提交的变化移除，稍后再移入
	+ `git stash`
	+ `git stash pop`
+ 查看分支状态，本地分支远程分支
	+ `git status`
	+ `git branch -a` 	
+ 新建本地分支，push到远程分支
	+ `git branch test`
	+  `git checkout test`
	+ `git push origin test`
	+ 一句命令:`git checkout -b issue-13 && git push origin issue-13`
+ 删除本地分支：` git branch -d [branchname] `
+ 删除远程分支: `git push origin --delete [branchname] `
+ 切到`master`分支上，去单独的拿`feature`分支点的`62ecb3` 代码
	+ `git checkout master  `
	+  `git cherry-pick 62ecb3`



### Reference
- [如何将新建的项目完整的提交到gitlab上？](https://www.cnblogs.com/ssqq5200936/p/10749201.html)