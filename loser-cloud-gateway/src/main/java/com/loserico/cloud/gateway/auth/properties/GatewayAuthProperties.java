package com.loserico.cloud.gateway.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Copyright: (C), 2022-10-27 10:54
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties("loser.gateway")
public class GatewayAuthProperties {
	
	/**
	 * 网关认证相关配置
	 */
	private Auth auth;
	@Data
	public static class Auth {
		
		/**
		 * 是否启用网关层认证
		 */
		private boolean enabled = false;
		
		/**
		 * 是否采用JWT Token
		 */
		private boolean jwtToken = false;
		
		/**
		 * 如果是JWT token的话, 从认证中心获取公钥的URI, 默认 /oauth/token_key
		 */
		private String tokenKeyEndpoint = "/oauth/token_key";
		
		/**
		 * 非JWT Token时用于向认证中心校验token的url
		 */
		private String checkTokenUrl = "/oauth/check_token";
		
		/**
		 * 不需要认证就可以访问的URL, 默认 "/oauth/token", "/oauth/check_token", "/oauth/token_key"
		 */
		private List<String> shouldSkipUrls = Arrays.asList("/oauth/token", "/oauth/check_token", "/oauth/token_key");
		
		/**
		 * 认证中心在Nacos注册的服务名
		 */
		private String authServerName;
		
		/**
		 * 网关的app ID
		 */
		private String clientId;
		
		/**
		 * 网关的APP Secret, 经过base64加密后的串
		 */
		private String clientSecret;
	}
}
