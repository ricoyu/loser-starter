package com.loserico.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Copyright: (C), 2020/4/29 20:16
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public abstract class TokenRequestMixin {
	
	@JsonCreator
	public TokenRequestMixin(@JsonProperty("requestParameters") Map<String, String> requestParameters, @JsonProperty("clientId") String clientId, 
	                        @JsonProperty("scope") Collection<String> scope, @JsonProperty("grantType") String grantType) {
	}
}
