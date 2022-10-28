package com.loserico.cloud.gateway.auth.filter;

import com.loserico.cloud.gateway.auth.properties.GatewayAuthProperties;
import com.loserico.cloud.gateway.client.LoserRestTemplate;
import com.loserico.cloud.gateway.exception.GatewayException;
import com.loserico.codec.Base64Utils;
import com.loserico.common.lang.errors.ErrorTypes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * <p>
 * Copyright: (C), 2022-10-27 11:38
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered, InitializingBean {
	
	public static final String SCHEMA = "http://";
	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
	
	@Autowired
	private LoserRestTemplate loserRestTemplate;
	
	@Autowired
	private GatewayAuthProperties gatewayAuthProperties;
	
	private PublicKey publicKey;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestPath = exchange.getRequest().getURI().getPath();
		log.info("网关认证开始URL->:{}", requestPath);
		
		if (shouldSkip(requestPath)) {
			log.info("无需认证的路径: {}", requestPath);
			return chain.filter(exchange);
		}
		
		//获取请求头
		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		//请求头为空
		if (StringUtils.isEmpty(authHeader)) {
			log.warn("需要认证的url,请求头为空");
			throw new GatewayException(ErrorTypes.MISSING_AUTHORIZATION);
		}
		
		//校验我们的jwt 若jwt不对或者超时都会抛出异常
		Claims claims = validateJwtToken(authHeader);
		
		//向headers中放文件，记得build
		ServerHttpRequest request = exchange.getRequest().mutate().header("username", claims.get("user_name").toString()).build();
		//将现在的request 变成 change对象
		ServerWebExchange serverWebExchange = exchange.mutate().request(request).build();
		
		//从jwt中解析出权限集合进行判断
		checkHasPremisson(claims, requestPath);
		
		return chain.filter(serverWebExchange);
	}
	
	
	public boolean shouldSkip(String requestPath) {
		for (String shouldSkipUrl : gatewayAuthProperties.getAuth().getShouldSkipUrls()) {
			if (ANT_PATH_MATCHER.match(shouldSkipUrl, requestPath)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 校验JWT Token
	 *
	 * @param authHeader
	 * @return
	 */
	private Claims validateJwtToken(String authHeader) {
		String token = null;
		try {
			token = substringAfter(authHeader, "bearer ");
			
			Jwt<JwsHeader, Claims> parseClaimsJwt = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
			
			Claims claims = parseClaimsJwt.getBody();
			log.info("claims:{}", claims);
			return claims;
		} catch (ExpiredJwtException e) {
			log.error("校验JWT Token异常:{},异常信息:{}", token, e);
			throw new GatewayException(ErrorTypes.TOKEN_EXPIRED);
		} catch (Exception e) {
			log.error("校验JWT Token异常:{},异常信息:{}", token, e);
			throw new GatewayException(ErrorTypes.TOKEN_INVALID);
		}
	}
	
	private boolean checkHasPremisson(Claims claims, String currentUrl) {
		boolean hasPremisson = false;
		//登陆用户的权限集合判断
		List<String> premessionList = claims.get("authorities", List.class);
		for (String url : premessionList) {
			if (ANT_PATH_MATCHER.match(url, currentUrl)) {
				hasPremisson = true;
				break;
			}
		}
		if (!hasPremisson) {
			log.warn("权限不足");
			throw new GatewayException(ErrorTypes.ACCESS_DENIED);
		}
		
		return hasPremisson;
	}
	
	private String getTokenKey() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(gatewayAuthProperties.getAuth().getClientId(), Base64Utils.decode(gatewayAuthProperties.getAuth().getClientSecret()));
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(null, headers);
		
		try {
			
			ResponseEntity<Map> response = loserRestTemplate.exchange(getFullUrl(gatewayAuthProperties.getAuth().getTokenKeyEndpoint()), 
					HttpMethod.GET, entity, Map.class);
			
			String tokenKey = response.getBody().get("value").toString();
			
			log.info("去认证服务器获取TokenKey:{}", tokenKey);
			
			return tokenKey;
		} catch (Exception e) {
			
			log.error("远程调用认证服务器获取tokenKey失败:{}", e.getMessage());
			throw new GatewayException(ErrorTypes.OAUTH2_GET_TOKENKEY_ERROR);
		}
	}
	
	private PublicKey genPublicKeyByTokenKey() {
		
		try {
			String tokenKey = getTokenKey();
			
			String dealTokenKey = tokenKey.replaceAll("\\-*BEGIN PUBLIC KEY\\-*", "").replaceAll("\\-*END PUBLIC KEY\\-*", "").trim();
			
			java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(dealTokenKey));
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
			
			log.info("生成公钥:{}", publicKey);
			
			return publicKey;
		} catch (Exception e) {
			log.info("生成公钥异常:{}", e.getMessage());
			throw new GatewayException(ErrorTypes.OAUTH2_GET_TOKENKEY_ERROR);
		}
		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//初始化公钥
		this.publicKey = genPublicKeyByTokenKey();
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
	
	private String getFullUrl(String uri) {
		return SCHEMA + gatewayAuthProperties.getAuth().getAuthServerName()+uri;
	}
}
