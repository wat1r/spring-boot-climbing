## 优雅读取Properties和yml文件的方式



### yml文件

#### 方式1

- `application.yml`

```yml

service:
  elasticsearch:
    url: http://127.0.0.1:9200/_sql
    username: elastic
    password: elastic
  apollo:
    url: http://127.0.0.1:17500
  # 获取kafka的schema信息
  schema:
    register:
      url: http://127.0.0.1:8081
  bridge:
    tasks:
      hive: http://127.0.0.1/hdfsbridge/ua
      ealsticsearch: http://127.0.0.1:8092/esbridge_tasks/ua
      hbase: http://127.0.0.1:8092/hbasebridge/ua
      kudu: http://127.0.0.1:8092/kudubridge/ua
      cassandra: http://127.0.0.1:8092/cassandrabridge/ua
      phoenix: http://127.0.0.1:8092/phoenixbridge/ua
      hive_orc: http://127.0.0.1:8092/hdfsbridge_orc/ua
  kafka:
    bootstrap-servers: 127.0.0.1:9092,127.0.0.2:9092,127.0.0.3:9092
    consumer:
      auto-offset-reset: latest                           #最早未被消费的offset earliest
      max-poll-records: 3100                              #批量消费一次最大拉取的数据量
      enable-auto-commit: false                           #是否开启自动提交
      auto-commit-interval: 1000                          #自动提交的间隔时间
      session-timeout: 20000                              #连接超时时间
      max-poll-interval: 15000                            #手动提交设置与poll的心跳数,如果消息队列中没有消息，等待毫秒后，调用poll()方法。如果队列中有消息，立即消费消息，每次消费的消息的多少可以通过max.poll.records配置。
      max-partition-fetch-bytes: 15728640                 #设置拉取数据的大小,15M
      concurrency: 10
      key-deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer #反序列化
      value-deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer
    listener:
      batch-listener: true                                #是否开启批量消费，true表示批量消费
      concurrencys: 3,6                                   #设置消费的线程数
      poll-timeout: 1500                                  #只限自动提交，
```

- `ServiceConfigs`

```java
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "service")
@Data
public class ServiceConfigs {

    @Autowired
    private ElasticSerach elasticSerachConfig;
    @Autowired
    private Apollo apolloUrl;
    @Autowired
    private SchemaRegister schemaRegisterUrl;
    @Autowired
    private BridgeTasks bridgeTasks;
    @Autowired
    private KafkaConfig kafkaConfig;

    @Data
    @Configuration
    @ConfigurationProperties("service.elasticsearch")
    public static class ElasticSerach {
        private String url;
        private String username;
        private String password;

    }

    @Data
    @Configuration
    @ConfigurationProperties("service.apollo")
    public static class Apollo {
        private String url;
    }

    @Data
    @Configuration
    @ConfigurationProperties("service.schema.register")
    public static class SchemaRegister {
        private String url;
    }

    @Data
    @Configuration
    @ConfigurationProperties("service.bridge.tasks")
    public static class BridgeTasks {
        private String hive;
        private String ealsticsearch;
        private String hbase;
        private String kudu;
        private String cassandra;
        private String phoenix;
        private String hiveOrc;
    }

    @Data
    @Configuration
    @ConfigurationProperties("service.kafka")
    public static class KafkaConfig {

        private String bootstrapServers;
        @Autowired
        private ListenerProperties listener;
        @Autowired
        private ConsumerProperties consumer;

        @Data
        @Configuration
        @ConfigurationProperties("service.kafka.listener")
        public static class ListenerProperties {
            private String concurrencys;
            private String batchListener;
            private String pollTimeout;

        }

        @Data
        @Configuration
        @ConfigurationProperties("service.kafka.consumer")
        public static class ConsumerProperties {
            private String autoOffsetReset;
            private String maxPollRecords;
            private String enableAutoCommit;
            private String sessionTimeout;
            private String maxPollInterval;
            private String autoCommitInterval;
            private String maxPartitionFetchBytes;
            private String concurrency;
            private String keyDeserializer;
            private String valueDeserializer;
        }
    }


}
```

- 在其他处使用时，`@AutoWired`引入即可

#### 方式2

- 引入pom

```java
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>1.29</version>
        </dependency>
```

- `test.yaml`

```yaml
yaml:
  name: test
  age: 26
  # 可以用 数组 或 List 接收
  pc: lenovo,dell
  # 可以用 数组 或 List 接收
  hobby:
    - lol
    - coding
```

- 配置文件读取工厂类`YamlPropertySourceFactory`

