## SpringBoot配置Swagger2接口未显示分析

### 0.背景

- 项目A继承自项目B,`SpringBoot`为`1.5.16.RELEASE`

```xml
    <parent>
        <artifactId>B</artifactId>
        <groupId>com.****</groupId>
        <version>****-SNAPSHOT</version>
    </parent>
```

### 1.配置信息

- 项目A中配置的`Swagger2`版本

```xml
 <!--		swagger2 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
            <exclusions>
                <exclusion>
                    <groupId>io.swagger</groupId>
                    <artifactId>swagger-models</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>com.google.guava</groupId>
                    <artifactId>guava</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-models</artifactId>
            <version>1.5.21</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
            <exclusions>
                <exclusion>
                    <groupId>com.google.guava</groupId>
                    <artifactId>guava</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

- 项目A这的`Swagger2Config.class`

```java
@EnableSwagger2
@Configuration
@EnableWebMvc
public class Swagger2Config extends WebMvcConfigurerAdapter {

    /**
     * 添加对swagger资源的放行
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("myDocket")
                .apiInfo(apiInfo())
                .pathMapping("/")
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.****.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    //构建 api文档的详细信息函数
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Spring Boot Swagger2 构建RESTful API")
                //条款地址
                .description("*** Web Service Api Document")
                .version("1.0.0")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                //描述
                .build();
    }
}
```



### 3.定位

配置如上信息的时候后启动端口进行调试：

![image-20200911091202198](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\swagger\SpringBoot配置Swagger2接口未显示分析.assets\image-20200911091202198.png)



正常的显示是没问题，但是所有的接口信息都不能展示，加了`Swagger`的的注解，未生效，报`No operations defined in spec!`

这种原因大部分是因为`package`没有被扫到，即上面的` .apis(RequestHandlerSelectors.basePackage("com.****.controller"))`,这段一开始，在`debug`过程中，也确实走了这段代码，但是未生效

之后试了下另外一个完全新搭建的项目（这个项目是从老项目中整体中剥离出来的，所以才有了继承父pom的写法），同样配置是完全OK的，正常显示的。

### 4.解决

上图中的1,2两个标志指向了一个一个邮箱地址，但在项目A中没找到此邮箱地址，去B项目搜了下，发现是这个`SwaggerConfig.class`影响到了

```java
@Configuration
@Profile("!prod")
@EnableSwagger2
public class SwaggerConfig extends ApolloAware{
    private static final String BASE_CONTROLLER_LEAF_NAME = "web";

    @Value("${spring.application.name}")
    private String appName;

    protected ApplicationContext applicationContext;
    public SwaggerConfig(@Autowired ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Docket api() {
        Object app = apolloApp(applicationContext);
        ApolloApplication apollo = app.getClass().getAnnotation(ApolloApplication.class);
        String defaultBasePackage = app.getClass().getPackage().getName() + "." + BASE_CONTROLLER_LEAF_NAME;
        String scanPackage = apollo.swaggerBasePackage().trim().isEmpty() ? defaultBasePackage : apollo.swaggerBasePackage();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(scanPackage))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact("****.cloud", "http://*****", "****.cloud@****.com");
        return new ApiInfoBuilder()
                .title(appName)
                .description("Web Service Api Document")
                .version("1.0.0")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }
```

这其实也能解释在A项目中，一开始没有单独命名`groupName("myDocket")`会提示`docket`重复冲突的错误，在B项目的`SwaggerConfig.class`中，``groupName`是默认的，也能发现这个里面有一个写法是默认的`package`：

```java
    String defaultBasePackage = app.getClass().getPackage().getName() + "." + BASE_CONTROLLER_LEAF_NAME;
```

重新审视了A项目的注解，主要是启动类上的注解，发现了采用了复合注解的写法，下面的扩展内容会说到

```java
@ApolloApplication
@ServletComponentScan
public class XXXApplication {
    public static void main(String[] args) {
        SpringApplication.run(XXXApplication.class, args);
    }
}
```

`@ApolloApplication`注解是复合注解，源码是这样的：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface ApolloApplication {
    @AliasFor(annotation = EnableAutoConfiguration.class, attribute = "exclude")
    Class<?>[] exclude() default {};
    
    @AliasFor(annotation = EnableAutoConfiguration.class, attribute = "excludeName")
    String[] excludeName() default {};
    
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {"com.******"};
    
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};
    
    String swaggerBasePackage() default "";
}
```

此注解集成了`@SpringBootConfiguration`,`@EnableAutoConfiguration`，`@ComponentScan`等注解，其中的一个参数`swaggerBasePackage`,指的是不指定包扫描的路径,所以调整下启动类的注解

```javascript
@ApolloApplication(swaggerBasePackage = "com.****.controller")
@ServletComponentScan
public class XXXApplication {
    public static void main(String[] args) {
        SpringApplication.run(XXXApplication.class, args);
    }
}
```

接口信息顺利加载出来

![image-20200911095410724](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\element\swagger\SpringBoot配置Swagger2接口未显示分析.assets\image-20200911095410724.png)

> 注解扩展

**@EnableAutoConfiguration**

@EnableAutoConfiguration的作用是启动自动配置，意思是Spring Boot会根据你添加的jar包来配置你项目的默认设置，比如你添加了Spring Boot提供的spring-boot-starter-web依赖，其中包含了Tomcat和Spring MVC，这个注释就会假设你正在开发一个Web应用程序，自动地帮你添加Web项目中所需要的Spring配置。

**@ComponentScan**

@ComponentScan的作用是扫描当前包及其子包下被@Component注解标记的类并纳入到Spring容器中进行管理。是Spring传统XML配置的<context:component-scan>的替代。

@Controller，@Service，@Repository是@Component的子注解，所以也会被@ComponentScan扫描并做和@Component相同的处理。

@ComponentScan提供了basePackage参数定义要扫描的包，如果不设置，默认会扫描包的所有类，即默认扫描**/*.class路径，建议加上该参数以减少加载的时间。

**@SpringBootApplication提供的参数**

**exclude**

exlude参数继承自@EnableAutoConfiguration注解的同名参数，根据class来排除特定的类加入Spring容器，传入参数的value类型是class类型数组。

```
@SpringBootApplication(exclude = {Good.class, Bad.class})
```

**excludeName**

exludeName参数继承自@EnableAutoConfiguration注解的同名参数，根据className来排除特定的类加入Spring容器，传入参数的value类型是class的全类名字符串数组。

```
@SpringBootApplication(excludeName = {"com.yanggb.xxx.Good", "com.yanggb.yyy.Bad"})
```

exclude和excludeName可以用来关闭指定的自动配置，比如关闭数据源相关的自动配置。

**scanBasePackages**

scanBasePackages参数继承自@ComponentScan注解的basePackages参数，指定要扫描的包，传入参数的value类型是包名的字符串数组。

```
@SpringBootApplication(scanBasePackages = {"com.yanggb.xxx", "com.yanggb.yyy"})
```

**scanBasePackageClasses**

scanBasePackageClasses参数继承自@ComponentScan注解的basePackageClasses参数，指定要扫描的包，传入参数的value类型是包名的字符串数组。

```java
@SpringBootApplication(scanBasePackageClasses = {Good.class, Bad.class}
```



### Reference

1.[springboot注解@SpringBootApplication分析](https://www.cnblogs.com/yanggb/p/10334914.html)

