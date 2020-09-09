package com.loserico.boot.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.boot.mixin.GrantedAuthorityMixIn;
import com.loserico.boot.mixin.SimpleGrantedAuthorityMixIn;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 为Spring Security添加序列化/反序列化支持
 * <p>
 * Copyright: Copyright (c) 2020-08-14 13:51
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
public class ObjectMapperBeanPostProcessor implements BeanPostProcessor, Ordered {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ObjectMapper) {
			ObjectMapper objectMapper = (ObjectMapper)bean;
			objectMapper.addMixIn(GrantedAuthority.class, GrantedAuthorityMixIn.class);
			objectMapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixIn.class);
			return objectMapper;
		}
		return bean;
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
