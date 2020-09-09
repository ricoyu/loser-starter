package com.loserico.boot.autoconfig.processor;

import com.loserico.boot.autoconfig.LoserAuthProperties;
import com.loserico.cache.auth.AuthUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * Copyright: (C), 2020-08-14 17:01
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class AuthUtilsInitializePostProcessor implements SmartInitializingSingleton {
	
	@Autowired
	private LoserAuthProperties loserAuthProperties;
	
	@Override
	public void afterSingletonsInstantiated() {
		if (loserAuthProperties.isClearOnStart()) {
			//启动就清理过期token
			AuthUtils.clearExpired();
		} else {
			//启动先加载AuthUtils, 延迟清理过期Token
			Class<AuthUtils> authUtilsClass = AuthUtils.class;
		}
	}
}
