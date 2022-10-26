package com.loserico.oauth2.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.Authentication;

/**
 * <p>
 * Copyright: (C), 2020/4/29 19:45
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public abstract class SwitchUserGrantedAuthorityMixin {
	
	@JsonCreator
	public SwitchUserGrantedAuthorityMixin(@JsonProperty("role") String role, @JsonProperty("source") Authentication source) {
		
	}
}
