package com.loserico.boot.autoconfig;

import com.loserico.boot.autoconfig.processor.AuthUtilsInitializePostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开启条件 loser.auth.enabled=true
 * <p>
 * Copyright: (C), 2020-08-14 9:56
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "loser.auth", name = "enabled", havingValue = "true")
public class LoserAuthConfiguration {
	
	@Bean
	public AuthUtilsInitializePostProcessor authUtilsInitializePostProcessor() {
		return new AuthUtilsInitializePostProcessor();
	}
}
