













## kafka安装

https://kafka.apache.org/downloads 下载[kafka_2.11-2.2.1.tgz](https://archive.apache.org/dist/kafka/2.2.1/kafka_2.11-2.2.1.tgz) ([asc](https://archive.apache.org/dist/kafka/2.2.1/kafka_2.11-2.2.1.tgz.asc), [sha512](https://archive.apache.org/dist/kafka/2.2.1/kafka_2.11-2.2.1.tgz.sha512))

**第一个命令窗口**->启动zookeeper服务： 

```bash
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

**第二个命令窗口**->启动kfaka服务：

```shell
bin\windows\kafka-server-start.bat config\server.properties
```

**第三个命令窗口**->启动produce: 

- 创建一个主题：

```shell
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

- 使用如下命令查看创建的主题列表：

```shell
bin\windows\kafka-topics.bat --list --zookeeper localhost:2181
```

- 启动生产者：

```shell
bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic testtopic
```

**第四个命令窗口**->启动consumer：

```shell
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:2181 --topic testtopic --from-beginning 
```

## flink搭建

https://www.apache.org/dyn/closer.lua/flink/flink-1.19.1/flink-1.19.1-bin-scala_2.12.tgz

下载flink-1.19.1-bin-scala_2.12.tgz

```shell
cd D:\Dev\flink-1.9.1>
bin\start-cluster.bat
```

## flink程序

执行kafka安装步骤里的第一个窗口，第二个窗口，启动kafka,创建一个person的topic

```shell
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic person
```

### 创建person表

```mysql
CREATE TABLE `person` (
  `id` mediumint NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `createDate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```

　　接下来我们该创建一个JAVA工程，采用的maven的方式。前提是大家一定要先安装好maven，可以执行mvn命令。直接执行一下maven的时候可能会卡住，下载不了，我先从

 [https://repo.maven.apache.org/maven2/](http://repo.maven.apache.org/maven2/)上下载一个 archetype-catalog.xml 文件，然后放到本地的maven对应的库，你们可以参考这个我的路径进行调整。  　D:\Dev\repository\org\apache\maven\archetype\archetype-catalog\3.3.0

### 创建maven项目

```shell
mvn archetype:generate \ 
    -DarchetypeGroupId=org.apache.flink \
    -DarchetypeArtifactId=flink-quickstart-java \
    -DarchetypeVersion=1.7.2 \
    -DgroupId=flink-project \
    -DartifactId=flink-project \
    -Dversion=0.1 \
    -Dpackage=myflink \
    -DinteractiveMode=false \  #这个是创建项目时采用交互方式，上边指定了了相关的版本号和包名等信息，所以不需要交互方式进行。
    -DarchetypeCatalog=local  #这个是使用上边下载的文件，local也就是从本地文件获取，因为远程获取特别慢。导致工程生成不了。
```

一行执行：

```shell
mvn archetype:generate     -DarchetypeGroupId=org.apache.flink     -DarchetypeArtifactId=flink-quickstart-java     -DarchetypeVersion=1.19.1     -DgroupId=flink-project     -DartifactId=flink-project     -Dversion=0.1     -Dpackage=myflink     -DinteractiveMode=false     -DarchetypeCatalog=local  
```

### 目录结构

```shell
 MINGW64 /d/Dev/wat1r/flink-project
$ tree
.
|-- README.md
|-- dependency-reduced-pom.xml
|-- flink-project.iml
|-- pom.xml
|-- src
|   `-- main
|       |-- java
|       |   `-- myflink
|       |       |-- DataSourceFromKafka.java
|       |       |-- db
|       |       |   `-- DbUtils.java
|       |       |-- kafka
|       |       |   |-- KafkaConsume.java
|       |       |   `-- KafkaWriter.java
|       |       |-- pojo
|       |       |   `-- Person.java
|       |       `-- sink
|       |           `-- MySqlSink.java
|       `-- resources
|           `-- log4j.properties
`-- target
    |-- classes
    |   |-- META-INF
    |   |   `-- flink-project.kotlin_module
    |   |-- log4j.properties
    |   `-- myflink
    |       |-- DataSourceFromKafka$1.class
    |       |-- DataSourceFromKafka.class
    |       |-- db
    |       |   `-- DbUtils.class
    |       |-- kafka
    |       |   |-- KafkaConsume.class
    |       |   `-- KafkaWriter.class
    |       |-- pojo
    |       |   `-- Person.class
    |       `-- sink
    |           `-- MySqlSink.class
    |-- flink-project-0.1.jar
    |-- generated-sources
    |   `-- annotations
    |-- maven-archiver
    |   `-- pom.properties
    |-- maven-status
    |   `-- maven-compiler-plugin
    |       `-- compile
    |           `-- default-compile
    |               |-- createdFiles.lst
    |               `-- inputFiles.lst
    `-- original-flink-project-0.1.jar

