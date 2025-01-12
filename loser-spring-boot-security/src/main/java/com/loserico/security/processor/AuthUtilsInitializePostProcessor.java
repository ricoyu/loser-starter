package com.loserico.security.processor;

import com.loserico.cache.auth.AuthUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;

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

	@Override
	public void afterSingletonsInstantiated() {
		//启动就清理过期token
		AuthUtils.clearExpired();
	}
}
