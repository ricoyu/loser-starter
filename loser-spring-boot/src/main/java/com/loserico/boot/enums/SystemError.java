package com.loserico.boot.enums;

import com.loserico.common.lang.vo.ErrorType;
import lombok.Getter;

@Getter
public enum SystemError implements ErrorType {
	
	SYSTEM_ERROR("-1", "系统异常"),
	
	SYSTEM_BUSY("000001", "系统繁忙,请稍候再试"),
	
	GATEWAY_NOT_FOUND_SERVICE("010404", "服务未找到"),
	
	GATEWAY_ERROR("010500", "网关异常"),
	
	FORBIDDEN("403", "无权访问"),
	
	TOKEN_TIMEOUT("403-1", "token过期"),
	
	INVALID_TOKEN("401-2", "无效token"),
	
	UNAUTHORIZED_HEADER_IS_EMPTY("401-1", "无权访问,请求头为空"),
	
	GATEWAY_CONNECT_TIME_OUT("000504", "网关超时"),
	
	BAD_GATEWAY("000502", "错误网关"),
	
	SUCCESS("888888", "处理成功"),
	
	NOT_LOGIN("-1", "没有登陆"),
	
	LOGIN_FAIL("000004", "登陆失败,用户名密码错误"),
	
	LOGIN_SUCCESS("000005", "登陆成功"),
	
	LOGOUT_SUCCESS("000006", "退出成功"),
	
	REFRESH_TOKEN_EXPIRE("000007", "刷新令牌过期"),
	
	GET_TOKEN_KEY_ERROR("000008", "认证服务器获取Token异常"),
	
	GEN_PUBLIC_KEY_ERROR("000009", "生成公钥异常");
	
	/**
	 * 错误类型码
	 */
	private String code;
	
	/**
	 * 错误类型描述信息
	 */
	private String msg;
	
	private SystemError(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}