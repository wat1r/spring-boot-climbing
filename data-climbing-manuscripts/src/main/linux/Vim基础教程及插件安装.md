## Vim基础教程及插件安装





硬件环境

```powershell
➜  ~ neofetch
                    'c.          frankcooper@FrankdeMacBook-Pro.local
                 ,xNMM.          ------------------------------------
               .OMMMMo           OS: macOS Catalina 10.15.7 19H1519 x86_64
               OMMM0,            Host: MacBookPro16,1
     .;loddo:' loolloddol;.      Kernel: 19.6.0
   cKMMMMMMMMMMNWMMMMMMMMMM0:    Uptime: 21 hours, 59 mins
 .KMMMMMMMMMMMMMMMMMMMMMMMWd.    Packages: 86 (brew)
 XMMMMMMMMMMMMMMMMMMMMMMMX.      Shell: zsh 5.7.1
;MMMMMMMMMMMMMMMMMMMMMMMM:       Resolution: 1792x1120@2x
:MMMMMMMMMMMMMMMMMMMMMMMM:       DE: Aqua
.MMMMMMMMMMMMMMMMMMMMMMMMX.      WM: Quartz Compositor
 kMMMMMMMMMMMMMMMMMMMMMMMMWd.    WM Theme: Blue (Light)
 .XMMMMMMMMMMMMMMMMMMMMMMMMMMk   Terminal: iTerm2
  .XMMMMMMMMMMMMMMMMMMMMMMMMK.   Terminal Font: Monaco 12
    kMMMMMMMMMMMMMMMMMMMMMMd     CPU: Intel i7-9750H (12) @ 2.60GHz
     ;KMMMMMMMWXXWMMMMMMMk.      GPU: Intel UHD Graphics 630, AMD Radeon Pro 5300M
       .cooc,.    .,coo:.        Memory: 10963MiB / 16384MiB

```









采用的`bundle`的方式安装的插件，也可以使用`Plug`的方式安装

```powershell
➜  .vim pwd
/Users/frankcooper/.vim
➜  .vim ls
autoload bundle   plugged
```

`vundle`安装命令

```powershell
# 安装插件
:BundleInstall
# 更新插件
:BundleUpdate
# 清除不需要的插件
:BundleClean
# 列出当前的插件
:BundleList
# 搜索插件
:BundleSearch
```



部分使用到cmake编译安装，编译安装YCM，这其中可能会遇到github端口的问题，下载包失败，需要手动下载解决

```powershell
brew install CMake
cd ~/.vim/bundle
git clone https://github.com/ycm-core/YouCompleteMe.git
git submodule update —init --recursive

```

`~/.vimrc`的配置

```powershell
" Configuration file for vim
set modelines=0		" CVE-2007-2438

" Normally we use vim-extensions. If you want true vi-compatibility
" remove change the following statements
set nocompatible	" Use Vim defaults instead of 100% vi compatibility
filetype off
set backspace=2		" more powerful backspacing


" Don't write backup file if vim is being called by "crontab -e"
au BufWrite /private/tmp/crontab.* set nowritebackup nobackup
" Don't write backup file if vim is being called by "chpass"
au BufWrite /private/etc/pw.* set nowritebackup nobackup

let skip_defaults_vim=1

set hlsearch
set nu!
syntax on

nnoremap <silent> <C-e> :NERDTree<CR>

" map <F3> :NERDTreeMirror<CR>
" map <F3> :NERDTreeToggle<CR>





let g:ycm_server_python_interpreter='/usr/bin/python3'
let g:ycm_global_ycm_extra_conf='~/.vim/.ycm_extra_conf.py'


" 自动补全配置
set completeopt=longest,menu "让Vim的补全菜单行为与一般IDE一致(参考VimTip1228)
autocmd InsertLeave * if pumvisible() == 0|pclose|endif "离开插入模式后自动关闭预览窗口
inoremap <expr> <CR> pumvisible() ? "\<C-y>" : "\<CR>" "回车即选中当前项
"上下左右键的行为 会显示其他信息
inoremap <expr> <Down> pumvisible() ? "\<C-n>" : "\<Down>"
inoremap <expr> <Up> pumvisible() ? "\<C-p>" : "\<Up>"
inoremap <expr> <PageDown> pumvisible() ? "\<PageDown>\<C-p>\<C-n>" : "\<PageDown>"
inoremap <expr> <PageUp> pumvisible() ? "\<PageUp>\<C-p>\<C-n>" : "\<PageUp>"

"youcompleteme 默认tab s-tab 和自动补全冲突
"let g:ycm_key_list_select_completion=['<c-n>']
let g:ycm_key_list_select_completion = ['<Down>']
"let g:ycm_key_list_previous_completion=['<c-p>']
let g:ycm_key_list_previous_completion = ['<Up>']
let g:ycm_confirm_extra_conf=0 "关闭加载.ycm_extra_conf.py提示

let g:ycm_collect_identifiers_from_tags_files=1 " 开启 YCM 基于标签引擎
let g:ycm_min_num_of_chars_for_completion=2 " 从第2个键入字符就开始罗列匹配项
let g:ycm_cache_omnifunc=0 " 禁止缓存匹配项,每次都重新生成匹配项
let g:ycm_seed_identifiers_with_syntax=1 " 语法关键字补全
nnoremap <F5> :YcmForceCompileAndDiagnostics<CR> "force recomile with syntastic
"nnoremap <leader>lo :lopen<CR> "open locationlist
"nnoremap <leader>lc :lclose<CR> "close locationlist
inoremap <leader><leader> <C-x><C-o>
"在注释输入中也能补全
let g:ycm_complete_in_comments = 1
"在字符串输入中也能补全
let g:ycm_complete_in_strings = 1
"注释和字符串中的文字也会被收入补全
let g:ycm_collect_identifiers_from_comments_and_strings = 0

nnoremap <leader>jd :YcmCompleter GoToDefinitionElseDeclaration<CR> " 跳转到定义处



autocmd ColorScheme janah highlight Normal ctermbg=235





" set the runtime path to include Vundle and initialize
set rtp+=~/.vim/bundle/Vundle.vim

call vundle#begin()

Plugin 'VundleVim/Vundle.vim'
Plugin 'Valloric/YouCompleteMe'
Plugin 'scrooloose/nerdtree'
" Plugin 'mhinz/vim-startify' 
" Plugin 'tpope/vim-fugitive' 
" Plugin 'lfv89/vim-interestingwords'
" Plugin 'dyng/ctrlsf.vim'


call vundle#end()
filetype plugin indent on    " required

```



`macos`上因为自带的`vim`默认不支持`python3`，需要安装`vim`或者`macvim`

```powershell
brew install vim 
or
brew install macvim

➜  ~ vim --version | grep python
+comments          +libcall           -python            +visual
+conceal           +linebreak         +python3           +visualextra
```







编译安装语言支持

```powershell
➜  YouCompleteMe git:(master) pwd
/Users/frankcooper/.vim/bundle/YouCompleteMe


YouCompleteMe git:(master) python3 install.py --clang-completer
python3 install.py --java-completer
python3 install.py --go-completer
python3 install.py   --gocode-completer
python3 install.py   --omnisharp-completer
 
 或者安装所有
python3 install.py  --all
 
```







### Reference

- [YCM-full-installation-guide](https://github.com/ycm-core/YouCompleteMe#full-installation-guide)

- [YouCompleteMe](https://github.com/ycm-core/YouCompleteMe)

- [VimAwesome](https://vimawesome.com/)
- [neocomplete](https://github.com/Shougo/neocomplete.vim)

