## Vue教程与实施指南

### 比较

#### export与export default

```
export 和 export default的区别
1.export
import {axiosfetch} from './util';  //需要加花括号  可以一次导入一个也可以一次导入多个，但都要加括号
如果是两个方法
import {axiosfetch,post} from './util'; 

2.export default 
import axiosfetch from './util';  //不需要加花括号  只能一个一个导入
*export default有单次限制，只能使用export default向外暴露一次*
```

#### dd
