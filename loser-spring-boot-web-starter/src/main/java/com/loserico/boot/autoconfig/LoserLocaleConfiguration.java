package com.loserico.boot.autoconfig;

import com.loserico.common.spring.filter.LocaleConfigurerFilter;
import com.loserico.common.spring.interceptor.LocaleChangeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * 国际化文件配置默认值为
 * spring:
 *   messages:
 *     basename: messages
 *     encoding: UTF-8
 * <p>
 * messages.properties
 * messages_en_US.properties
 * messages_zh_CN.properties
 * <p>
 * Copyright: (C), 2020-09-02 14:26
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
@EnableConfigurationProperties(LocaleProperties.class)
@ConditionalOnProperty(prefix = "loser.locale", name = "enabled", havingValue = "true")
@Slf4j
public class LoserLocaleConfiguration implements WebMvcConfigurer {
	
	/**
	 * springSecurityFilterChain的Order是-100
	 */
	private static final int ORDER_SPRINGSECURITY_FILTER_CHAIN = -100;
	
	@Autowired
	private LocaleProperties localeProperties;
	
	/**
	 * In order for our application to be able to determine which locale is currently being used,
	 * we need to add a LocaleResolver bean
	 * <p>
	 * bean名字一定要是localeResolver, 不然不能替换默认的AcceptHeaderLocaleResolver
	 *
	 * @return
	 */
	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(Locale.CHINA);
		if (isNotBlank(localeProperties.getCookieName())) {
			resolver.setCookieName(localeProperties.getCookieName());
		}
		return resolver;
	}
	
	/**
	 * Add an interceptor bean that will switch to a new locale based on the value of the lang parameter appended to a request
	 *
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setIgnoreInvalidLocale(localeProperties.isIgnoreInvalidLocale());
		localeChangeInterceptor.setParamName(localeProperties.getParamName());
		
		return localeChangeInterceptor;
	}
	
	/**
	 * To achieve this, our @Configuration class has to implement the WebMvcConfigurer interface and override the addInterceptors() method
	 *
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Bean
	public FilterRegistrationBean<LocaleConfigurerFilter> localeConfigurerFilter() {
		FilterRegistrationBean<LocaleConfigurerFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
		LocaleConfigurerFilter filter = null;
		if (isNotBlank(localeProperties.getParamName())) {
			filter = new LocaleConfigurerFilter(localeProperties.getParamName());
		} else {
			filter = new LocaleConfigurerFilter();
		}
		filterFilterRegistrationBean.setFilter(filter);
		filterFilterRegistrationBean.setOrder(ORDER_SPRINGSECURITY_FILTER_CHAIN * 2);
		return filterFilterRegistrationBean;
	}
	
}
