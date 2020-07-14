package com.loserico.boot.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;

public abstract class UnmodifiableSetMixin {

	/**
	 * Mixin Constructor
	 * @param s the Set
	 */
	@JsonCreator
	public UnmodifiableSetMixin(Set<?> s) {}
}