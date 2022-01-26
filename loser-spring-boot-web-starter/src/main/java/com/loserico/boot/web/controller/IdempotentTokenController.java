package com.loserico.boot.web.controller;

import com.loserico.boot.web.autoconfig.LoserMvcProperties;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Copyright: (C), 2022-01-21 17:18
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@RestController
public class IdempotentTokenController {
	
	@Autowired
	private LoserMvcProperties properties;
	
	@GetMapping("/idempotent-token")
	public String idempotentToken() {
		String idempotentToken = StringUtils.uniqueKey(36).toLowerCase();
		JedisUtils.HASH.hset("idempotent-token", idempotentToken, "", properties.getIdemtotentTokenTtl());
		return idempotentToken;
	}
}
