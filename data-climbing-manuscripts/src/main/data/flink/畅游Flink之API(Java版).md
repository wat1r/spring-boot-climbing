## 畅游Flink之API(Java版)











### Transform

#### 基本转换算子

> **map/flatMap/filter**

- map

把数组流中的每一个值，使用所提供的函数执行一遍，一一对应。得到元素个数相同的数组流

- flatmap

flat是扁平的意思。它把数组流中的每一个值，使用所提供的函数执行一遍，一一对应。得到元素相同的数组流。只不过，里面的元素也是一个子数组流。把这些子数组合并成一个数组以后，元素个数大概率会和原数组流的个数不同。 ![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220504220827.png)





```java
package com.frankcooper.apitest.transform;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class TransformTest1 {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 使得任务抢占同一个线程
        env.setParallelism(1);
        // 从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("/Users/frankcooper/IdeaProjects/spring-boot-climbing/bigdata-flink-grab/src/main/resources/sensor.txt");
        // 1. map, String => 字符串长度INT
        DataStream<Integer> mapStream = dataStream.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String value) throws Exception {
                return value.length();
            }
        });
        // 2. flatMap，按逗号分割字符串
        DataStream<String> flatMapStream = dataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] fields = value.split(",");
                for (String field : fields) {
                    out.collect(field);
                }
            }
        });

        // 3. filter,筛选"sensor_1"开头的数据
        DataStream<String> filterStream = dataStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return value.startsWith("sensor_1");
            }
        });

        // 打印输出
        mapStream.print("map");
        flatMapStream.print("flatMap");
        filterStream.print("filter");
        env.execute();
    }
}
```



输入`sensor.txt`

```java
sensor_1,1547718199,35.8
sensor_6,1547718201,15.4
sensor_7,1547718202,6.7
sensor_10,1547718205,38.1
sensor_1,1547718207,36.3
sensor_1,1547718209,32.8
sensor_1,1547718212,37.1
```

打印结果：

```java
map> 24
flatMap> sensor_1
flatMap> 1547718199
flatMap> 35.8
filter> sensor_1,1547718199,35.8
map> 24
flatMap> sensor_6
flatMap> 1547718201
flatMap> 15.4
map> 23
flatMap> sensor_7
flatMap> 1547718202
flatMap> 6.7
map> 25
flatMap> sensor_10
flatMap> 1547718205
flatMap> 38.1
filter> sensor_10,1547718205,38.1
map> 24
flatMap> sensor_1
flatMap> 1547718207
flatMap> 36.3
filter> sensor_1,1547718207,36.3
map> 24
flatMap> sensor_1
flatMap> 1547718209
flatMap> 32.8
filter> sensor_1,1547718209,32.8
map> 24
flatMap> sensor_1
flatMap> 1547718212
flatMap> 37.1
filter> sensor_1,1547718212,37.1
```







#### 多流转换算子

> **split/connect/union**

DataStream -> SplitStream 

- 根据某些特征把DataStream拆分成SplitStream， SplitStream虽然看起来像是两个Stream，但是其实它是一个特殊的Stream

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505090153.png)



```java

import com.frankcooper.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.Collections;


public class TransformTest4_MultipleStreams {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 从文件读取数据
    DataStream<String> inputStream = env.readTextFile("/Users/frankcooper/IdeaProjects/spring-boot-climbing/bigdata-flink-grab/src/main/resources/sensor.txt");

    // 转换成SensorReading
    DataStream<SensorReading> dataStream = inputStream.map(line -> {
      String[] fields = line.split(",");
      return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
    } );

    // 1. 分流，按照温度值30度为界分为两条流
    SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
      @Override
      public Iterable<String> select(SensorReading value) {
        return (value.getTemperature() > 30) ? Collections.singletonList("high") : Collections.singletonList("low");
      }
    });

    DataStream<SensorReading> highTempStream = splitStream.select("high");
    DataStream<SensorReading> lowTempStream = splitStream.select("low");
    DataStream<SensorReading> allTempStream = splitStream.select("high", "low");

    highTempStream.print("high");
    lowTempStream.print("low");
    allTempStream.print("all");
    
    env.execute();
  }
}
```

输出

```java
high> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
all > SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
low > SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
all > SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}
...
```

DataStream,DataStream -> ConnectedStreams

- 连接两个保持他们类型的数据流，两个数据流被Connect之后，只是被放在了一个流中，内部依然保持各自的数据和形式不发生任何变化，两个流相互独立。

DataStream -> DataStream

-  对两个或者两个以上的DataStream进行Union操作，产生一个包含多有DataStream元素的新DataStream。

对比

- 1.Connect 的数据类型可以不同，Connect 只能合并两个流；
- 2.Union可以合并多条流，Union的数据结构必须是一样的；

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505090816.png)

