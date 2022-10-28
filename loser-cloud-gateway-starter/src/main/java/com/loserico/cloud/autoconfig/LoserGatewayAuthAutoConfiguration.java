package com.loserico.cloud.autoconfig;

import com.loserico.cloud.gateway.auth.filter.AuthenticationFilter;
import com.loserico.cloud.gateway.auth.filter.JwtAuthenticationFilter;
import com.loserico.cloud.gateway.auth.properties.GatewayAuthProperties;
import com.loserico.gateway.auth.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2022-10-27 11:37
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(GatewayAuthProperties.class)
@ConditionalOnProperty(name = "loser.gateway.auth.enabled", havingValue = "true", matchIfMissing = false)
public class LoserGatewayAuthAutoConfiguration {
	
	@Autowired
	private GatewayAuthProperties gatewayAuthProperties;
	
	@Bean
	@ConditionalOnProperty(name = "loser.gateway.auth.jwt-token", havingValue = "true", matchIfMissing = false)
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	@Bean
	@ConditionalOnProperty(name = "loser.gateway.auth.jwt-token", havingValue = "false", matchIfMissing = true)
	public AuthenticationFilter authenticationFilter() {
		return new AuthenticationFilter();
	}
	
	@Bean
	@ConditionalOnProperty(name = "loser.gateway.auth.jwt-token", havingValue = "false", matchIfMissing = true)
	public AuthorizationFilter authorizationFilter() {
		return new AuthorizationFilter();
	}
}
