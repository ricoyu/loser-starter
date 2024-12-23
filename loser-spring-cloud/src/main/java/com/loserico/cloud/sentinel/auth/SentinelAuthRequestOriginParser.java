package com.loserico.cloud.sentinel.auth;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 授权规则限流需要在feign的被调用方添加这个bean, feign的调用方添加一个拦截器
 * <p/>
 * Copyright: Copyright (c) 2024-12-22 20:56
 * <p/>
 * Company: Sexy Uncle Inc.
 * <p/>

 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
public class SentinelAuthRequestOriginParser implements RequestOriginParser {
	@Override
	public String parseOrigin(HttpServletRequest request) {
		return request.getParameter("serviceName");
	}
}