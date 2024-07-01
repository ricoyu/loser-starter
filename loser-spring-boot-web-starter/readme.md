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



# 二 WebSocket支持

```yaml
loser:
  websocket:
    enabled: true
    pathPrefix: /ws/push/**
  cache:
    enabled: true
```

客户端要求后端Websocket服务器推送消息时, 如果是分布式部署的, 那么有可能值推送连接到某一台服务器上的websocket客户端
 * 所以需要额外处理, 使得所有websocket服务器上连接的客户端都被通知到

通过自动添加WebSocketFilter来拦截特定的请求, 这个filter拦截特定的URI, 然后通过Redis发布一条消息, 消息的channel是uri的后半部分, 消息内容是request body部分

**意思就是:**

> 调用HTTP接口, 路径匹配http://localhost:8080/ws/push/weekend就会被这个filter拦截, 然后这个filter读取消息体, 以/ws/push/后面的路径(weekend)为channel发送Redis PUB/SUB, 分布式websocket服务端监听Redis消息, 然后把它存储的websocket session拿出来, 挨个推送一下消息



# 三 国际化支持

1. application.yml配置

   ```yaml
   loser:
     locale:
       enabled: true
   ```

2. src\main\resources下创建i18m目录

   **重要:** 必须要一个messages.properties, 否则MessageSource是一个空的MessageSource对象, 里面实际没有加载国际化资源文件

   **第二个重要:**

   * messages.properties        放中文
   * messages_en_US.properties  放英文

   如果只有这两个文件, 在Windows系统里面切换都OK的, 但是到了Linux系统, 中文始终出不来, 经测试, 必须加另外一个

   * messages_zh_CN.properties  放中文



## 3.1 编码方式获取国际化消息

```java
I18N.i18nMessage("account.retry.locked", 3, 1000)
```

这个template在message.properties中

```properties
account.retry.locked=密码错误次数已达到{0}次，账户锁定{1}分钟
```



# 四 Jackson定制

出现过这样一个问题: 自定义了ObjectMapper, 为其添加了自定义序列化器, 但是实测Controller输出JSON并没有走这个自定义Serializer

解决:

```java
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class HttpMessageConverterAutoConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapperDecorator decorator = new ObjectMapperDecorator();
		decorator.decorate(objectMapper);
		mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
		return mappingJackson2HttpMessageConverter;
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, mappingJackson2HttpMessageConverter());
	}
}
```

# 五 自动配置Encoding

1. LoserMvcConfiguration会自动配置CharacterEncodingFilter, 字符编码设为UTF-8, 但是请注意, 这个CharacterEncodingFilter堆下面这种Controller是无效的, 返回的还是乱码

   ```java
   @CrossOrigin()
   @GetMapping(value = "/hello")
   public String hello() throws InterruptedException {
     return "hi 三少爷";
   }
   ```

2. 其实这种方式返回的数据是由HttpMessageConverter负责输出的, 所以要配置HttpMessageConverter的编码格式吗,这个在com.loserico.boot.web.autoconfig.HttpMessageConverterAutoConfiguration做了配置

   ```java
   /*
    * 添加这个是处理在返回String类型的结果时, 多了一个双引号问题
    */
   List<MediaType> mediaTypes = new ArrayList<MediaType>();
   mediaTypes.add(MediaType.TEXT_PLAIN);
   mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
   mediaTypes.add(MediaType.APPLICATION_JSON);
   //构造函数必须传默认编码, 不然返回字符串带中文的湖乱码
   StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(UTF_8);
   stringHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
   converters.add(0, stringHttpMessageConverter);
   ```

   

   
