package com.loserico.cloud.autoconfig;

import com.loserico.cloud.gateway.filter.TimeMonitorGatewayFilterFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2020/4/24 11:05
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(LoserGatewayProperties.class)
public class LoserGatewayFilterAutoConfiguration {
	
	@Bean
	@ConditionalOnProperty(prefix = "loser.gateway", value = "time-monitor-filter-enabled", havingValue = "true", matchIfMissing = true)
	public TimeMonitorGatewayFilterFactory timeMonitorGatewayFilterFactory() {
		return new TimeMonitorGatewayFilterFactory();
	}
}
