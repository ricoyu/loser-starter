package com.loserico.cloud.feign.aspect;

import com.loserico.cache.JedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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
@Aspect
public class IdempotentAspect {

	private static final Logger log = LoggerFactory.getLogger(IdempotentAspect.class);
	
	@Pointcut("@annotation(com.loserico.cloud.feign.annotation.Idempotent)")
	public void pointcut() {
		
	}
	
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName().toLowerCase();
		String methodName = joinPoint.getSignature().getName().toLowerCase();
		int paramsCount = joinPoint.getArgs().length;
		String key = "Idempotent:"+className + ":" +methodName +":" +paramsCount;
		
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String idempotent = request.getHeader("Idempotent");
		//调用目标方法
		if (StringUtils.isBlank(idempotent)) {
				return joinPoint.proceed();
		} else {
			Long count = JedisUtils.SET.sadd(key, idempotent);
			//往Set里面添加成功表示这是第一次调用, 添加失败表示是重复提交, 就不调用目标方法了
			if (count == 1L) {
				try {
					return joinPoint.proceed();
				} catch (Throwable e) {
					log.error("AOP执行调用方法出错", e);
					//业务方法执行报错了把往Redis里面塞的idempotent删掉
					count = JedisUtils.SET.srem(key, idempotent);
					if (count == 1L) {
						log.info("业务方法执行报错了, 把往Redis里面塞的idempotent删掉");
					}
					throw e;
				}
			} else {
				log.warn("发现重复提交, 拒绝执行目标方法!");
			}
		}

		return null;
	}

}
