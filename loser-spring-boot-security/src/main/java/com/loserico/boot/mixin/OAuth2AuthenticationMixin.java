package com.loserico.boot.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * 用来处理没有默认构造函数的对象
 * <p>
 * Copyright: (C), 2020/4/29 18:52
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public abstract class OAuth2AuthenticationMixin {
	
	@JsonCreator
	public OAuth2AuthenticationMixin(@JsonProperty("storedRequest") OAuth2Request storedRequest,
	                                 @JsonProperty("userAuthentication") Authentication userAuthentication) {
		
	}
}