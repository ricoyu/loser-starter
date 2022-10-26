package com.loserico.oauth2.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class AnonymousAuthenticationTokenMixin {

	/**
	 * Constructor used by Jackson to create object of {@link org.springframework.security.authentication.AnonymousAuthenticationToken}.
	 *
	 * @param keyHash hashCode of key provided at the time of token creation by using
	 *                {@link org.springframework.security.authentication.AnonymousAuthenticationToken#AnonymousAuthenticationToken(String, Object, Collection)}
	 * @param principal the principal (typically a <code>UserDetails</code>)
	 * @param authorities the authorities granted to the principal
	 */
	@JsonCreator
	public AnonymousAuthenticationTokenMixin(@JsonProperty("keyHash") Integer keyHash, @JsonProperty("principal") Object principal,
	                                         @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
	}
}
