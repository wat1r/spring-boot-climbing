

![截屏2021-05-24 下午10.16.39](/Users/frankcooper/Desktop/截屏2021-05-24 下午10.16.39.png)



## `Netty`时间轮源码

#### - 构造函数

```java
/**
threadFactory:用来创建worker线程
 tickDuration:每一个格子的时间即精度
 unit: 时间单位
 ticksPerWheel：一圈有几格
 leakDetection:是否开启内存泄露检测
 
**/
public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection, long maxPendingTimeouts) {
        /*.参数传递+参数校验..*/
        else {
            //创建时间轮的基本数据结构：数组
            this.wheel = createWheel(ticksPerWheel);
            //限制wheel.length的2的次方,即 tick &(wheel.length - 1) = tick%wheel.length
            //因为一圈的长度为2的n次方，mask = 2^n-1后低位将全部是1
            this.mask = this.wheel.length - 1;
            //转化成纳秒
            this.tickDuration = unit.toNanos(tickDuration);
            //防止溢出 Long.MAX_VALUE
            if (this.tickDuration >= 9223372036854775807L / (long)this.wheel.length) {
                throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", tickDuration, 9223372036854775807L / (long)this.wheel.length));
            } else {
                //创建worker线程
                this.workerThread = threadFactory.newThread(this.worker);
                //内存泄露检测，当HashedWheelTimer的实例超过当前cpu可用核数*4的时候，将发出警告
                this.leak = !leakDetection && this.workerThread.isDaemon() ? null : leakDetector.track(this);
                this.maxPendingTimeouts = maxPendingTimeouts;
                //当实例数超过当前cpu的可用核数的最大的实例为64个时
                if (INSTANCE_COUNTER.incrementAndGet() > 64 && WARNED_TOO_MANY_INSTANCES.compareAndSet(false, true)) {
                    reportTooManyInstances();
                }

            }
        }
    }
```

#### - `createWheel`

```java
// ticksPerWheel：一圈有几格
private static HashedWheelTimer.HashedWheelBucket[] createWheel(int ticksPerWheel) {
   /.../
       
    else {
        //把一个数，变成最接近的2^n的数字
        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        //创建一个数组链表
        HashedWheelTimer.HashedWheelBucket[] wheel = new HashedWheelTimer.HashedWheelBucket[ticksPerWheel];
		//初始化，每个槽位初始化为一个双向链表
        for(int i = 0; i < wheel.length; ++i) {
            wheel[i] = new HashedWheelTimer.HashedWheelBucket();
        }
        return wheel;
    }
```

#### - `normalizeTicksPerWheel`

```java
//把一个数，变成最接近的2^n的数字
private static int normalizeTicksPerWheel(int ticksPerWheel) {
    int normalizedTicksPerWheel;
    for(normalizedTicksPerWheel = 1; normalizedTicksPerWheel < ticksPerWheel; normalizedTicksPerWheel <<= 1) {
    }
    return normalizedTicksPerWheel;
}
```

等价于下面的写法:

```java
static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```

#### - `newTimeout`

```java
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
       /*...*/
           else {
            long pendingTimeoutsCount = this.pendingTimeouts.incrementAndGet();
                /*...*/
               else {
                //启动时间轮
                this.start();
                  //deadline = 当前时间-延迟的时间-开始时间
                long deadline = System.nanoTime() + unit.toNanos(delay) - this.startTime;
                if (delay > 0L && deadline < 0L) {
                    deadline = 9223372036854775807L;
                }
				//创建任务，该任务是放置在mpsc队列中（MpscLinkedQueue，与JDK通过锁实现的LinkedBlockingQueue不同，MpscLinkedQueue是一种针对Netty中NIO任务设计的一种队列，允许有多个生产者，只有一个消费者的队列）
                HashedWheelTimer.HashedWheelTimeout timeout = new HashedWheelTimer.HashedWheelTimeout(this, task, deadline);
                this.timeouts.add(timeout);
                return timeout;
            }
        }
    }
```

#### - `start`

```java
    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case WORKER_STATE_INIT:
                if (WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    workerThread.start();
                }
                break;
            case WORKER_STATE_STARTED:
                break;
            case WORKER_STATE_SHUTDOWN:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // Wait until the startTime is initialized by the worker.
        while (startTime == 0) {
            try {
                startTimeInitialized.await();
            } catch (InterruptedException ignore) {
                // Ignore - it will be ready very soon.
            }
        }
```

