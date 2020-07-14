package com.loserico.boot.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Copyright: (C), 2020/4/29 20:48
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public abstract class OAuth2RequestMixin {
	
	@JsonCreator
	public OAuth2RequestMixin(@JsonProperty("requestParameters") Map<String, String> requestParameters, @JsonProperty("clientId") String clientId,
	                          @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities, @JsonProperty("isApproved") boolean approved, 
	                          @JsonProperty("scope") Set<String> scope, @JsonProperty("resourceIds") Set<String> resourceIds, 
	                          @JsonProperty("redirectUri") String redirectUri, @JsonProperty("responseTypes") Set<String> responseTypes,
	                          @JsonProperty("extensionProperties") Map<String, Serializable> extensionProperties){}
}
