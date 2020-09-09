package com.loserico.boot.autoconfig;

import com.loserico.boot.processor.ObjectMapperBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@EnableConfigurationProperties({LoserAuthProperties.class})
public class LoserSecurityAutoConfig {
	
	@Bean
	public ObjectMapperBeanPostProcessor securityObjectMapperPostProcessor() {
		return new ObjectMapperBeanPostProcessor();
	}
}
