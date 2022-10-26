package com.loserico.oauth2.advice;

import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.security.exception.JwtTokenParseException;
import com.loserico.security.exception.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.loserico.common.lang.errors.ErrorTypes.ACCESS_DENIED;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_EXPIRED;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_INVALID;

/**
 * TokenEndpoint报错会自行在其handleException方法中处理, 但是并不打印strck trace, 导致不知道出的啥问题
 * <p>
 * Copyright: (C), 2021-04-01 13:57
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestSecurityExceptionAdvice {
	
	/**
	 * 解析JWT Token失败处理
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(JwtTokenParseException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Object> handleJwtTokenParseException(JwtTokenParseException e) {
		log.error("", e);
		Result result = Results.status(TOKEN_INVALID).build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException e) {
		log.error("", e);
		Result result = Results.status(TOKEN_EXPIRED).build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
		log.error("", e);
		Result result = Results.status(ACCESS_DENIED).build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
}