```java
import com.frankcooper.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.Collections;

/**
 * @ClassName: TransformTest4_MultipleStreams
 * @Description:
 * @Author: wushengran on 2020/11/7 16:14
 * @Version: 1.0
 */
public class TransformTest4_MultipleStreams {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
        DataStream<String> inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt");

        // 转换成SensorReading
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        } );

        // 1. 分流，按照温度值30度为界分为两条流
        SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
            @Override
            public Iterable<String> select(SensorReading value) {
                return (value.getTemperature() > 30) ? Collections.singletonList("high") : Collections.singletonList("low");
            }
        });

        DataStream<SensorReading> highTempStream = splitStream.select("high");
        DataStream<SensorReading> lowTempStream = splitStream.select("low");
        DataStream<SensorReading> allTempStream = splitStream.select("high", "low");

        // highTempStream.print("high");
        // lowTempStream.print("low");
        // allTempStream.print("all");

        // 2. 合流 connect，将高温流转换成二元组类型，与低温流连接合并之后，输出状态信息
        DataStream<Tuple2<String, Double>> warningStream = highTempStream.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(SensorReading value) throws Exception {
                return new Tuple2<>(value.getId(), value.getTemperature());
            }
        });

        ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStreams = warningStream.connect(lowTempStream);

        DataStream<Object> resultStream = connectedStreams.map(new CoMapFunction<Tuple2<String, Double>, SensorReading, Object>() {
            @Override
            public Object map1(Tuple2<String, Double> value) throws Exception {
                return new Tuple3<>(value.f0, value.f1, "high temp warning");
            }

            @Override
            public Object map2(SensorReading value) throws Exception {
                return new Tuple2<>(value.getId(), "normal");
            }
        });

        resultStream.print();
        
        env.execute();
    }
}
```

输出

```java
(sensor_1,35.8,high temp warning)
(sensor_6,normal)
(sensor_10,38.1,high temp warning)
(sensor_7,normal)
(sensor_1,36.3,high temp warning)
(sensor_1,32.8,high temp warning)
(sensor_1,37.1,high temp warning)
```





```java
// 3. union联合多条流
//        warningStream.union(lowTempStream); 这个不行，因为warningStream类型是DataStream<Tuple2<String, Double>>，而highTempStream是DataStream<SensorReading>
        highTempStream.union(lowTempStream, allTempStream);
```





#### 算子转换

在Flink中，**Transformation算子就是将一个或多个DataStream转换为新的DataStream**，可以将多个转换组合成复杂的数据流拓扑。 如下图所示，DataStream会由不同的Transformation操作，转换、过滤、聚合成其他不同的流，从而完成我们的业务要求。

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505205039.png)







### Window

- streaming流式计算是一种被设计用于处理无限数据集的数据处理引擎，而无限数据集是指一种不断增长的本质上无限的数据集，而**window是一种切割无限数据为有限块进行处理的手段**。
- **Window是无限数据流处理的核心，Window将一个无限的stream拆分成有限大小的”buckets”桶，我们可以在这些桶上做计算操作**。 

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505205928.png)





#### Window的类型

- 时间窗口（Time Window）：按照时间生成Window
  - 滚动时间窗口
  - 滑动时间窗口
  - 会话窗口
- 计数窗口（Count Window）：按照指定的数据条数生成一个Window，与时间无关
  - 滚动计数窗口
  - 滑动计数窗口





#### 滚动窗口(Tumbling Windows)

- 依据**固定的窗口长度**对数据进行切分
- 时间对齐，窗口长度固定，没有重叠

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505210831.png)



#### 滑动窗口(Sliding Windows)

![](https://wat1r-1311637112.cos.ap-shanghai.myqcloud.com/imgs/20220505211806.png)

- 可以按照固定的长度向后滑动固定的距离
- 滑动窗口由**固定的窗口长度**和**滑动间隔**组成
- 可以有重叠(是否重叠和滑动距离有关系)
- 滑动窗口是固定窗口的更广义的一种形式，滚动窗口可以看做是滑动窗口的一种特殊情况（即窗口大小和滑动间隔相等）



#### 会话窗口(Session Windows)

![image-20220505211832471](/Users/frankcooper/Library/Application Support/typora-user-images/image-20220505211832471.png)

- 由一系列事件组合一个指定时间长度的timeout间隙组成，也就是一段时间没有接收到新数据就会生成新的窗口
- 特点：时间无对齐



#### 概述

- 窗口分配器——`window()`方法

- 我们可以用`.window()`来定义一个窗口，然后基于这个window去做一些聚合或者其他处理操作。

  **注意`window()`方法必须在keyBy之后才能使用**。

- Flink提供了更加简单的`.timeWindow()`和`.countWindow()`方法，用于定义时间窗口和计数窗口。

```java
DataStream<Tuple2<String,Double>> minTempPerWindowStream = 
  datastream
  .map(new MyMapper())
  .keyBy(data -> data.f0)
  .timeWindow(Time.seconds(15))
  .minBy(1);
```

#### 窗口分配器(window assigner)

- `window()`方法接收的输入参数是一个WindowAssigner
- WindowAssigner负责将每条输入的数据分发到正确的window中
- Flink提供了通用的WindowAssigner
  - 滚动窗口（tumbling window）
  - 滑动窗口（sliding window）
  - 会话窗口（session window）
  - 全局窗口（global window

#### 创建不同类型的窗口

- 滚动时间窗口（tumbling time window）`.timeWindow(Time.seconds(15))`
- 滑动时间窗口（sliding time window）`.timeWindow(Time.seconds(15),Time.seconds(5))`
- 会话窗口（session window）`.window(EventTimeSessionWindows.withGap(Time.minutes(10)))`
- 滚动计数窗口（tumbling count window）`.countWindow(5)`
- 滑动计数窗口（sliding count window）`.countWindow(10,2)`
