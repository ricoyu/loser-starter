package com.loserico.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;

public abstract class UnmodifiableListMixin {

	/**
	 * Mixin Constructor
	 * @param s the Set
	 */
	@JsonCreator
	public UnmodifiableListMixin(Set<?> s) {}
}
