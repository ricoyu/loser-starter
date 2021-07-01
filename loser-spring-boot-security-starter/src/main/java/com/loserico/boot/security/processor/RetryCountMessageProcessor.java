package com.loserico.boot.security.processor;

/**
 * 登录失败超过指定次数后返回的错误消息
 * <p>
 * Copyright: (C), 2021-05-25 10:07
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface RetryCountMessageProcessor {
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public String retryCountExceeded(String username);
}
