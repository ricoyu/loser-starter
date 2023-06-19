package com.loserico.cloud.sentinel.auth;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于支持Sentinel授权规章制度流控, 需要在被调用方配置这个Bean 
 * <p>
 * Copyright: Copyright (c) 2023-03-22 11:12
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
public class MyRequestOriginParser implements RequestOriginParser {
	@Override
	public String parseOrigin(HttpServletRequest request) {
		return request.getParameter("serviceName");
	}
}
