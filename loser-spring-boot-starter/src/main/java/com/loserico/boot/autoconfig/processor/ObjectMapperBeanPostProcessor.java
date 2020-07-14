package com.loserico.boot.autoconfig.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.json.ObjectMapperDecorator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * <p>
 * Copyright: (C), 2020/4/30 11:05
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class ObjectMapperBeanPostProcessor implements BeanPostProcessor, Ordered {
	
	private ObjectMapperDecorator decorator = new ObjectMapperDecorator();
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ObjectMapper) {
			ObjectMapper objectMapper = (ObjectMapper)bean;
			decorator.decorate(objectMapper);
			return objectMapper;
		}
		return bean;
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
