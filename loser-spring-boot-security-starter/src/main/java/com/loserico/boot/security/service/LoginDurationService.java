package com.loserico.boot.security.service;

/**
 * <p>
 * Copyright: (C), 2021-06-29 11:08
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface LoginDurationService {
	
	/**
	 * 返回登录后多久无操作token过期, 单位分钟
	 * @param username
	 * @return long
	 */
	public long expiresInMinutes(String username);
}
