package com.loserico.cloud.autoconfig;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.DefaultBlockRequestHandler;
import com.loserico.cloud.gateway.client.LoserRestTemplate;
import com.loserico.cloud.gateway.handler.GatewayBlockRequestHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
	
	/**
	 * 支持服务一起动就可以通过服务名调用的版本
	 * @param discoveryClient
	 * @return LoserRestTemplate
	 */
	@Bean
	@LoadBalanced
	public LoserRestTemplate loserRestTemplate(DiscoveryClient discoveryClient) {
		return new LoserRestTemplate(discoveryClient);
	}
}
