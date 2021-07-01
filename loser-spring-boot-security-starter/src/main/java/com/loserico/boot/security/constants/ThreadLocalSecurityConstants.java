package com.loserico.boot.security.constants;

/**
 * <p>
 * Copyright: (C), 2021-05-19 11:27
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public final class ThreadLocalSecurityConstants {
	
	/**
	 * TokenDecryptProcessingFilter会把客户端传递的token解析成AuthRequest对象放到ThreadLocal里面, 这个常量定义了key值
	 */
	public static final String AUTH_REQUEST = "authRequest";
	
	public static final String ACCESS_TOKEN = "accessToken";
	
	public static final String USERNAME = "username";
	
	public static final String USER_ID = "userId";
	
	/**
	 * 登录成功后放到ThreadContext里面的额外的一些信息, 比如用户名, 全名, 邮箱地址等
	 */
	public static final String LOGIN_INFO = "loginInfo";
	
}
