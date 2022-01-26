package com.loserico.boot.web.intercepter;

import com.loserico.boot.web.annotation.Idempotent;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.common.spring.utils.ServletUtils;
import com.loserico.web.utils.CORS;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * <p>
 * Copyright: (C), 2022-01-21 16:49
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class IdempotentIntercepter extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Idempotent idempotent = handlerMethod.getMethodAnnotation(Idempotent.class);
		
		//没有打@Idempotent注解的话不处理
		if (idempotent == null) {
			return super.preHandle(request, response, handler);
		}
		
		String token = request.getHeader("Idempotent-Token");
		if (isBlank(token)) {
			log.warn("请求: {} 未携带Idempotent-Token, 默认拦截掉", request.getContextPath());
			Result result = Results.status(ErrorTypes.MISSING_IDEMPOTENT_TOKEN).build();
			response.setContentType(ServletUtils.APPLICATION_JSON_UTF8);
			CORS.builder().allowAll().build(response);
			RestUtils.writeJson(response, result);
			return false;
		}
		
		Long count = JedisUtils.HASH.hdel("idempotent-token", token);
		if (count == null || count == 0) {
			log.warn("检测到重复提交, 请求URI: {}", request.getContextPath());
			Result result = Results.status(ErrorTypes.DUPLICATE_SUBMISSION).build();
			response.setContentType(ServletUtils.APPLICATION_JSON_UTF8);
			CORS.builder().allowAll().build(response);
			RestUtils.writeJson(response, result);
			return false;
		}
		return true;
	}
}
