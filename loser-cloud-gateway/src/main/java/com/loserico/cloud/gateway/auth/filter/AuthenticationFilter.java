package com.loserico.cloud.gateway.auth.filter;

import com.loserico.cloud.gateway.auth.properties.GatewayAuthProperties;
import com.loserico.cloud.gateway.exception.GatewayException;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.gateway.auth.common.TokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;

@Slf4j
@EnableConfigurationProperties(value= {GatewayAuthProperties.class, GatewayProperties.class})
public class AuthenticationFilter implements GlobalFilter, Ordered {
	
	private PublicKey publicKey;
	
	@Autowired
	private GatewayAuthProperties gatewayAuthProperties;
	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 请求各个微服务 不需要用户认证的URL
	 */
	private String string;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestPath = exchange.getRequest().getURI().getPath();
		log.info("网关开始认证url: {}", requestPath);
		
		if (shouldSkip(requestPath)) {
			log.info("无需认证的路径: {}", requestPath);
			return chain.filter(exchange);
		}
		
		//获取请求头
		String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (StringUtils.isEmpty(authorization)) {
			log.warn("请求的url需要认证, 但是Authorization为空");
			throw new GatewayException(ErrorTypes.MISSING_AUTHORIZATION);
		}
		
		TokenInfo tokenInfo = null;
		try {
			tokenInfo = getTokenInfo(authorization);
		} catch (Exception e) {
			log.error("校验令牌异常:{}", e);
			throw new GatewayException(ErrorTypes.GATEWAY_TOKEN_INVALID);
		}
		
		ServerHttpRequest request = exchange.getRequest().mutate().header("username", tokenInfo.getUsername()).build();
		//将现在的request 变成 change对象
		ServerWebExchange serverWebExchange = exchange.mutate().request(request).build();
		serverWebExchange.getAttributes().put("tokenInfo", tokenInfo);
		
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
	
	private TokenInfo getTokenInfo(String authHeader) {
		String token = StringUtils.substringAfter(authHeader, "bearer ");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(gatewayAuthProperties.getAuth().getClientId(), gatewayAuthProperties.getAuth().getClientSecret());
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token", token);
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
		ResponseEntity<TokenInfo> responseEntity = restTemplate.exchange(gatewayAuthProperties.getAuth().getCheckTokenUrl(), HttpMethod.POST, entity, TokenInfo.class);
		log.info("Token info: ", responseEntity.getBody().toString());
		return responseEntity.getBody();
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
}
