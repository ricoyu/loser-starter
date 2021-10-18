package com.loserico.boot.security.processor;

import org.springframework.security.core.AuthenticationException;

/**
 * 每次登录失败后的处理器
 * <p>
 * Copyright: (C), 2021-10-13 9:58
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface LoginFailPertimeProcessor {
	
	/**
	 * 登录失败处理
	 * @param username 用户名, 可能是null
	 * @param e 具体登录失败的异常信息
	 */
	public void onLoginFail(String username, AuthenticationException e);
}
