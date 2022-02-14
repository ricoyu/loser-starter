package com.loserico.security;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * <p>
 * Copyright: (C), 2022-02-14 10:50
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class AntMatchTest {
	
	@Test
	public void testAntMatch() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/device/log/download/**", HttpMethod.POST.name());
		matcher.
	}
}
