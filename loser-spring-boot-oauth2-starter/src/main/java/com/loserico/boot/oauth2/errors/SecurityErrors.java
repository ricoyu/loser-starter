package com.loserico.boot.oauth2.errors;

import com.loserico.common.lang.errors.ErrorType;

/**
 * <p>
 * Copyright: (C), 2021-05-17 15:21
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public enum SecurityErrors implements ErrorType {
	
	/**
	 * 提交的验证码不匹配
	 */
	AUTH_CODE_MISMATCH("4010101", "auth.verify.code.fail", "验证码错误"),
	
	AUTH_CODE_MISS("4010102", "auth.verify.code.required", "请提供验证码或验证码ID"),
	
	AUTH_CODE_EXPIRED("4010103", "auth.verify.code.notexits", "验证码不存在或已过期"),
	
	/**
	 * 超过登录失败次数限制
	 */
	RETRY_COUNT_EXCEED("4010104", "auth.retry.later", "请5分钟之后再登录!");
	
	/**
	 * 错误类型码
	 */
	private String code;
	
	/**
	 * 国际化消息模板
	 */
	private String msgTemplate;
	
	/**
	 * 错误类型描述信息
	 */
	private String msg;
	
	private SecurityErrors(String code, String msgTemplate, String msg) {
		this.code = code;
		this.msgTemplate = msgTemplate;
		this.msg = msg;
	}
	
	@Override
	public String code() {
		return code;
	}
	
	@Override
	public String message() {
		return msg;
	}
	
	@Override
	public String msgTemplate() {
		return msgTemplate;
	}
	
	
}
