package com.loserico.boot.security.policy;

/**
 * 登录策略 - 是否允许多地同时登录? 是否踢掉上一个登录? 已在别处登录就不能再次登录?
 * <p>
 * Copyright: (C), 2021-05-25 10:32
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface LoginPolicyService {
	
	/**
	 * 返回登录策略
	 * @param username
	 * @return Policy
	 */
	public default Policy loginPolicy(String username) {
		return Policy.MULTIPLE;
	}
	
	public static enum Policy {
		
		/**
		 * 禁止同一个用户在多地登录, 已在别处登录就不允许再次登录了
		 */
		SINGLE,
		
		/**
		 * 禁止同一个用户在多地登录, 已在别处登录就踢掉后重新登录
		 */
		KICK_OFF,
		
		/**
		 * 可以在多出登录
		 */
		MULTIPLE;
	}
}
