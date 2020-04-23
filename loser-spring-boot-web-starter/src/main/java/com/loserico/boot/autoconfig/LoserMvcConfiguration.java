package com.loserico.boot.autoconfig;

import com.loserico.web.advice.GlobalBindingAdvice;
import com.loserico.web.advice.RestExceptionAdvice;
import com.loserico.web.converter.GenericEnumConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

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
public class LoserMvcConfiguration implements WebMvcConfigurer {
	
	@Bean
	@ConditionalOnMissingBean(GlobalBindingAdvice.class)
	public GlobalBindingAdvice globalBindingAdvice() {
		return new GlobalBindingAdvice();
	}
	
	/**
	 * 全局异常处理
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(RestExceptionAdvice.class)
	public RestExceptionAdvice restExceptionAdvice() {
		return new RestExceptionAdvice();
	}
	
	/**
	 * 支持Enum类型参数绑定，可以按名字，也可以按制定的属性
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		Set<String> properties = new HashSet<>();
		properties.add("code");
		properties.add("desc");
		registry.addConverter(new GenericEnumConverter(properties));
		WebMvcConfigurer.super.addFormatters(registry);
	}
}
