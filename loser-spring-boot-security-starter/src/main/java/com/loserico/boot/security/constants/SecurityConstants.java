package com.loserico.boot.security.constants;

/**
 * <p>
 * Copyright: (C), 2021-05-17 13:50
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public final class SecurityConstants {
	
	/**
	 * 图片验证码key前缀
	 */
	public static final String VERIFY_CODE_PREFIX = "verifycode:";
	
	/**
	 * 验证码ID参数名字
	 */
	public static final String VERIFY_CODE_ID = "codeId";
	
	/**
	 * 验证码参数名字
	 */
	public static final String VERIFY_CODE = "code";
	
	public static final String ACCESS_TOKEN = "accessToken";
	
	/**
	 * 获取图片验证码URL
	 */
	public static final String PIC_CODE_URL = "/pic-code";
	
	/**
	 * 用于Redis中记录重试次数
	 */
	public static final String RETRY_COUNT_KEY_PREFIX = "retryCount:";
	
	/**
	 * Authorization请求头名字
	 */
	public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";
	
	/**
	 * 基于token认证的系统, 需要以 Bearer token 形式设置Authorization请求头
	 */
	public static final String BEARER_TOKEN_PREFIX = "Bearer ";
	
}
