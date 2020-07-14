package com.loserico.boot.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Copyright: (C), 2020/4/29 19:46
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public abstract class SimpleGrantedAuthorityMixin {
	
	@JsonCreator
	public SimpleGrantedAuthorityMixin(@JsonProperty("role") String role) {
		
	}
}