```java

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * Yaml 配置文件读取工厂类
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    public YamlPropertySourceFactory() {
    }

    /**
     * yaml 文档解析方法
     *
     * @param name     配置项名称
     * @param resource 配置项资源
     * @return PropertySource<?>
     * @throws IOException IOException
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {

        // 返回 yaml 属性资源
        return new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource())
                .get(0);
    }

}
```

- `TestYaml`

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * yaml 配置映射类
 */
@Data
@Component
@PropertySource(
        name = "test.yml",
        value = {"classpath:tmp/test.yml"},
        ignoreResourceNotFound = false,
        encoding = "UTF-8",
        factory = YamlPropertySourceFactory.class
)
@ConfigurationProperties(prefix = "yaml")
public class TestYaml {

    private String name;

    private int age;

    private String[] pc;
    //private List<String> pc;

    private List<String> hobby;
    //private String[] hobby;

}
```

- 使用方式：

```java
   @Autowired
    private TestYaml testYaml;
```



### 读取properties文件

- 前置的pom文件

```xml
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
            <type>jar</type>
            <scope>compile</scope>
        </dependency>
                  <dependency>
            <groupId>commons-beanutils</groupId>
            <artifactId>commons-beanutils-core</artifactId>
            <version>1.8.3</version>
        </dependency>
        <dependency>
            <groupId>commons-beanutils</groupId>
            <artifactId>commons-beanutils-bean-collections</artifactId>
            <version>1.8.3</version>
        </dependency>
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.5</version>
        </dependency>
```

- 配置文件管理类`ConfigurationManager`

```java

import java.net.URL;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class ConfigurationManager {

    private static Log logger = LogFactory.getLog(Configuration.class);
    private static Configuration config = null;
    private static URL CONFIGURATION_FILE_URL = ConfigurationManager.class.getResource("application.properties");

    private static final Object lock = new Object();

    private ConfigurationManager() {
    }

    public static Configuration getConfig() {
        if (config == null) {
            synchronized (lock) {
                ConfigurationFactory factory = new ConfigurationFactory();
                factory.setConfigurationURL(CONFIGURATION_FILE_URL);
                try {
                    config = factory.getConfiguration();
                } catch (ConfigurationException e) {
                    logger.error(CommonUtil.getExceptionTrace(e));
                }
            }
        }
        return config;
    }
}

```

- Interface:`Properties`

```java
public interface Properties {

    String RT_RMI_SERVER_URL = ConfigurationManager.getConfig().getString("rt.rmi.server.url");

    String EXIT_URL = ConfigurationManager.getConfig().getString("uam.exitUrl");
    String APP_CODE = ConfigurationManager.getConfig().getString("uam.appCode");
    String ENTRANCE_CODE = ConfigurationManager.getConfig().getString("uam.entranceCode");
    String LOGIN_URL = (ConfigurationManager.getConfig().getString("uam.loginUrl") + "/Login.aspx?SubSystemCode={0}&EntranceCode={1}").replace("{0}", APP_CODE.toString())
            .replace("{1}", ENTRANCE_CODE.toString()).replace("{2}", "");
    ;
    String AUTHOR_URL = ConfigurationManager.getConfig().getString("uam.loginUrl") + "/Service/Privilege.asmx?wsdl";

    String PREVIEW_SERVER = ConfigurationManager.getConfig().getString("preview.server");

    String RTC_DATASHEETS = ConfigurationManager.getConfig().getString("rtc.datasheets");

    String FILE_UPLOAD_PATH = ConfigurationManager.getConfig().getString("file.upload.path");

    String SCHEMA_REGISTRY_URL = ConfigurationManager.getConfig().getString("schema.registry.url");

    String ZOOKEEPER_URL = ConfigurationManager.getConfig().getString("zookeeper.url");

    int KAFKA_PARTITION_NUM = ConfigurationManager.getConfig().getInt("kafka.partition.num");

    int KAFKA_PARTITION_REPLICA = ConfigurationManager.getConfig().getInt("kafka.partition.replica");

    String BUILD_VERSION = ConfigurationManager.getConfig().getString("build.version");

    int UPLOAD_MAX_SIZE = 104857600;
    int SIZE_THRESHOLD = 4096;

    String ES_BRIDGE = ConfigurationManager.getConfig().getString("esbridge.tasks");
    String CASSANDRA_BRIDGE = ConfigurationManager.getConfig().getString("cassandrabridge.tasks");
    String HIVE_BRIDGE = ConfigurationManager.getConfig().getString("hivebridge.tasks");
    String HIVE_ORC = ConfigurationManager.getConfig().getString("orcbridge.tasks");
    String HBASE_BRIDGE = ConfigurationManager.getConfig().getString("hbasebridge.tasks");
    String PHOENIX_BRIDGE = ConfigurationManager.getConfig().getString("phoenixbridge.tasks");
    String KUDU_BRIDGE = ConfigurationManager.getConfig().getString("kudubridge.tasks");


