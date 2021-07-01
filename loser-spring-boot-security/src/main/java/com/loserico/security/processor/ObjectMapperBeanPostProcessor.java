package com.loserico.security.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.security.mixin.GrantedAuthorityMixIn;
import com.loserico.security.mixin.SimpleGrantedAuthorityMixIn;
import com.loserico.security.mixin.UnmodifiableSetMixin;
import com.loserico.security.mixin.UserMixin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashSet;

/**
 * 为Spring Security相关对象添加序列化/反序列化支持
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
			objectMapper.addMixIn(User.class, UserMixin.class);
			objectMapper.addMixIn(Collections.unmodifiableSet(new HashSet<>(0)).getClass(), UnmodifiableSetMixin.class);
			return objectMapper;
		}
		return bean;
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
