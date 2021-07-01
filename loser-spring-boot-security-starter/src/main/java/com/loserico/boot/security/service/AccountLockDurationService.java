package com.loserico.boot.security.service;

/**
 * 登录失败达到允许次数后, 需要锁定一段时间
 * <p>
 * Copyright: (C), 2021-05-20 10:52
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface AccountLockDurationService {
	
	/**
	 * 返回系统配置的登录失败达到限制次数后账户需要锁定多少时间, 单位毫秒
	 * @return
	 */
	public int lockTime();
}
