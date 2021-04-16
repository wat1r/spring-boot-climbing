## Java并发变成JUC实践

## 王者荣耀开始游戏等待

> 10个玩家，10个线程，模拟加载等待的过程

```java
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Frank Cooper
 * @date: 2021/4/15 21:38
 * @description: 模拟王者荣耀10个玩家加载游戏
 */
public class CountDownLatchExample3 {
    public static void main(String[] args) throws InterruptedException {
        test();

    }

    static int COUNT = 10;

    public static void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT);
        CountDownLatch latch = new CountDownLatch(COUNT);
        Random random = new Random(10);
        String[] all = new String[COUNT];
        for (int j = 0; j < COUNT; j++) {
            int k = j;
            executorService.submit(() -> {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    all[k] = i + "%";
                    System.out.print("\r" + Arrays.toString(all));//\r覆盖打印
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("\n开始游戏");
        executorService.shutdown();
    }
}
```

