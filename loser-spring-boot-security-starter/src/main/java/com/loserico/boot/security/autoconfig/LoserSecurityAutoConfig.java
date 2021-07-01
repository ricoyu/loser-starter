package com.loserico.boot.security.autoconfig;

import com.loserico.boot.security.controller.RsaController;
import com.loserico.boot.security.controller.VerifyCodeController;
import com.loserico.boot.security.handler.RestAccessDeniedHandler;
import com.loserico.boot.security.processor.AuthUtilsInitializePostProcessor;
import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.security.advice.RestSecurityExceptionAdvice;
import com.loserico.security.endpoint.RestAuthenticationEntryPoint;
import com.loserico.security.processor.ObjectMapperBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

/**
 * <p>
 * Copyright: (C), 2020-08-14 13:55
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties({LoserSecurityProperties.class})
public class LoserSecurityAutoConfig {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Bean
	public AuthUtilsInitializePostProcessor authUtilsInitializePostProcessor() {
		return new AuthUtilsInitializePostProcessor();
	}
	
	@Bean
	public ObjectMapperBeanPostProcessor securityObjectMapperPostProcessor() {
		return new ObjectMapperBeanPostProcessor();
	}
	
	@Bean
	@ConditionalOnMissingBean(RestSecurityExceptionAdvice.class)
	public RestSecurityExceptionAdvice restSecurityExceptionAdvice() {
		return new RestSecurityExceptionAdvice();
	}
	
	/**
	 * 用来设置SpringSecurity中@Secured("ROLE_custom-rule")等注解指定的角色名字是否需要加ROLE_前缀
	 */
	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(properties.getRolePrefix());
	}
	
	@Bean
	public RestAccessDeniedHandler restAccessDeniedHandler() {
		return new RestAccessDeniedHandler();
	}
	
	@Bean
	public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}
	
	/**
	 * 根据是否启用验证码功能动态注册验证码Controller
	 */
	@Bean
	@ConditionalOnProperty(prefix = "loser.security.pic-code", name = "enabled", havingValue = "true", matchIfMissing = false)
	public VerifyCodeController verifyCodeController() {
		return new VerifyCodeController();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public RsaController rsaController() {
		properties.getWhiteList().add("/public-key");
		return new RsaController();
	}
	
}
