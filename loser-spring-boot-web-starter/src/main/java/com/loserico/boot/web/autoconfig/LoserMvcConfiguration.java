package com.loserico.boot.web.autoconfig;

import com.loserico.common.spring.aspect.PageResultAspect;
import com.loserico.web.advice.GlobalBindingAdvice;
import com.loserico.web.advice.RestExceptionAdvice;
import com.loserico.web.context.support.CustomConversionServiceFactoryBean;
import com.loserico.web.converter.GenericEnumConverter;
import com.loserico.web.filter.HttpServletRequestRepeatedReadFilter;
import com.loserico.web.resolver.DateArgumentResolver;
import com.loserico.web.resolver.LocalDateArgumentResolver;
import com.loserico.web.resolver.LocalDateTimeArgumentResolver;
import com.loserico.web.resolver.LocalTimeArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

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
@ConditionalOnWebApplication(type = SERVLET)
@EnableConfigurationProperties({LoserFilterProperties.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoserMvcConfiguration implements WebMvcConfigurer {
	
	@Bean
	@ConditionalOnMissingBean(GlobalBindingAdvice.class)
	public GlobalBindingAdvice globalBindingAdvice() {
		return new GlobalBindingAdvice();
	}
	
	@Bean
	public CustomConversionServiceFactoryBean conversionService() {
		CustomConversionServiceFactoryBean conversionServiceFactoryBean = new CustomConversionServiceFactoryBean();
		conversionServiceFactoryBean.getProperties().add("code");
		return conversionServiceFactoryBean;
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
	 * 把方法参数里面的Page对象回填到方法返回值Result里面
	 * @return
	 */
	@Bean
	public PageResultAspect pageResultAspect() {
		return new PageResultAspect();
	}
	
	@Bean
	@Primary
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceRequestEncoding(true);
		filter.setForceResponseEncoding(true);
		return filter;
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "loser.filter", value = "repeated-read")
	public FilterRegistrationBean requestRepeatedReadFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new HttpServletRequestRepeatedReadFilter());
		return registrationBean;
	}
	
	/**
	 * @return
	 */
	@Bean
	@ConditionalOnWebApplication(type = REACTIVE)
	public CorsWebFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// 允许cookies跨域
		config.setAllowCredentials(true);
		// 允许向该服务器提交请求的URI, *表示全部允许, 在SpringMVC中, 如果设成*, 会自动转成当前请求头中的Origin
		config.addAllowedOrigin("*");
		// 允许访问的头信息,*表示全部
		config.addAllowedHeader("*");
		// 预检请求的缓存时间(秒), 即在这个时间段里, 对于相同的跨域请求不会再预检了
		config.setMaxAge(18000L);
		// 允许提交请求的方法, *表示全部允许
		config.addAllowedMethod("*");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
		source.registerCorsConfiguration("/**", config);
		
		return new CorsWebFilter(source);
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
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new DateArgumentResolver());
		resolvers.add(new LocalDateArgumentResolver());
		resolvers.add(new LocalDateTimeArgumentResolver());
		resolvers.add(new LocalTimeArgumentResolver());
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedHeaders("*")
				.allowedMethods("*")
				.allowCredentials(true);
	}
}
