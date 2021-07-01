package com.loserico.boot.security.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 在Controller方法上标注
 * 
 * 表示expire秒内最多有count次请求，否则返回 429 Too Many Requests
 * <p>
 * Copyright: Copyright (c) 2018-07-20 10:19
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RateLimit {

	/**
	 * 给定秒内访问次数不超过count次
	 * 
	 * @return
	 */
	int count() default 1000;

	/**
	 * 秒数限定
	 * @return
	 */
	int expire() default 1;
}
