## SpringBoot集成jasypt教程



```properties
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.4</version>
        </dependency>
```

- 本地环境配置启动的`password`和`algorithm`

![image-20230109145002486](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20230109145002486.png)

`linux`服务器上配置环境变量：

- `vim ~/.bash_profile`

```json
export ENCRYPTOR_PASSWORD=dataworks
export ENCRYPTOR_ALGORITHM=PBEWithMD5AndDES
```



### JasyptConfig

```java
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;
    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    @Bean("encryptorBean")
    public StringEncryptor getSimpleStringPBEConfig() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(algorithm);
        config.setPoolSize("1");
        config.setKeyObtentionIterations("1000");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

}
```

### yaml

```yaml
jasypt:
  encryptor:
    password: ${ENCRYPTOR_PASSWORD}
    algorithm: ${ENCRYPTOR_ALGORITHM}
    bean: encryptorBean

spring:
  application:
    name: data-studio-server-test
  datasource:
    primary:
      jdbc-url: jdbc:mysql://127.0.0.1:3306/data_studio_test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=true
      username: process
      password: ENC(guFZJJzc93M3GpAx2ZVJpwI6RVXSkbhr2pNj9LYYTUJrDzdPO9XjLQ==)
      driver-class-name: com.mysql.cj.jdbc.Driver
    secondary:
      jdbc-url: jdbc:mysql://127.0.0.1:3306/smallbang-dev?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=true
      username: process
      password: ENC(guFZJJzc93M3GpAx2ZVJpwI6RVXSkbhr2pNj9LYYTUJrDzdPO9XjLQ==)
      driver-class-name: com.mysql.cj.jdbc.Driver
```



默认的bean名称，如果自定义，需要指定bean

```json
    {
      "name": "jasypt.encryptor.bean",
      "type": "java.lang.String",
      "description": "Specify the name of bean to override jasypt-spring-boot's default properties based {@link org.jasypt.encryption.StringEncryptor}. Default Value is {@code jasyptStringEncryptor}.",
      "sourceType": "com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties",
      "defaultValue": "jasyptStringEncryptor"
    }
```



### 启动命令：

```shell
nohup  java -jar  -Djasypt.encryptor.password=$ENCRYPTOR_PASSWORD -Djasypt.encryptor.algorithm=$ENCRYPTOR_ALGORITHM  dataworks-studio-1.0-SNAPSHOT.jar --spring.profiles.active=test &
```

## JasyptUtils

- 生成ENC内的加密内容

```java
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtils {
    /**
     * {@link StringEncryptor} 加解密。
     * 同一个密钥（salt）对同一个内容执行加密，生成的密文都是不一样的，但是根据根据这些密文解密成明文都是可以.
     * 1、Jasypt 默认使用 {@link StringEncryptor} 来解密全局配置文件中的属性，所以提供密文时，也需要提供 {@link StringEncryptor} 加密的密文
     * 2、{@link StringEncryptor} 接口有很多的实现类，比如常用的 {@link PooledPBEStringEncryptor}
     * 3、setConfig(final PBEConfig config)：为对象设置 {@link PBEConfig} 配置对象
     * 4、encrypt(final String password)：加密内容
     * 5、decrypt(final String encryptedMessage)：解密内容
     *
     * @param salt      ：盐 加/解密必须使用同一个密钥
     * @param password  ：加/解密的内容
     * @param isEncrypt ：true 表示加密、false 表示解密
     * @return
     */
    public static String stringEncryptor(String salt, String password, boolean isEncrypt) {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setConfig(getSimpleStringPBEConfig(salt));
        String result = isEncrypt ? pooledPBEStringEncryptor.encrypt(password) : pooledPBEStringEncryptor.decrypt(password);
        return result;
    }

    /**
     * 设置 {@link PBEConfig} 配置对象，SimpleStringPBEConfig 是它的实现类
     * 1、所有的配置项建议与全局配置文件中的配置项保持一致，特别是 password、algorithm 等等选项，如果不一致，则应用启动时解密失败而报错.
     * 2、setPassword(final String password)：设置加密密钥，必须与全局配置文件中配置的保存一致，否则应用启动时会解密失败而报错.
     * 3、setPoolSize(final String poolSize)：设置要创建的加密程序池的大小.
     * 4、setAlgorithm(final String algorithm): 设置加密算法的值， 此算法必须由 JCE 提供程序支持
     * 5、setKeyObtentionIterations: 设置应用于获取加密密钥的哈希迭代次数。
     * 6、setProviderName(final String providerName)：设置要请求加密算法的安全提供程序的名称
     * 7、setSaltGeneratorClassName：设置 Sal 发生器
     * 8、setIvGeneratorClassName：设置 IV 发生器
     * 9、setStringOutputType：设置字符串输出的编码形式。可用的编码类型有 base64、hexadecimal
     *
     * @param secretKey
     * @return
     */
    private static SimpleStringPBEConfig getSimpleStringPBEConfig(String secretKey) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(secretKey);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPoolSize("1");
        config.setKeyObtentionIterations("1000");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }


    public static void main(String[] args) throws Exception {
        String salt = "XXX";
        String password = "123456";
        //一个同样的密码和秘钥，每次执行加密，密文都是不一样的。但是解密是没问题的。
        String jasyptEncrypt = stringEncryptor(salt, password, true);
        System.out.println(jasyptEncrypt);
        String jasyptDecrypt = stringEncryptor(salt, "sr+jVGgmX/jhTDCOKx5jmDdJWZgFEoWm7IYto4jlxv2wXqHjR+Y8MQ==", false);
        System.out.println(jasyptDecrypt);
    }
}
```









### 一些报错信息

- salt传的不对，生成的原始秘钥不匹配：

![image-20230109142435065](C:\Users\wangzhou\AppData\Roaming\Typora\typora-user-images\image-20230109142435065.png)



























