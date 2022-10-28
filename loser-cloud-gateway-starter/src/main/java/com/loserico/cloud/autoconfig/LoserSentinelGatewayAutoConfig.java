package com.loserico.cloud.autoconfig;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * <p>
 * Copyright: (C), 2020/5/1 14:56
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(SentinelGatewayFilter.class)
public class LoserSentinelGatewayAutoConfig {
	
	@Bean
	@Order(-1)
	public GlobalFilter loserSentinelGatewayFilter() {
		return new SentinelGatewayFilter();
	}
}
