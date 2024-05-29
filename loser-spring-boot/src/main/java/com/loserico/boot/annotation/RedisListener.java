package com.loserico.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需引入依赖 loser-spring-boot-starter
 * 监听Redis事件, 事件过来后, 会传入两个或一个参数到目标方法:
 * <ul>
 *     <li/>如果目标方法只有一个参数, 那么参数就是消息体
 *     <li/>如果目标方法有两个参数, 那么第一个参数是channel, 第二个参数是消息体
 * </ul>
 * 都是String类型
 * <p>
 * Copyright: Copyright (c) 2020-09-10 11:39
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisListener {
	
	/**
	 * 订阅的频道, channels和channelPatterns二选一, 都指定的话channels win
	 * @return
	 */
	String[] channels() default {};
	
	/**
	 * 订阅的频道, 支持glob风格正则, channels和channelPatterns二选一, 都指定的话channels win
	 * <ul>
	 *     <li/>h?llo subscribes to hello, hallo and hxllo
	 *     <li/>h*llo subscribes to hllo and heeeello
	 *     <li/>h[ae]llo subscribes to hello and hallo, but not hillo
	 *     <li/>h[ae]llo subscribes to hello and hallo, but not hillo
	 *     <li/>如果要订阅Redis的key过期事件, pattern可以这么写: "__keyevent@*__:expired"<br/>
	 *     消息来时, 对应的channel是: "__keyevent@0__:expired"; message就是key的名字
	 * </ul>
	 * @return
	 */
	String[] channelPatterns() default {};
	
	/**
	 * 只有收到的消息满足指定规则, 才会调用该Listener方法
	 * 比如订阅了keyspace, keyevent事件, 那么收到的消息体就是过期的key名
	 * @return
	 */
	String messagePattern() default "";
}
