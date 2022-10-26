package com.loserico.oauth2.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BadCredentialsExceptionMixin {

	/**
	 * Constructor used by Jackson to create
	 * {@link org.springframework.security.authentication.BadCredentialsException} object.
	 *
	 * @param message the detail message
	 */
	@JsonCreator
	public BadCredentialsExceptionMixin(@JsonProperty("message") String message) {}
}
