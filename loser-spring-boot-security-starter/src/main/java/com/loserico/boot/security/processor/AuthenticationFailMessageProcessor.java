package com.loserico.boot.security.processor;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户名密码登录失败错误消息处理器
 * <p>
 * Copyright: (C), 2021-05-20 10:32
 * <p>b 
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface AuthenticationFailMessageProcessor {
	
	/**
	 * 不用的AuthenticationException由不同的MessageProcessor来处理
	 * @param e
	 * @return
	 */
	public boolean supports(AuthenticationException e);
	
	/**
	 * 返回自定义的错误码, 错误消息
	 * @param e
	 * @return String[]
	 */
	public String[] message(AuthenticationException e);
}
