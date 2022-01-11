package com.loserico.boot.web.autoconfig;

import com.loserico.web.listener.ThreadLocalCleanupListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.ServletRequestListener;

/**
 * <p>
 * Copyright: (C), 2020/4/14 16:22
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@Slf4j
public class LoserThreadAutoConfiguration {
	
	/**
	 * 在Http请求进来和结束时清理ThreadLocal
	 * @return
	 */
	@Bean
	@ConditionalOnClass(ServletRequestListener.class)
	@Primary
	public ServletListenerRegistrationBean<ServletRequestListener> listenerRegistrationBean() {
		ServletListenerRegistrationBean<ServletRequestListener> bean = new ServletListenerRegistrationBean<>();
		bean.setListener(new ThreadLocalCleanupListener());
		return bean;
	}
}
