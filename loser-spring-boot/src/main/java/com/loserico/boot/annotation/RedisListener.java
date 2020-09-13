package com.loserico.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监听Redis事件, 事件过来后, 会传入两个参数到目标方法:
 * channel 发生事件的频道
 * message 具体的消息
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
	 * 
	 * h?llo subscribes to hello, hallo and hxllo
	 * h*llo subscribes to hllo and heeeello
	 * h[ae]llo subscribes to hello and hallo, but not hillo
	 * 
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
