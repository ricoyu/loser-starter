package com.loserico.boot.security.service;

/**
 * 负责获取用户名密码登录失败重试次数, 如果不提供, 默认允许5次失败
 * <p>
 * Copyright: (C), 2021-05-20 10:49
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface RetryCountService {
	
	/**
	 * 重试次数限制可以是全局统一配置的, 也可能不同用户有不同的重试次数限制, 所以参数传入username, 按需取用
	 * @param username
	 * @return int 允许的重试次数
	 */
	public int maxRetryCount(String username);
}
