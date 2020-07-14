package com.loserico.cloud.autoconfig;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.DefaultBlockRequestHandler;
import com.loserico.cloud.gateway.handler.GatewayBlockRequestHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2020/4/25 15:04
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnClass(DefaultBlockRequestHandler.class)
public class LoserGatewayAutoConfiguration {
	
	@Bean
	public GatewayBlockRequestHandler gatewayBlockRequestHandler() {
		return new GatewayBlockRequestHandler();
	}
}
