package com.loserico.boot.security.autoconfig;

import com.loserico.boot.security.intercepter.RateLimitIntercepter;
import com.loserico.boot.security.intercepter.TokenBasedAntiDupSubmitIntercepter;
import com.loserico.boot.security.props.LoserSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * Copyright: (C), 2021-05-28 17:00
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties({LoserSecurityProperties.class})
public class LoserWebAutoConfig implements WebMvcConfigurer {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (properties.isRateLimit()) {
			registry.addInterceptor(new RateLimitIntercepter()); //实现应用限流
		}
		if (properties.isAntiDuplicateSubmit()) {
			registry.addInterceptor(new TokenBasedAntiDupSubmitIntercepter()); //实现防止重复提交
		}
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
