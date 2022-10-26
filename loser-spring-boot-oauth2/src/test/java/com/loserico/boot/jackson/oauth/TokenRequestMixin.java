package com.loserico.boot.jackson.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Map;

public abstract class TokenRequestMixin {
	
	@JsonCreator
	public TokenRequestMixin(@JsonProperty("requestParameters") Map<String, String> requestParameters, @JsonProperty("clientId") String clientId,
	                         @JsonProperty("scope") Collection<String> scope, @JsonProperty("grantType") String grantType) {
	}
}
