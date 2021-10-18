package com.loserico.boot.security.processor;

import java.util.Map;

/**
 * <p>
 * Copyright: (C), 2021-09-02 14:30
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface LoginInfoProvider {
	
	/**
	 * 根据用户名, 提供这个用户额外的一些登录信息, 比如用户的角色?
	 * @param username
	 * @return Map<String, Object>
	 */
	public Map<String, Object> loginInfo(String username);
}
