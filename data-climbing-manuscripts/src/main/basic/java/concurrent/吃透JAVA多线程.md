# 吃透JAVA多线程









## 吃透JAVA多线程(一)：Semophore

### 简介

`Semaphore`是计数信号量。`Semaphore`管理一系列许可。

每个`acquire`方法阻塞，直到有一个许可证可以获得然后拿走一个许可证；

每个`release`方法增加一个许可，这可能会释放一个阻塞的`acquire`方法。然而，其实并没有实际的许可这个对象，`Semaphore`只是维持了一个可获得许可证的数量。

举例：

例1：停车场入口立着的那个显示屏，每有一辆车进入停车场显示屏就会显示剩余车位减1，每有一辆车从停车场出去，显示屏上显示的剩余车辆就会加1，当显示屏上的剩余车位为0时，停车场入口的栏杆就不会再打开，车辆就无法进入停车场了，直到有一辆车从停车场出去为止。

例2：在学生时代都去餐厅打过饭，假如有3个窗口可以打饭，同一时刻也只能有3名同学打饭。第四个人来了之后就必须在外面等着，只要有打饭的同学好了，就可以去相应的窗口了 。

### API

#### 构造方法

```java
//创建具有给定的许可数和非公平的公平设置的 Semaphore
Semaphore(int permits)   
//创建具有给定的许可数和给定的公平设置的 Semaphore
Semaphore(int permits, boolean fair)   
```

#### 函数

```java
//从此信号量获取给定数目的许可，在提供这些许可前一直将线程阻塞，或者线程已被中断，就好比是一个学生占两个窗口。这同时也对应了相应的release方法
acquire(int permits)
//释放给定数目的许可，将其返回到信号量。这个是对应于上面的方法，一个学生占几个窗口完事之后还要释放多少
release(int permits)
//返回此信号量中当前可用的许可数。也就是返回当前还有多少个窗口可用  
availablePermits()
//根据指定的缩减量减小可用许可的数目
reducePermits(int reduction)
//查询是否有线程正在等待获取资源
hasQueuedThreads()
//返回正在等待获取的线程的估计数目。该值仅是估计的数字
getQueueLength()
//如果在给定的等待时间内此信号量有可用的所有许可，并且当前线程未被中断，则从此信号量获取给定数目的许可
tryAcquire(int permits, long timeout, TimeUnit unit)
//从此信号量获取给定数目的许可，在提供这些许可前一直将线程阻塞
8、acquireUninterruptibly(int permits)
```

### 案例

#### Case1

每个停车场入口都有一个提示牌，上面显示着停车场的剩余车位还有多少，当剩余车位为0时，不允许车辆进入停车场，直到停车场里面有车离开停车场，这时提示牌上会显示新的剩余车位数。
**详细需求：**

- 停车场容纳总停车量3，每次只能有3台车辆在停车场内
- 当一辆车进入停车场后，显示牌的剩余车位数响应的减1
- 每有一辆车驶出停车场后，显示牌的剩余车位数响应的加1
- 停车场剩余车位不足时，车辆只能在外面等待

```java
package basic.concurrent.sema;

import com.google.common.base.Stopwatch;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class TestCar {
    //停车场同时容纳的车辆10
    private static Semaphore semaphore = new Semaphore(3);

    static Stopwatch stopwatch = Stopwatch.createStarted();

    public static void main(String[] args) {
        //模拟10辆车进入停车场
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    System.out.println("====" + Thread.currentThread().getName() + "来到停车场");
                    if (semaphore.availablePermits() == 0) {
                        System.out.printf("%s :你好，车位不足，请耐心等待%n", Thread.currentThread().getName());
                    }
                    semaphore.acquire();//获取令牌尝试进入停车场
                    System.out.println(Thread.currentThread().getName() + " " + stopwatch.toString() + " 成功进入停车场");
                    Thread.sleep(new Random().nextInt(10000));//模拟车辆在停车场停留的时间
                    System.out.println(Thread.currentThread().getName() + " " + stopwatch.toString() + " 驶出停车场");
                    semaphore.release();//释放令牌，腾出停车场车位
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, i + "号车");
            thread.start();
        }
    }
}
```

#### 打印的结果

- 以这次的打印为例子：第一次进入的是 0 3 2 号车这3辆车

