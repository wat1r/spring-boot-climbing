









### QNA

#### 1.测试

```go
# 报错 -> go run: cannot run *_test.go files (abc_test.go)
只有package为main时才能执行main方法，测试类需要test命名结尾,比如hello_test.go，在vscode执行该测试类，比如是下面的测试类：

package test

import (
	"fmt"
	"testing"
)

func TestMethod(t *testing.T) {
	for i := 0; i < 5; i++ {
		fmt.Printf("This is the %d iteration\n", i)
	}
}



```



### Reference

- https://learnku.com/go/t/24715

- https://github.com/unknwon/the-way-to-go_ZH_CN/blob/master/eBook/directory.md