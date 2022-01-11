package com.loserico.boot.web.handler;

import javax.servlet.http.HttpServletResponse;

/**
 * 触发限流规则后, 默认会返回错误码
 * <pre> {@code
 * TOO_MANY_REQUESTS("4290000", "template.too.many.requests", "Too Many Requests")
 * }</pre>
 * 但如果想自行处理, 可以提供RateLimitHandler, 需要有默认构造函数
 * <p>
 * Copyright: (C), 2022-01-09 20:29
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface RateLimitHandler {
	
	/**
	 * 
	 * @param response    HttpServletResponse
	 * @param timeWindow  限流时间窗口, 单位毫秒
	 * @param limit       具体限制的流量值, 即timeWindow内允许通过的请求数
	 * @param path        被限流的API地址
	 */
	public void handler(HttpServletResponse response, long timeWindow, long limit, String path);
}
