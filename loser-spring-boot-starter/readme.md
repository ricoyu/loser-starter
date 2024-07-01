# 一 @RedisListener注解支持

1. 先要引入starter:

   ```xml
   <dependency>
       <groupId>com.loserico</groupId>
       <artifactId>loser-spring-boot-starter</artifactId>
       <version>2.6.2</version>
   </dependency>
   ```

   com.loserico.boot.annotation.processor.RedisListenerProcessor类负责处理@RedisListener注解

2. src/main/resources下放redis.properties, 配置Redis IP和密码

   ```properties
   redis.host=192.168.100.13
   redis.password=deepdata$
   ```

3. application.yaml增加配置

   ```yaml
   loser.cache.enabled: true
   ```

即可开启对@RedisListener的支持

# 二 @RedisListener使用示例

## 2.1 Key过期订阅

1. 需要订阅的方法增加注解

   下面这个方法在Redis任何key过期时会收到通知, 收到消息时传入的参数:

   * channel 参数值固定为 `__keyevent@0__:expired`
   * message 参数值为过期的key, 如k1

   ```java
   @RedisListener(channelPatterns = "__keyevent@*__:expired")
   public void listen(String channel, String message) {
     log.info("{} 频道上收到消息: {}", channel, message);
   }
   ```

## 2.2 PUB/SUB订阅

1. 需要订阅的方法增加注解

   ```java
   @RedisListener(channels = "inbound")
   public void broadCastMessage(String channel, String message) {
     System.out.println(channel+": " + message);
   }
   ```

2. 在Redis发布一条消息

   ```shell
   127.0.0.1:6379> publish inbound 三少爷
   (integer) 1
   ```

3. 接收参数解释

   * channel 在哪个topic上收到的消息就是哪个topic的名字, 这边是示例中的inbound
   * message 发布的消息, 这边是示例中的 三少爷



# 三 @PostInitialize注解支持

Spring官方的@PostConstruct也可以在容器启动后自动执行指定方法, 但此时Spring事务还未准备好, 而@PostInitialize可以在Spring的事务一键完全Ready的情况下自动运行

# 四 日期转换

LoserConverterAutoConfiguration配置了bean: LocalTimeConverter来实现对LocalTime的自动转换

# 五 解决循环依赖

Circular References Prohibited by Default in spring boot version 2.6

所以现在默认不允许有循环依赖了, 所以我在这个starter的classpath下放了一个application.properties文件, 里面配置了

```properties
spring.main.allow-circular-references=true
```

使得SpringBoot应用跟以前一样, 默认可以自动解决循环依赖问题