24 directories, 25 files

```





### pom.xml

注意provided标签

```xml
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>flink-project</groupId>
	<artifactId>flink-project</artifactId>
	<version>0.1</version>
	<packaging>jar</packaging>

	<name>Flink Quickstart Job</name>
	<url>http://www.myorganization.org</url>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<!--		<flink.version>1.7.2</flink.version>-->
		<flink.version>1.9.1</flink.version>
		<java.version>1.8</java.version>
		<scala.binary.version>2.11</scala.binary.version>
		<maven.compiler.source>${java.version}</maven.compiler.source>
		<maven.compiler.target>${java.version}</maven.compiler.target>
	</properties>

	<repositories>
		<repository>
			<id>apache.snapshots</id>
			<name>Apache Development Snapshot Repository</name>
			<url>https://repository.apache.org/content/repositories/snapshots/</url>
			<releases>
				<enabled>false</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>

	<dependencies>
		<!-- Apache Flink dependencies -->
		<!-- These dependencies are provided, because they should not be packaged into the JAR file. -->
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-java</artifactId>
			<version>${flink.version}</version>
<!--			<scope>provided</scope>-->
<!--			<exclusions>-->
<!--				<exclusion>-->
<!--					<groupId>org.apache.commons</groupId>-->
<!--					<artifactId>commons-lang3</artifactId>-->
<!--				</exclusion>-->
<!--			</exclusions>-->
		</dependency>
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
			<version>${flink.version}</version>
<!--			<scope>provided</scope>-->
		</dependency>

		<!-- Add connector dependencies here. They must be in the default scope (compile). -->

		<!-- Example:

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka-0.10_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>
        -->

		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-connector-kafka-0.10_${scala.binary.version}</artifactId>
			<version>${flink.version}</version>
		</dependency>

		<!-- Add logging framework, to produce console output when running in the IDE. -->
		<!-- These dependencies are excluded from the application JAR by default. -->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>1.7.7</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.17</version>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.62</version>
		</dependency>

		<dependency>
			<groupId>com.google.guava</groupId>
			<artifactId>guava</artifactId>
			<version>28.1-jre</version>
		</dependency>

		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
			<version>3.1.0</version>
		</dependency>


		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>8.0.16</version>
		</dependency>

		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.20</version>
		</dependency>

<!--		<dependency>-->
<!--			<groupId>org.apache.commons</groupId>-->
<!--			<artifactId>commons-lang3</artifactId>-->
<!--			<version>3.2.1</version>-->
<!--		</dependency>-->

	</dependencies>

	<build>
		<plugins>

			<!-- Java Compiler -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>${java.version}</source>
					<target>${java.version}</target>
				</configuration>
			</plugin>

			<!-- We use the maven-shade plugin to create a fat jar that contains all necessary dependencies. -->
			<!-- Change the value of <mainClass>...</mainClass> if your program entry point changes. -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.0.0</version>
				<executions>
					<!-- Run shade goal on package phase -->
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<artifactSet>
								<excludes>
									<exclude>org.apache.flink:force-shading</exclude>
									<exclude>com.google.code.findbugs:jsr305</exclude>
									<exclude>org.slf4j:*</exclude>
									<exclude>log4j:*</exclude>
								</excludes>
							</artifactSet>
							<filters>
								<filter>
									<!-- Do not copy the signatures in the META-INF folder.
									Otherwise, this might cause SecurityExceptions when using the JAR. -->
									<artifact>*:*</artifact>
									<excludes>
										<exclude>META-INF/*.SF</exclude>
										<exclude>META-INF/*.DSA</exclude>
										<exclude>META-INF/*.RSA</exclude>
									</excludes>
								</filter>
							</filters>
							<transformers>
								<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>myflink.StreamingJob</mainClass>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>

		<pluginManagement>
			<plugins>

				<!-- This improves the out-of-the-box experience in Eclipse by resolving some warnings. -->
				<plugin>
					<groupId>org.eclipse.m2e</groupId>
					<artifactId>lifecycle-mapping</artifactId>
					<version>1.0.0</version>
					<configuration>
						<lifecycleMappingMetadata>
							<pluginExecutions>
								<pluginExecution>
									<pluginExecutionFilter>
										<groupId>org.apache.maven.plugins</groupId>
										<artifactId>maven-shade-plugin</artifactId>
										<versionRange>[3.0.0,)</versionRange>
										<goals>
											<goal>shade</goal>
										</goals>
									</pluginExecutionFilter>
									<action>
										<ignore/>
									</action>
								</pluginExecution>
								<pluginExecution>
									<pluginExecutionFilter>
										<groupId>org.apache.maven.plugins</groupId>
										<artifactId>maven-compiler-plugin</artifactId>
										<versionRange>[3.1,)</versionRange>
										<goals>
											<goal>testCompile</goal>
											<goal>compile</goal>
										</goals>
									</pluginExecutionFilter>
									<action>
										<ignore/>
									</action>
								</pluginExecution>
							</pluginExecutions>
						</lifecycleMappingMetadata>
					</configuration>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>

	<!-- This profile helps to make things run out of the box in IntelliJ -->
	<!-- Its adds Flink's core classes to the runtime class path. -->
	<!-- Otherwise they are missing in IntelliJ, because the dependency is 'provided' -->
	<profiles>
		<profile>
			<id>add-dependencies-for-IDEA</id>

			<activation>
				<property>
					<name>idea.version</name>
				</property>
			</activation>

			<dependencies>
				<dependency>
					<groupId>org.apache.flink</groupId>
					<artifactId>flink-java</artifactId>
					<version>${flink.version}</version>
					<scope>compile</scope>
				</dependency>
				<dependency>
					<groupId>org.apache.flink</groupId>
					<artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
					<version>${flink.version}</version>
					<scope>compile</scope>
				</dependency>
			</dependencies>
		</profile>
	</profiles>

</project>

```



Person.java

```java
package myflink.pojo;

import java.util.Date;

/**
 * @Author Frank Cooper(wang zhou)
 * @Date: 2024/10/10/ 15:29
 * @description
 */
