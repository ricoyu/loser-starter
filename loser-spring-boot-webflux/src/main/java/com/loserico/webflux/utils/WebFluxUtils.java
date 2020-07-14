package com.loserico.webflux.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/**
 * <p>
 * Copyright: (C), 2020/5/1 10:40
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public final class WebFluxUtils {
	
	/**
	 * 获取单个请求头
	 *
	 * @param exchange
	 * @param headerName
	 * @return String
	 */
	public static String getHeader(ServerWebExchange exchange, String headerName) {
		Assert.notNull(exchange, "exchange cannot be null");
		Assert.notNull(headerName, "headerName cannot be null");
		return exchange.getRequest().getHeaders().getFirst(headerName);
	}
	
	/**
	 * 添加请求头
	 *
	 * @param exchange
	 * @param headerName
	 * @param headerValue
	 * @return ServerWebExchange
	 */
	public static ServerWebExchange addHeader(ServerWebExchange exchange, String headerName, String headerValue) {
		//向headers中放文件, 记得build
		ServerHttpRequest request = exchange.getRequest().mutate().header(headerName, headerValue).build();
		//将现在的request 变成 change对象
		return exchange.mutate().request(request).build();
	}
	
	/**
	 * 添加attribute
	 *
	 * @param exchange
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void putAttribute(ServerWebExchange exchange, String attributeName, Object attributeValue) {
		exchange.getAttributes().put(attributeName, attributeValue);
	}
	
	/**
	 * 返回请求的URI路径
	 *
	 * @param exchange
	 * @return String
	 */
	public static String requestPath(ServerWebExchange exchange) {
		return exchange.getRequest().getURI().getPath();
	}
}
