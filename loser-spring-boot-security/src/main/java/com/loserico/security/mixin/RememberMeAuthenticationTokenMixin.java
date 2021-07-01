package com.loserico.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class RememberMeAuthenticationTokenMixin {

	/**
	 * Constructor used by Jackson to create
	 * {@link org.springframework.security.authentication.RememberMeAuthenticationToken} object.
	 *
	 * @param keyHash hashCode of above given key.
	 * @param principal the principal (typically a <code>UserDetails</code>)
	 * @param authorities the authorities granted to the principal
	 */
	@JsonCreator
	public RememberMeAuthenticationTokenMixin(@JsonProperty("keyHash") Integer keyHash,
												@JsonProperty("principal") Object principal,
												@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
	}
}
