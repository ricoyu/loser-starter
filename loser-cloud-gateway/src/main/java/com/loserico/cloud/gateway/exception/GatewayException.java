package com.loserico.cloud.gateway.exception;

import com.loserico.common.lang.vo.ErrorType;
import lombok.Getter;

/**
 * 网关错误 
 * <p>
 * Copyright: Copyright (c) 2020-05-19 9:45
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Getter
public class GatewayException extends RuntimeException {
	
	private String code;
	
	private String msg;
	
	public GatewayException(ErrorType errorType) {
		this.code = errorType.getCode();
		this.msg = errorType.getMsg();
	}
	
}