# 一 日期类型绑定支持

## 1.1 URL请求参数日期类型绑定

Controller

```java
@GetMapping("/birthday")
public Date dateBind(Date birthday) {
  return birthday;
}
```

http://localhost:8080/order/birthday?birthday=2982-11-09

直接传日期参数SpringBoot默认是无法绑定的

```
2023-02-27 16:56:01.025  WARN 10428 --- [nio-8080-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver L199  : Resolved [org.springframework.web.method.annotation.MethodArgumentTypeMismatchException: Failed to convert value of type 'java.lang.String' to required type 'java.util.Date'; nested exception is org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type [java.util.Date] for value '2982-11-09'; nested exception is java.lang.IllegalArgumentException]
```

loser-spring-boot-web-starter#GlobalBindingAdvice里面通过配置相应的PropertyEditorSupport, 以达到对:

1. java.util.Date
2. java.time.LocalDate
3. java.time.LocalDateTime
4. java.time.LocalTime

这几种日期对象绑定的支持



## 1.2 输出结果日期类型格式化

如果Controller返回的是一个Date对象, 默认输出格式是UTC日期格式: "2982-11-08T16:00:00.000+00:00"

HttpMessageConverterAutoConfiguration#mappingJackson2HttpMessageConverter方法里面对Spring内置的ObjectMapper增强之后就能输出符合我们习惯的日期格式了



## 1.3 URL enum类型参数绑定

Controller方法参数是enum类型的话, 传的字符串参数要大写, 与enum完全匹配才行, 否则报:

```
.w.s.m.s.DefaultHandlerExceptionResolver L199  : Resolved [org.springframework.web.method.annotation.MethodArgumentTypeMismatchException: Failed to convert value of type 'java.lang.String' to required type 'com.loserico.cloud.enums.OrderType'; nested exception is org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type [com.loserico.cloud.enums.OrderType] for value 'sec_kill'; nested exception is java.lang.IllegalArgumentException: No enum constant com.loserico.cloud.enums.OrderType.sec_kill]
```

LoserMvcConfiguration实现了WebMvcConfigurer接口, 通过override #addFormatters 方法像Spring MVC 注入一个自定义的GenericEnumConverter以支持Controller方法Enum类型大小写不敏感的绑定, 并且默认配置了可以按Enum对象的自定义属性code或者desc来绑定, 配置如下:

```java
@Override
public void addFormatters(FormatterRegistry registry) {
  Set<String> properties = new HashSet<>();
  properties.add("code");
  properties.add("desc");
  registry.addConverter(new GenericEnumConverter(properties));
  WebMvcConfigurer.super.addFormatters(registry);
}
```

Controller方法如下:

```java
@GetMapping("/type")
public OrderType dateBind(OrderType orderType) {
  return orderType;
}
```

```java
public enum OrderType {
	SEC_KILL(100, "秒杀"),
	PROMOTION(99, "促销");
	private int code;
	private String desc;
	
	private OrderType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
```

支持的请求示例:

1. http://localhost:8080/order/type?orderType=促销
2. http://localhost:8080/order/type?orderType=99



## 1.4 RequestBody绑定到Bean中num类型属性

是通过在HttpMessageConverterAutoConfiguration#mappingJackson2HttpMessageConverter方法中

```java
ObjectMapperDecorator decorator = new ObjectMapperDecorator();
decorator.decorate(objectMapper);
```

对Spring容器中的objectMapper做了装饰增强后的效果

## 1.5 Requestbody绑定到Bean的日期类型属性

也是是通过在HttpMessageConverterAutoConfiguration#mappingJackson2HttpMessageConverter方法中

```java
@Bean
public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
  MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
  ObjectMapperDecorator decorator = new ObjectMapperDecorator();
  /*
   * Controller通过一个Bean接收json数据, 对bean中的enum类型属性等的增强, 默认不支持这些类型的绑定
   */
  decorator.decorate(objectMapper); //objectMapper是@Autowired进来的
  mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
  return mappingJackson2HttpMessageConverter;
}
```

对Spring容器中的objectMapper做了装饰增强后的效果



