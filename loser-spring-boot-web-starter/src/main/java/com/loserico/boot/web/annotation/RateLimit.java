package com.loserico.boot.web.annotation;

import com.loserico.boot.web.handler.RateLimitHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.loserico.boot.web.annotation.RateLimit.Algorithm.SLIDING_WINDOW;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 应用限流, 标注在Controller方法上, 能自动识别API, 做到API级别限流控制
 * <p>
 * 表示expire秒内最多有count次请求，否则返回 429 Too Many Requests
 * <p>
 * Copyright: Copyright (c) 2018-07-20 10:19
 * <p>
 * Company: DataSense
 * <p>
 *
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RateLimit {
	
	/**
	 * 限流算法, 默认滑动时间窗口
	 *
	 * @return
	 */
	Algorithm algorithm() default SLIDING_WINDOW;
	
	/**
	 * 给定时间窗口内访问次数不超过limit次, 默认100
	 *
	 * @return
	 */
	int limit() default 100;
	
	/**
	 * 时间窗口, 单位是毫秒, 默认1000
	 *
	 * @return
	 */
	int window() default 1000;
	
	/**
	 * 触发限流规则后, 默认会返回错误码
	 * <pre> {@code
	 * TOO_MANY_REQUESTS("4290000", "template.too.many.requests", "Too Many Requests")
	 * }</pre>
	 * 但如果想自行处理, 可以提供RateLimitHandler, 需要有默认构造函数
	 * @return
	 */
	Class<? extends RateLimitHandler> handler();
	
	public static enum Algorithm {
		
		/**
		 * 滑动时间窗口算法
		 */
		SLIDING_WINDOW;
	}
}
