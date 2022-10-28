package com.loserico.gateway.auth.filter;

import com.loserico.cloud.gateway.auth.properties.GatewayAuthProperties;
import com.loserico.cloud.gateway.exception.GatewayException;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.gateway.auth.common.TokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 这个是在AuthorizationFilter校验token通过之后, 校验是否有权限访问请求的url
 * <p>
 * Copyright: (C), 2022-10-14 9:35
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@EnableConfigurationProperties(value= {GatewayAuthProperties.class})
public class AuthorizationFilter implements GlobalFilter, Ordered {
	
	@Autowired
	private GatewayAuthProperties gatewayAuthProperties;
	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestPath = exchange.getRequest().getURI().getPath();
		log.info("网关开始认证url: {}", requestPath);
		
		if (shouldSkip(requestPath)) {
			log.info("无需认证的路径: {}", requestPath);
			return chain.filter(exchange);
		}
		
		//获取当前请求的路径
		String reqPath = exchange.getRequest().getURI().getPath();
		TokenInfo tokenInfo = exchange.getAttribute("tokenInfo");
		
		if (!tokenInfo.isActive()) {
			log.warn("token过期");
			throw new GatewayException(ErrorTypes.TOKEN_EXPIRED);
		}
		
		if (!allowAccess(requestPath, tokenInfo.getAuthorities())) {
			throw new GatewayException(ErrorTypes.ACCESS_DENIED);
		}
		return chain.filter(exchange);
	}
	
	public boolean shouldSkip(String requestPath) {
		for (String shouldSkipUrl : gatewayAuthProperties.getAuth().getShouldSkipUrls()) {
			if (ANT_PATH_MATCHER.match(shouldSkipUrl, requestPath)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean allowAccess(String requestPath, String[] authorities) {
		for (String authority : authorities) {
			if (ANT_PATH_MATCHER.match(authority, requestPath)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getOrder() {
		return 1;
	}
}
