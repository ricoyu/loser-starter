package com.loserico.cloud.feign.aspect;

import com.loserico.cache.JedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Copyright: (C), 2023-03-06 10:06
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Aspect
public class IdempotentAspect {
	
	@Pointcut("@annotation(com.loserico.cloud.feign.annotation.Idempotent)")
	public void pointcut() {
		
	}
	
	@Around("pointcut()")
	public void around(ProceedingJoinPoint joinPoint) {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName().toLowerCase();
		String methodName = joinPoint.getSignature().getName().toLowerCase();
		int paramsCount = joinPoint.getArgs().length;
		String key = className + ":" +methodName +":" +paramsCount;
		
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String idempotent = request.getHeader("Idempotent");
		//调用目标方法
		if (StringUtils.isBlank(idempotent)) {
			try {
				joinPoint.proceed();
			} catch (Throwable e) {
				log.error("AOP执行调用方法出错", e);
				throw new RuntimeException(e);
			}
		} else {
			Long count = JedisUtils.HyperLogLog.pfadd(key, idempotent);
			//往HyperLogLog里面添加成功表示这是第一次调用, 添加失败表示是重复提交, 就不调用目标方法了
			if (count == 1L) {
				try {
					joinPoint.proceed();
				} catch (Throwable e) {
					log.error("AOP执行调用方法出错", e);
					throw new RuntimeException(e);
				}
			} else {
				log.warn("发现重复提交, 拒绝执行目标方法!");
			}
		}
	}
}