#### - 工作线程`Worker`的执行方法

```java
 @Override
        public void run() {
            // Initialize the startTime.
            startTime = System.nanoTime();
            if (startTime == 0) {
                // We use 0 as an indicator for the uninitialized value here, so make sure it's not 0 when initialized.
                startTime = 1;
            }

            // Notify the other threads waiting for the initialization at start().
            startTimeInitialized.countDown();

            do {
                //等待执行任务时间的到来
                final long deadline = waitForNextTick();
                if (deadline > 0) {
                    int idx = (int) (tick & mask);
                    processCancelledTasks();//处理那些被取消的任务，弹出timeOut任务，移除
                    //找到之前任务放入mpsc队列在时间轮上的槽位
                    HashedWheelBucket bucket =
                            wheel[idx];
                    //将加入mpsc队列的任务加入到时间轮的槽位中
                    transferTimeoutsToBuckets();
                    //处理时间到了的任务，判断状态，做一些移除操作
                    bucket.expireTimeouts(deadline);
                    tick++;//记录一次移动,模拟指针走了一格
                }
            } while (WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == WORKER_STATE_STARTED);
			//清理任务
            // Fill the unprocessedTimeouts so we can return them from stop() method.
            for (HashedWheelBucket bucket: wheel) {
                bucket.clearTimeouts(unprocessedTimeouts);
            }
            for (;;) {
                HashedWheelTimeout timeout = timeouts.poll();
                if (timeout == null) {
                    break;
                }
                if (!timeout.isCancelled()) {
                    unprocessedTimeouts.add(timeout);
                }
            }
            processCancelledTasks();
        }
```

#### - `expireTimeouts`

```java
        public void expireTimeouts(long deadline) {
            HashedWheelTimeout timeout = head;//链表的头节点
			
            // process all timeouts
            while (timeout != null) {
                HashedWheelTimeout next = timeout.next;//移到下一个节点
                if (timeout.remainingRounds <= 0) {//轮次<=0时，删除该及节点，到这一轮了
                    next = remove(timeout);
                    if (timeout.deadline <= deadline) {//时间也到了
                        timeout.expire();//调用run()方法，开始执行
                    } else {
                        // The timeout was placed into a wrong slot. This should never happen.
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                } else if (timeout.isCancelled()) {//节点标记取消
                    next = remove(timeout);
                } else {//轮次-1
                    timeout.remainingRounds --;
                }
                timeout = next;
            }
        }
```

- #### `transferTimeoutsToBuckets`

```java
        private void transferTimeoutsToBuckets() {
            // transfer only max. 100000 timeouts per tick to prevent a thread to stale the workerThread when it just
            // adds new timeouts in a loop.
            //每次最多处理100000个，否则会有大量延迟
            for (int i = 0; i < 100000; i++) {
                HashedWheelTimeout timeout = timeouts.poll();
                if (timeout == null) {
                    // all processed
                    break;
                }
                if (timeout.state() == HashedWheelTimeout.ST_CANCELLED) {
                    // Was cancelled in the meantime.
                    continue;
                }
				//算出时间
                long calculated = timeout.deadline / tickDuration;
                //算出第几轮
                timeout.remainingRounds = (calculated - tick) / wheel.length;
				
                final long ticks = Math.max(calculated, tick); // Ensure we don't schedule for past.
                //计算防止在哪个槽位
                int stopIndex = (int) (ticks & mask);
				//塞入位中，插入链表
                HashedWheelBucket bucket = wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }
```

- #### `waitForNextTick`

  - 通过 `tickDuration `和此时已经滴答的次数算出下一次需要检查的时间，时候未到就`sleep`等待