public class Person {

    private String name;
    private int age;
    private Date createDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
```



DbUtils.java

```java
package myflink.db;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;

public class DbUtils {

    private static DruidDataSource dataSource;

    public static Connection getConnection() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/flink_dev");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        //设置初始化连接数，最大连接数，最小闲置数
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(50);
        dataSource.setMinIdle(5);
        //返回连接
        return dataSource.getConnection();
    }

}
```

KafkaWriter.java

```java
package myflink.kafka;

import com.alibaba.fastjson.JSON;
import myflink.pojo.Person;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaWriter {

    //本地的kafka机器列表
    public static final String BROKER_LIST = "localhost:9092";
    //kafka的topic
    public static final String TOPIC_PERSON = "person";
    //key序列化的方式，采用字符串的形式
    public static final String KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    //value的序列化的方式
    public static final String VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    public static void writeToKafka() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", BROKER_LIST);
        props.put("key.serializer", KEY_SERIALIZER);
        props.put("value.serializer", VALUE_SERIALIZER);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        //构建Person对象，在name为hqs后边加个随机数
        int randomInt = RandomUtils.nextInt(1, 100000);
        Person person = new Person();
        person.setName("hqs" + randomInt);
        person.setAge(randomInt);
        person.setCreateDate(new Date());
        //转换成JSON
        String personJson = JSON.toJSONString(person);

        //包装成kafka发送的记录
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_PERSON, null,
                null, personJson);
        //发送到缓存
        producer.send(record);
        System.out.println("向kafka发送数据:" + personJson);
        //立即发送
        producer.flush();

    }

    public static void main(String[] args) {
        while (true) {
            try {
                //每三秒写一条数据
                TimeUnit.SECONDS.sleep(1);
                writeToKafka();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

```

KafkaConsume.java

```java
package myflink.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class KafkaConsume {

    //本地的kafka机器列表
    public static final String BROKER_LIST = "localhost:9092";
    //kafka的topic
    public static final String TOPIC_PERSON = "person";
    //key序列化的方式，采用字符串的形式

    public static void readKafka() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", BROKER_LIST);
        props.put("group.id", "xxx");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        //拉取所有topic中的数据
        consumer.subscribe(Arrays.asList(TOPIC_PERSON));
        while (true) {
            //每隔1秒拉取一行数据
            ConsumerRecords<String, String> records = consumer.poll(1000L);
            if (!records.isEmpty()) {
                for (ConsumerRecord<String, String> rec : records) {
                    System.out.println("=============" + rec.value());
                }
            }
        }

    }

    public static void main(String[] args) {
        try {
            readKafka();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```

MySqlSink.java

```java
package myflink.sink;

import myflink.db.DbUtils;
import myflink.pojo.Person;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author Frank Cooper(wang zhou)
 * @Date: 2024/10/10/ 15:30
 * @description
 */
public class MySqlSink extends RichSinkFunction<List<Person>> {
    private PreparedStatement ps;
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取数据库连接，准备写入数据库
        connection = DbUtils.getConnection();
        String sql = "insert into person(name, age, createDate) values (?, ?, ?); ";
        System.out.printf("sql:%s\n", sql);
        ps = connection.prepareStatement(sql);
    }

    @Override
    public void close() throws Exception {
        super.close();
        //关闭并释放资源
        if (connection != null) {
            connection.close();
        }

        if (ps != null) {
            ps.close();
        }
    }

    @Override
    public void invoke(List<Person> persons, Context context) throws Exception {
        for (Person person : persons) {
            ps.setString(1, person.getName());
            ps.setInt(2, person.getAge());
            ps.setTimestamp(3, new Timestamp(person.getCreateDate().getTime()));
            ps.addBatch();
        }

        //一次性写入
        int[] count = ps.executeBatch();
        System.out.println("成功写入Mysql数量：" + count.length);

    }
}
```

DataSourceFromKafka.java

```java
package myflink;

import com.alibaba.fastjson.JSONObject;
import myflink.kafka.KafkaWriter;
import myflink.pojo.Person;
import myflink.sink.MySqlSink;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Properties;


public class DataSourceFromKafka {

    public static void main(String[] args) throws Exception {
        //构建流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //kafka
        Properties prop = new Properties();
        prop.put("bootstrap.servers", KafkaWriter.BROKER_LIST);
        prop.put("zookeeper.connect", "localhost:2181");
        prop.put("group.id", KafkaWriter.TOPIC_PERSON);
        prop.put("key.serializer", KafkaWriter.KEY_SERIALIZER);
        prop.put("value.serializer", KafkaWriter.VALUE_SERIALIZER);
        prop.put("auto.offset.reset", "earliest");

        DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer010<String>(
                KafkaWriter.TOPIC_PERSON,
                new SimpleStringSchema(),
                prop
        )).
                //单线程打印，控制台不乱序，不影响结果
                        setParallelism(1);

        //从kafka里读取数据，转换成Person对象
        DataStream<Person> dataStream = dataStreamSource.map(value -> {
            System.out.printf("value:%s\n", value);
            return JSONObject.parseObject(value, Person.class);
        });
        //收集5秒钟的总数
        dataStream.timeWindowAll(Time.seconds(30L)).
                apply(new AllWindowFunction<Person, List<Person>, TimeWindow>() {

                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<Person> iterable, Collector<List<Person>> out) throws Exception {
                        List<Person> persons = Lists.newArrayList(iterable);
                        System.out.println("outer：" + persons.size());
                        if (persons.size() > 0) {
                            System.out.println("5秒的总共收到的条数：" + persons.size());
                            out.collect(persons);
                        }

                    }
                })
                //sink 到数据库
                .addSink(new MySqlSink());
        //打印到控制台
        //.print();


        env.execute("kafka 消费任务开始");
    }

}
```



启动KafkaWriter的main方法，写数据

启动DataSourceFromKafka的方法，读数据写mysql

```mysql
mysql> select * from person limit 10;
+----+----------+-------+---------------------+
| id | name     | age   | createDate          |
+----+----------+-------+---------------------+
|  1 | hqs31127 | 31127 | 2024-10-10 17:19:19 |
|  2 | hqs82244 | 82244 | 2024-10-10 17:19:31 |
|  3 | hqs72826 | 72826 | 2024-10-10 17:19:23 |
|  4 | hqs24087 | 24087 | 2024-10-10 17:19:35 |
|  5 | hqs39663 | 39663 | 2024-10-10 17:19:15 |
|  6 | hqs99405 | 99405 | 2024-10-10 17:19:28 |
|  7 | hqs95301 | 95301 | 2024-10-10 17:19:40 |
|  8 | hqs77945 | 77945 | 2024-10-10 17:19:25 |
|  9 | hqs8315  |  8315 | 2024-10-10 17:19:37 |
| 10 | hqs9056  |  9056 | 2024-10-10 17:19:24 |
+----+----------+-------+---------------------+
10 rows in set (0.04 sec)

```



## Reference

[Windows平台下kafka+flink开发环境搭建](https://blog.csdn.net/weixin_42412645/article/details/90952532)

[Windows平台下kafka+ flink环境的搭建以及简单使用](https://blog.csdn.net/zuokaopuqingnian/article/details/83001695)

[Windows本地运行flink+kafka](https://blog.csdn.net/Baron_ND/article/details/106403290)

[构建一个flink程序,从kafka读取然后写入MYSQL](https://www.cnblogs.com/huangqingshi/p/12003453.html)

