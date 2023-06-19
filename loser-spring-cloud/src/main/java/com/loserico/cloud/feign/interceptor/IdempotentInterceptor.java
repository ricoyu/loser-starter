package com.loserico.cloud.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 实现接口幂等性; 这个是配置在调用方的, 在调用其他服务的时候往Idempotent请求头里面塞UUID, 只有POST PUT方法会拦截
 * 接口被调用放的Controller方法要加@Idempotent注解, 配置好redis.properties, 我会通过AOP拦截加了@Idempotent注解的方法调用, 从Request里面拿到
 * Idempotent请求头, 然后往Redis的HyperLogLog里面塞, 塞成功过过了就表示没有重复调用, 塞失败了就不允许重复调用
 * <p>
 * Copyright: (C), 2023-03-05 17:07
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class IdempotentInterceptor implements RequestInterceptor {
	
	/**
	 * 只有PUT, POST方法需要做幂等性
	 */
	//private static String[] INTERCEPT_METHODS = new String[]{"POST", "PUT"};
	
	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		/*
		 * 客户端调用服务A, 服务A通过feign调用服务B
		 * 注意这里拿到的是服务A的Controller方法的Method, 不是feign调用的服务B的method, 所以不能只拦截POST, PUT方法, 因为有可能
		 * 客户端调服务A走的GET, 服务A调服务B走的POST(需要做接口幂等性), 这种情况如果加了调用方法限制, 这个幂等性控制就不生效了
		 * 所以干脆去掉, 反正多个请求头也没事
		 */
		/*String method = request.getMethod();
		boolean shouldIntercept = false;
		for (int i = 0; i < INTERCEPT_METHODS.length; i++) {
			if (method.equalsIgnoreCase(INTERCEPT_METHODS[i])) {
				shouldIntercept = true;
			}
		}*/
		
		//设置Idempotent请求头
		//if (!template.headers().containsKey("Idempotent") && shouldIntercept) {
		if (!template.headers().containsKey("Idempotent")) {
			String Idempotent = UUID.randomUUID().toString().replaceAll("-", "");
			log.info("添加Idempotent请求头: {}", Idempotent);
			template.header("Idempotent", Idempotent);
		}
	}
}