```java
 private long waitForNextTick() {
            long deadline = tickDuration * (tick + 1);//计算一次需要检查的时间
			
            for (;;) {
                //+ 999999保证足够的sleep时间，比如deadline-currentTime 为5纳秒，5纳秒转1毫秒就是0 ，实际时间未到
                final long currentTime = System.nanoTime() - startTime;
                long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;

                if (sleepTimeMs <= 0) {//不用再等了，时间已经到了
                    if (currentTime == Long.MIN_VALUE) {
                        return -Long.MAX_VALUE;
                    } else {
                        return currentTime;
                    }
                }

                // Check if we run on windows, as if thats the case we will need
                // to round the sleepTime as workaround for a bug that only affect
                // the JVM if it runs on windows.
                //
                // See https://github.com/netty/netty/issues/356
                //windows下的bug，sleep时间是10的整数倍
                if (PlatformDependent.isWindows()) {
                    sleepTimeMs = sleepTimeMs / 10 * 10;
                }

                try {
                    Thread.sleep(sleepTimeMs);//等待时间到来
                } catch (InterruptedException ignored) {
                    if (WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }
```

#### 总结

- `Netty`中时间轮的实现是通过轮数`rounds`实现，精度通过`tickDuration`控制，工作线程除了处理执行到期的任务还附带做了其他操作，任务不一定会被完全精准的执行
- 任务的执行如果不是新起一个线程，或者将任务扔到线程池执行，那么耗时的任务会阻塞下个任务的执行
- 务数很多，通过分批执行，并且增删任务的时间复杂度都是`O(1)`



### `Kafka`时间轮源码









### `PowerJob`时间轮源码

- 版本：`4.0.1`

#### - 构造函数

```java
  /**
     * 新建时间轮定时器
     * @param tickDuration 时间间隔，单位毫秒（ms）
     * @param ticksPerWheel 轮盘个数
     * @param processThreadNum 处理任务的线程个数，0代表不启用新线程（如果定时任务需要耗时操作，请启用线程池）
     */
    public HashedWheelTimer(long tickDuration, int ticksPerWheel, int processThreadNum) {

        this.tickDuration = tickDuration;

        // 初始化轮盘，大小格式化为2的N次，可以使用 & 代替取余
        int ticksNum = CommonUtils.formatSize(ticksPerWheel);
        wheel = new HashedWheelBucket[ticksNum];
        for (int i = 0; i < ticksNum; i++) {//初始化双端链表
            wheel[i] = new HashedWheelBucket();
        }
        mask = wheel.length - 1;//掩码

        // 初始化执行线程池
        if (processThreadNum <= 0) {
            taskProcessPool = null;
        }else {
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("HashedWheelTimer-Executor-%d").build();
            BlockingQueue<Runnable> queue = Queues.newLinkedBlockingQueue(16);
            int core = Math.max(Runtime.getRuntime().availableProcessors(), processThreadNum);
            //初始化线程池
            taskProcessPool = new ThreadPoolExecutor(core, 4 * core,
                    60, TimeUnit.SECONDS,
                    queue, threadFactory, RejectedExecutionHandlerFactory.newCallerRun("PowerJobTimeWheelPool"));
        }
		
        startTime = System.currentTimeMillis();

        // 启动后台线程 
        indicator = new Indicator();
        new Thread(indicator, "HashedWheelTimer-Indicator").start();
    }
```

#### - `formatSize`

```java
    /**
     * 将大小格式化为 2的N次
     *
     * @param cap 初始大小
     * @return 格式化后的大小，2的N次
     */
    public static int formatSize(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        //MAXIMUM_CAPACITY = 1 << 30 相当于 2^30
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```

#### - `Indicator`模拟指针转动的处理器

##### `run()`

```java
        @Override
        public void run() {

            while (!stop.get()) {

                // 1. 将任务从队列推入时间轮
                pushTaskToBucket();
                // 2. 处理取消的任务
                processCanceledTasks();
                // 3. 等待指针跳向下一刻
                tickTack();
                // 4. 执行定时任务
                int currentIndex = (int) (tick & mask);//拿到当前的槽位，获取到队列中的任务
                HashedWheelBucket bucket = wheel[currentIndex];
                bucket.expireTimerTasks(tick);

                tick ++;//指针转动一次
            }
            latch.countDown();
        }

```

##### `pushTaskToBucket`

```java
// 将队列中的任务推入时间轮中      
private void pushTaskToBucket() {

            while (true) {
                HashedWheelTimerFuture timerTask = waitingTasks.poll();//弹出任务
                if (timerTask == null) {
                    return;
                }

                // 总共的偏移量
                long offset = timerTask.targetTime - startTime;
                // 总共需要走的指针步数
                timerTask.totalTicks = offset / tickDuration;
                // 取余计算 bucket index 计算槽位 获取槽位中的任务
                int index = (int) (timerTask.totalTicks & mask);
                HashedWheelBucket bucket = wheel[index];

                // TimerTask 维护 Bucket 引用，用于删除该任务
                timerTask.bucket = bucket;

                if (timerTask.status == HashedWheelTimerFuture.WAITING) {//处于等待中的任务
                    bucket.add(timerTask);
                }
            }
        }
```



