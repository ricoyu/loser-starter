package com.loserico.security.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.security.mixin.AnonymousAuthenticationTokenMixin;
import com.loserico.security.mixin.BadCredentialsExceptionMixin;
import com.loserico.security.mixin.JaasGrantedAuthorityMixin;
import com.loserico.security.mixin.OAuth2AuthenticationMixin;
import com.loserico.security.mixin.OAuth2RequestMixin;
import com.loserico.security.mixin.RememberMeAuthenticationTokenMixin;
import com.loserico.security.mixin.SwitchUserGrantedAuthorityMixin;
import com.loserico.security.mixin.TokenRequestMixin;
import com.loserico.security.mixin.UserMixin;
import com.loserico.security.mixin.UsernamePasswordAuthenticationTokenMixin;
import com.loserico.json.ObjectMapperDecorator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

/**
 * 找到ObjectMapper, 为其添加OAuth2Authentication的MixIn
 * <p>
 * Copyright: (C), 2020/4/29 18:59
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class OAuth2ObjectMapperBeanPostProcessor implements BeanPostProcessor, Ordered {
	
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
			addSecurityMixin(objectMapper);
			return objectMapper;
		}
		return bean;
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
	
	private void addSecurityMixin(ObjectMapper objectMapper) {
		objectMapper.addMixIn(AnonymousAuthenticationToken.class, AnonymousAuthenticationTokenMixin.class);
		objectMapper.addMixIn(BadCredentialsException.class, BadCredentialsExceptionMixin.class);
		objectMapper.addMixIn(JaasGrantedAuthority.class, JaasGrantedAuthorityMixin.class);
		objectMapper.addMixIn(OAuth2Authentication.class, OAuth2AuthenticationMixin.class);
		objectMapper.addMixIn(OAuth2Request.class, OAuth2RequestMixin.class);
		objectMapper.addMixIn(RememberMeAuthenticationToken.class, RememberMeAuthenticationTokenMixin.class);
		objectMapper.addMixIn(SwitchUserGrantedAuthority.class, SwitchUserGrantedAuthorityMixin.class);
		objectMapper.addMixIn(TokenRequest.class, TokenRequestMixin.class);
		objectMapper.addMixIn(User.class, UserMixin.class);
		objectMapper.addMixIn(UsernamePasswordAuthenticationToken.class, UsernamePasswordAuthenticationTokenMixin.class);
	}
}
