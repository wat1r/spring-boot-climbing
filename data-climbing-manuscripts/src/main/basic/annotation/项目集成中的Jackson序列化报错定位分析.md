

```java
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CellColType.class, name = "cell"),
        @JsonSubTypes.Type(value = WrappedColType.class, name = "wrap")
})
public abstract class DisplayColumnType {
 ....   
}


@JsonTypeName("wrap")
public abstract class CellColType extends DisplayColumnType{
    @Override
    public boolean canBeWrap() {
        return true;
    }
}

@JsonTypeName("cell")
public abstract class WrappedColType extends DisplayColumnType{
 ...   
}
```



[Jackson对多态和多子类序列化的处理配置](https://my.oschina.net/u/3664884/blog/1932829)

```log
Caused by: org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Can not construct instance of com.sdg.dpd.apollo.meta.manager.beans.types.DisplayColumnType: abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information; nested exception is com.fasterxml.jackson.databind.JsonMappingException: Can not construct instance of com.sdg.dpd.apollo.meta.manager.beans.types.DisplayColumnType: abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
 at [Source: java.io.PushbackInputStream@763dad40; line: 1, column: 207] (through reference chain: com.sdg.dpd.apollo.meta.manager.beans.TableOverView["info"]->com.sdg.dpd.apollo.meta.manager
```







```java
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper disable = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        return disable;
//    }
```

```java
//@EnableWebMvc
```



```java
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(jacksonMessageConverter());
//        super.configureMessageConverters(converters);
//    }
//
//    private MappingJackson2HttpMessageConverter jacksonMessageConverter() {
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper mapper = new ObjectMapper();
////        mapper.registerModule(new Hibernate4Module());
//        messageConverter.setObjectMapper(mapper);
//        return messageConverter;
//    }
```







https://www.cnblogs.com/lvbinbin2yujie/p/10624584.html