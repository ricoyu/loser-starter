package com.loserico.boot.security.processor;

import com.loserico.boot.security.props.LoserSecurityProperties;
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
	private LoserSecurityProperties properties;
	
	@Override
	public void afterSingletonsInstantiated() {
		if (properties.isClearOnStart()) {
			//启动就清理过期token
			AuthUtils.clearExpired();
		} else {
			//启动先加载AuthUtils, 延迟清理过期Token
			Class<AuthUtils> authUtilsClass = AuthUtils.class;
		}
	}
}