```log
====0号车来到停车场
====3号车来到停车场
====2号车来到停车场
====1号车来到停车场
====5号车来到停车场
====4号车来到停车场
====6号车来到停车场
====7号车来到停车场
====8号车来到停车场
====9号车来到停车场
4号车 :你好，车位不足，请耐心等待
8号车 :你好，车位不足，请耐心等待
9号车 :你好，车位不足，请耐心等待
1号车 :你好，车位不足，请耐心等待
6号车 :你好，车位不足，请耐心等待
5号车 :你好，车位不足，请耐心等待
7号车 :你好，车位不足，请耐心等待
0号车 67.07 ms 成功进入停车场
3号车 67.07 ms 成功进入停车场
2号车 67.13 ms 成功进入停车场
3号车 5.418 s 驶出停车场
4号车 5.419 s 成功进入停车场
0号车 6.636 s 驶出停车场
8号车 6.637 s 成功进入停车场
2号车 9.454 s 驶出停车场
9号车 9.454 s 成功进入停车场
4号车 10.49 s 驶出停车场
1号车 10.49 s 成功进入停车场
8号车 10.70 s 驶出停车场
6号车 10.70 s 成功进入停车场
9号车 11.12 s 驶出停车场
5号车 11.12 s 成功进入停车场
5号车 19.28 s 驶出停车场
7号车 19.28 s 成功进入停车场
1号车 19.67 s 驶出停车场
7号车 19.93 s 驶出停车场
6号车 20.07 s 驶出停车场
```









## 吃透JAVA多线程(二)：LockSupport

### 简介

LockSupport类的核心方法其实就两个：`park()`和`unpark()`，其中`park()`方法用来阻塞当前调用线程，`unpark()`方法用于唤醒指定线程。
这其实和Object类的wait()和signal()方法有些类似，但是LockSupport的这两种方法从语意上讲比Object类的方法更清晰，而且可以针对指定线程进行阻塞和唤醒。

> LockSupport类使用了一种名为Permit（许可）的概念来做到阻塞和唤醒线程的功能，可以把许可看成是一种(0,1)信号量（Semaphore），但与 Semaphore 不同的是，许可的累加上限是1。
> 初始时，permit为0，当调用`unpark()`方法时，线程的permit加1，当调用`park()`方法时，如果permit为0，则调用线程进入阻塞状态。

### API

```java
public static void park(Object blocker); // 暂停当前线程
public static void parkNanos(Object blocker, long nanos); // 暂停当前线程，不过有超时时间的限制
public static void parkUntil(Object blocker, long deadline); // 暂停当前线程，直到某个时间
public static void park(); // 无期限暂停当前线程
public static void parkNanos(long nanos); // 暂停当前线程，不过有超时时间的限制
public static void parkUntil(long deadline); // 暂停当前线程，直到某个时间
public static void unpark(Thread thread); // 恢复当前线程
public static Object getBlocker(Thread t);
```

### 对比wait()和notify/notifyAll()

1. `LockSupport`不需要在同步代码块里 ，线程间不需要维护一个共享的同步对象，实现了线程间的解耦,主要针对`Thread`，`Object.wait()`是以对象为纬度,阻塞当前的线程和唤醒单个(随机)或者所有线程。
2. `unpark`函数可以先于`park`调用，不需要担心线程间的执行的先后顺序。
3. 多次调用`unpark`方法和调用一次`unpark`方法效果一样，比如线程`A`连续调用两次`LockSupport.unpark(B)`方法唤醒线程`B`，然后线程B调用两次`LockSupport.park()`方法， 线程`B`依旧会被阻塞。因为两次`unpark`调用效果跟一次调用一样，只能让线程B的第一次调用`park`方法不被阻塞，第二次调用依旧会阻塞

### 总结：

`park`: 阻塞线程,线程在一下三种情况下会被阻塞:

1. 调用`unpark`方法,释放该线程的许可
2. 该线程被中断
3. 到期时间

### Demo

- #### test1

```java
private static void lockSupportOne() {
    Thread t1 = new Thread(() -> {
        LockSupport.park();
        System.out.println("lock support running");
    });
    t1.start();
    LockSupport.unpark(t1);
    System.out.println("lock support end");
}
```

- #### test2

```java
   private static Thread mainThread;

    public static void main(String[] args) throws InterruptedException {
        ThreadA ta = new ThreadA("ta");
        // 获取主线程
        mainThread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + " start ta");
        ta.start();
        System.out.println(Thread.currentThread().getName() + " block");
        // 主线程阻塞
        LockSupport.park(mainThread);
        System.out.println(Thread.currentThread().getName() + " continue");
    }

    static class ThreadA extends Thread {
        public ThreadA(String name) {
            super(name);
        }

        public void run() {
            System.out.println(Thread.currentThread().getName() + " wake up others");
            // 唤醒“主线程”
            LockSupport.unpark(mainThread);
        }
    }

---
//打印结果
/**
main start ta
main block
ta wake up others
main continue
**/
```

### 源码







## Reference

- [jstack 打印 Java 进程堆栈信息](http://einverne.github.io/post/2017/09/jstack-usage.html)

- [LockSupport解析与使用](https://blog.csdn.net/secsf/article/details/78560013)