##### `processCanceledTasks`

```java
    private final Queue<HashedWheelTimerFuture> waitingTasks = Queues.newLinkedBlockingQueue();
    private final Queue<HashedWheelTimerFuture> canceledTasks = Queues.newLinkedBlockingQueue();

        /**
         * 处理被取消的任务
         */
        private void processCanceledTasks() {
            while (true) {
                HashedWheelTimerFuture canceledTask = canceledTasks.poll();//弹出取消的队列中的任务
                if (canceledTask == null) {
                    return;
                }
                // 从链表中删除该任务（bucket为null说明还没被正式推入时间格中，不需要处理）
                if (canceledTask.bucket != null) {
                    canceledTask.bucket.remove(canceledTask);
                }
            }
        }
```

##### `tickTack`

```java
        /**
         * 模拟指针转动，当返回时指针已经转到了下一个刻度
         */
        private void tickTack() {

            // 下一次调度的绝对时间
            long nextTime = startTime + (tick + 1) * tickDuration;
            long sleepTime = nextTime - System.currentTimeMillis();//等待的时间

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                }catch (Exception ignore) {
                }
            }
        }
```

##### `expireTimerTasks`

```java
 public void expireTimerTasks(long currentTick) {
			
            removeIf(timerFuture -> {
                // processCanceledTasks 后外部操作取消任务会导致 BUCKET 中仍存在 CANCELED 任务的情况
                if (timerFuture.status == HashedWheelTimerFuture.CANCELED) {
                    return true;
                }
				
                if (timerFuture.status != HashedWheelTimerFuture.WAITING) {
                    log.warn("[HashedWheelTimer] impossible, please fix the bug");
                    return true;
                }

                // 本轮直接调度 
                if (timerFuture.totalTicks <= currentTick) {

                    if (timerFuture.totalTicks < currentTick) {
                        log.warn("[HashedWheelTimer] timerFuture.totalTicks < currentTick, please fix the bug");
                    }

                    try {
                        // 提交执行 开始提交执行任务
                        runTask(timerFuture);
                    }catch (Exception ignore) {
                    } finally {
                        timerFuture.status = HashedWheelTimerFuture.FINISHED;
                    }
                    return true;
                }

                return false;
            });

        }
```

##### `runTask`

```java
    private void runTask(HashedWheelTimerFuture timerFuture) {
        timerFuture.status = HashedWheelTimerFuture.RUNNING;//标记任务的状态为RUNNING
        if (taskProcessPool == null) {
            timerFuture.timerTask.run();
        }else {
            taskProcessPool.submit(timerFuture.timerTask);//执行
        }
    }
```

##### 包装类`HashedWheelTimerFuture`

```java
        // 预期执行时间
        private final long targetTime;
        private final TimerTask timerTask;

        // 所属的时间格，用于快速删除该任务
        private HashedWheelBucket bucket;
        // 总圈数
        private long totalTicks;
        // 当前状态 0 - 初始化等待中，1 - 运行中，2 - 完成，3 - 已取消
        private int status;

        // 状态枚举值
        private static final int WAITING = 0;
        private static final int RUNNING = 1;
        private static final int FINISHED = 2;
        private static final int CANCELED = 3;
```



#### 添加到时间轮

```java
    @Override
    public TimerFuture schedule(TimerTask task, long delay, TimeUnit unit) {

        long targetTime = System.currentTimeMillis() + unit.toMillis(delay);
        HashedWheelTimerFuture timerFuture = new HashedWheelTimerFuture(task, targetTime);

        // 直接运行到期、过期任务
        if (delay <= 0) {
            runTask(timerFuture);
            return timerFuture;
        }

        // 写入阻塞队列，保证并发安全（性能进一步优化可以考虑 Netty 的 Multi-Producer-Single-Consumer队列）
        waitingTasks.add(timerFuture);
        return timerFuture;
    }
```