    String STREAM_AGENT_LIST = ConfigurationManager.getConfig().getString("stream.agent.list");

    String STREAM_AGENT_PORT_MIN = ConfigurationManager.getConfig().getString("stream.agent.port.min");
    String STREAM_AGENT_PORT_MAX = ConfigurationManager.getConfig().getString("stream.agent.port.max");
    String STREAM_AGENT_SPRING_BOOT_SERVER = ConfigurationManager.getConfig().getString("stream.agent.spring.boot.server");


    String HBASE_QUORUM = ConfigurationManager.getConfig().getString("hbase.quorum");
    String HBASE_PORT = ConfigurationManager.getConfig().getString("hbase.port");


    String DATA_RECEIVE_SERVER = ConfigurationManager.getConfig().getString("data.receive.server");

    String PUBLISH_SCHEMA_SERVER = ConfigurationManager.getConfig().getString("publish.schema.server");

    String META_MANAGER_URL = ConfigurationManager.getConfig().getString("meta.manager.url");
    String META_MANAGER_ACL_CACHE_URL = ConfigurationManager.getConfig().getString("meta.manager.acl.cache.url");
    String META_MANAGER_ACL_URL = ConfigurationManager.getConfig().getString("meta.manager.acl.url");
    String META_MANAGER_APP_URL = ConfigurationManager.getConfig().getString("meta.manager.app.url");

    String METASYS_URL = ConfigurationManager.getConfig().getString("metasys.url");

    String APOLLO_TENANTID = ConfigurationManager.getConfig().getString("apollo.tenantId");
    String APOLLO_ES_ID = ConfigurationManager.getConfig().getString("apollo.esId");
    String APOLLO_PHOENIX_ID = ConfigurationManager.getConfig().getString("apollo.phoenixId");
    String APOLLO_HIVE_ID = ConfigurationManager.getConfig().getString("apollo.hiveId");
    String APOLLO_KUDU_ID = ConfigurationManager.getConfig().getString("apollo.kuduId");

    String Dev = ConfigurationManager.getConfig().getString("env.dev");

}
```

- `application.properties`

```properties
################### Dev  DataSource Configuration TEST##########################xxxx
jdbc.url = jdbc:mysql://localhost:3306/sponge?zeroDateTimeBehavior=convertToNull
jdbc.username = test
jdbc.password = test
jdbc.driverClassName = com.mysql.jdbc.Driver

###################realtime server###################################################
rt.rmi.server.url=http://127.0.0.1:8089/test/

######################preview data #######################################
preview.server=http://127.0.0.1:1992/


######################hive operate #######################################
rtc.datasheets=http://127.0.0.1:9999/
file.upload.path=D:\\tmp_dir

#######################schema########################
schema.registry.url=127.0.0.1:8081
zookeeper.url=127.0.0.1:2181\,127.0.0.2:2181\,127.0.0.3:2181
kafka.partition.num=3
kafka.partition.replica=1

######################version#####################
build.version=${current.time}

######################configServer##############################
esbridge.tasks=http://127.0.0.1:8092/esbridge_tasks/ua
cassandrabridge.tasks=http://127.0.0.1:8092/cassandrabridge/ua
hivebridge.tasks=http://127.0.0.1:8092/hdfsbridge/ua
orcbridge.tasks=http://127.0.0.1:8092/hdfsbridge_orc/ua
hbasebridge.tasks=http://127.0.0.1:8092/hbasebridge/ua
phoenixbridge.tasks=http://127.0.0.1:8092/phoenixbridge/ua
kudubridge.tasks=http://127.0.0.1:8092/kudubridge/ua

######################stream agent####################################
stream.agent.list=127.0.0.137
stream.agent.port.min=10000
stream.agent.port.max=19999
stream.agent.spring.boot.server=http://127.0.0.1:8090

#####################hbase-server###########################################
hbase.quorum=127.0.0.1\,127.0.0.2\,127.0.0.3
hbase.port=2181

#####################data receive auth ###################################
data.receive.server=http://127.0.0.1:9008/

#####################publish-schema########################################
publish.schema.server = http://127.0.0.1:8092/publish-schema




```

- 调用的方式

```java
Properties.AAP_CODE;
```





 









