package com.loserico.boot.annotation.processor;

import com.loserico.boot.annotation.RedisListener;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
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
@Slf4j
public class RedisListenerProcessor implements SmartInitializingSingleton {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public void afterSingletonsInstantiated() {
		this.applicationContext.getBeansOfType(null, false, false).values()
				.stream()
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
									method.invoke(bean, channel, message);
								} catch (IllegalAccessException | InvocationTargetException e) {
									String msg = "Invoke @RedisListener annotation method failed! bean:" + bean.getClass() + " method:" + method.getName();
									log.error(msg, e);
									throw new RuntimeException(msg, e);
								}
							}, channels);
						}
						
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
									method.invoke(bean, channel, message);
								} catch (IllegalAccessException | InvocationTargetException e) {
									String msg = "Invoke @RedisListener annotation method failed! bean:" + bean.getClass() + " method:" + method.getName();
									log.error(msg, e);
									throw new RuntimeException(msg, e);
								}
							}, channelPatterns);
						}
					});
				});
	}
}
