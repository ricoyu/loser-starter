package com.loserico.boot.autoconfig;

import com.loserico.boot.processor.OAuth2ObjectMapperBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * <p>
 * Copyright: (C), 2020/4/29 19:08
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(OAuth2Authentication.class)
public class LoserOAuth2AutoConfiguration {
	
	@Bean
	public OAuth2ObjectMapperBeanPostProcessor objectMapperBeanPostProcessor() {
		return new OAuth2ObjectMapperBeanPostProcessor();
	}
}
