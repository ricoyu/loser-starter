package com.loserico.boot.security.processor;

/**
 * 当用户不允许在多处登录, 且当前已在别处登录时应该提示给用户的消息
 * <p>
 * Copyright: (C), 2021-05-25 11:11
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface SingleLoginMessageProcessor {
	
	/**
	 * 当用户不允许在多处登录, 且当前已在别处登录时应该提示给用户的消息
	 * @param username
	 * @return String
	 */
	public String onSingletonLogin(String username);
}
