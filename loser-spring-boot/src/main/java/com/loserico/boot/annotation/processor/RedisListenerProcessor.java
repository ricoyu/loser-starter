package com.loserico.boot.annotation.processor;

import com.loserico.boot.annotation.RedisListener;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * <p>
 * Copyright: (C), 2020-09-10 14:01
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class RedisListenerProcessor implements SmartInitializingSingleton {
	
	private static Logger log = LoggerFactory.getLogger(RedisListenerProcessor.class);
	
	/**
	 * 在Netty结合Redis PUB/SUB的场景下, 一个方法可能会被注册多次, 所以这里需要记录一下(这是实测的经验教训)
	 */
	private List<String> methods = new ArrayList<>();
	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public void afterSingletonsInstantiated() {
		/*
		 * 实测这个方法拿到的Bean, 如果是加了@Transactional注解的, 那么会有两个bean实例, 一个是代理类的实例, 一个是原始类的实例
		 * 所以不做处理的话下面实际上会为一个方法注册两次, 造成收到订阅消息时一个方法被调用两次
		 */
		Collection<Object> allBeans = this.applicationContext.getBeansOfType(null, false, false).values();
		allBeans.stream()
				.filter(Objects::nonNull)
				.forEach((bean) -> {
					ReflectionUtils.doWithMethods(bean.getClass(), (method) -> {
						RedisListener annotation = AnnotationUtils.findAnnotation(method, RedisListener.class);
						if (annotation == null) {
							return;
						}
						
						String[] channels = annotation.channels();
						if (channels != null && channels.length > 0) {
							JedisUtils.subscribe((channel, message) -> {
								//对消息进行过滤, 只有匹配正则的消息才会被消费
								String messagePattern = annotation.messagePattern();
								Pattern pattern = null;
								if (isNotBlank(messagePattern)) {
									pattern = Pattern.compile(messagePattern);
								}
								
								if (pattern != null && !pattern.matcher(message).matches()) {
									log.info("Message:[{}] does not match pattern {}, message on channel[{}], so will not be consumed", message, messagePattern, channel);
									return;
								}
								try {
									int parameterCount = method.getParameterCount();
									//方法就定义了一个参数的话只传消息内容
									if (parameterCount == 1) {
										method.invoke(bean, message);
									} else {
										//定义了两个参数的话, 第一个参数是channel, 第二个参数是消息内容
										method.invoke(bean, channel, message);
									}
								} catch (IllegalAccessException | InvocationTargetException e) {
									String msg = "Invoke @RedisListener annotation method failed! bean:" + bean.getClass() + " method:" + method.getName();
									log.error(msg, e);
									throw new RuntimeException(msg, e);
								}
							}, channels);
						} else {
							String[] channelPatterns = annotation.channelPatterns();
							if (channelPatterns != null && channelPatterns.length > 0) {
								JedisUtils.psubscribe((channel, message) -> {
									//对消息进行过滤, 只有匹配正则的消息才会被消费
									String messagePattern = annotation.messagePattern();
									Pattern pattern = null;
									if (isNotBlank(messagePattern)) {
										pattern = Pattern.compile(messagePattern);
									}
									
									if (pattern != null && !pattern.matcher(message).matches()) {
										log.info("Message:[{}] does not match pattern {}, message on channel[{}], so will not be consumed", message, messagePattern, channel);
										return;
									}
									try {
										int parameterCount = method.getParameterCount();
										//方法就定义了一个参数的话只传消息内容
										if (parameterCount == 1) {
											method.invoke(bean, message);
										} else {
											//定义了两个参数的话, 第一个参数是channel, 第二个参数是消息内容
											method.invoke(bean, channel, message);
										}
									} catch (IllegalAccessException | InvocationTargetException e) {
										String msg = "Invoke @RedisListener annotation method failed! bean:" + bean.getClass() + " method:" + method.getName();
										log.error(msg, e);
										throw new RuntimeException(msg, e);
									}
								}, channelPatterns);
							}
						}
						
					});
				});
	}
	
	private String toMethodName(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getSimpleName()).append(".").append(method.getName()).append("(");
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			sb.append(method.getParameterTypes()[i].getSimpleName());
			if (i < method.getParameterTypes().length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		String methodFullName = sb.toString();
		log.info("方法全名: {}", methodFullName);
		return methodFullName;
	}
}
