package com.loserico.cloud.autoconfig;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.loserico.cloud.feign.aspect.IdempotentAspect;
import com.loserico.cloud.feign.interceptor.IdempotentInterceptor;
import com.loserico.cloud.sentinel.RestBlockExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * 网关异常处理自动配置
 * <p>
 * Copyright: Copyright (c) 2020-05-02 10:45
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class LoserSpringCloudAutoConfiguration {
	
	/**
	 * 实现接口幂等性的Feign拦截器, 在发送请求调用其他微服务时, 往请求头里塞Idempotent头; 
	 * 接口超时重试的时候, Feign会携带之前塞的Idempotent请求头, 在目标方法上加@Idempotent注解, 
	 * 通过AOP拦截, 然后从Request中拿Idempotent头, 如果拿到就往Redis的HyperLogLog里面塞, 塞成功了
	 * 就允许调用, 否则认为是重复提交, 不执行目标方法
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(name = "loser.idemtotent.enabled", havingValue = "true", matchIfMissing = false)
	public IdempotentInterceptor idempotentInterceptor() {
		return new IdempotentInterceptor();
	}
	
	/**
	 * 在需要做幂等性控制的接口方法上加@Idempotent注解, 这个切面提供做这个注解的支持
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(name = "loser.idemtotent.enabled", havingValue = "true", matchIfMissing = false)
	public IdempotentAspect idempotentAspect() {
		return new IdempotentAspect();
	}
	
	@Bean
	@ConditionalOnProperty(name = "loser.sentinel.enabled", havingValue = "true", matchIfMissing = false)
	public RestBlockExceptionHandler restBlockExceptionHandler() {
		return new RestBlockExceptionHandler();
	}
	
	@Bean
	@ConditionalOnProperty(name = "loser.sentinel.enabled", havingValue = "true", matchIfMissing = false)
	@ConditionalOnMissingBean(SentinelResourceAspect.class)
	public SentinelResourceAspect sentinelResourceAspect() {
		return new SentinelResourceAspect();
	}
}
