package com.loserico.cloud.autoconfig;

import com.loserico.cloud.gateway.filter.TimeMonitorGatewayFilterFactory;
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
public class GatewayFilterAutoConfiguration {
	
	@Bean
	public TimeMonitorGatewayFilterFactory timeMonitorGatewayFilterFactory() {
		return new TimeMonitorGatewayFilterFactory();
	}
}
