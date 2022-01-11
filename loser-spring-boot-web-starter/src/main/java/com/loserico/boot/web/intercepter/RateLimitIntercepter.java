package com.loserico.boot.web.intercepter;

import com.loserico.boot.web.annotation.RateLimit;
import com.loserico.boot.web.annotation.RateLimit.Algorithm;
import com.loserico.boot.web.handler.RateLimitHandler;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.json.jackson.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.loserico.boot.web.annotation.RateLimit.Algorithm.SLIDING_WINDOW;
import static java.lang.String.join;

/**
 * 实现应用限流
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
public class RateLimitIntercepter extends HandlerInterceptorAdapter {
	
	private ConcurrentMap<Class<? extends RateLimitHandler>, RateLimitHandler> handlers = new ConcurrentHashMap<>();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
		
		//没有打@RateLimit注解的话不处理
		if (rateLimit == null) {
			return super.preHandle(request, response, handler);
		}
		
		//下面是为了拼接出完整的API Path
		RequestMapping controllerMapping = handlerMethod.getBean().getClass().getAnnotation(RequestMapping.class);
		String[] controllerPaths = controllerMapping.value();
		for (int i = 0; i < controllerPaths.length; i++) {
			String controllerPath = controllerPaths[i];
			controllerPaths[i] = clean(controllerPath).replaceAll("/", ":");
		}
		RequestMapping methodMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
		String[] methodPaths = methodMapping.value();
		for (int i = 0; i < methodPaths.length; i++) {
			String methodPath = methodPaths[i];
			methodPaths[i] = clean(methodPath).replaceAll("/", ":");
		}
		
		String controllerPath = join(":", controllerPaths);
		String methodPath = join(":", methodPaths);
		String key = join(":", "rate_limit", controllerPath, methodPath);
		
		int timeWindow = rateLimit.window();
		int limit = rateLimit.limit();
		Algorithm algorithm = rateLimit.algorithm();
		//目前就支持滑动时间窗口算法
		if (algorithm == SLIDING_WINDOW) {
			boolean requestSucceed = JedisUtils.AFFLUENT.rateLimit(key, timeWindow, limit);
			if (!requestSucceed) {
				Class<? extends RateLimitHandler> handlerClass = rateLimit.handler();
				//如果指定了handler, 那么由开发者自行处理限流事件
				if (handlerClass != null) {
					RateLimitHandler rateLimitHandler = handlers.computeIfAbsent(handlerClass, (clazz) -> {
						try {
							return clazz.newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeException("实例化" + handlerClass.getName() +" 失败!");
						}
					});
					rateLimitHandler.handler(response, 
							timeWindow, 
							limit, 
							join("/", "rate_limit", controllerPath, methodPath));
					return false;
				}
				
				Result result = Results.status(ErrorTypes.TOO_MANY_REQUESTS).build();
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Methods", "*");
				response.setHeader("Access-Control-Allow-Headers ", "*");
				JacksonUtils.writeValue(response.getWriter(), result);
				return false;
			}
		}
		
		return super.preHandle(request, response, handler);
	}
	
	/**
	 * 清理头尾的/
	 *
	 * @param str
	 * @return String
	 */
	private String clean(String str) {
		str = StringUtils.removeStart(str, "/");
		return StringUtils.removeEnd(str, "/");
	}
	
}
