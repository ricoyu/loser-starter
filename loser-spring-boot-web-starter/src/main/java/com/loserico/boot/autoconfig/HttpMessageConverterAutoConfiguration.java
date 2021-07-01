package com.loserico.boot.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.json.ObjectMapperDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * 配置Spring MVC输出JSON使用自定义的Jackson ObjectMapper
 * <p>
 * Copyright: (C), 2021-04-14 18:33
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class HttpMessageConverterAutoConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapperDecorator decorator = new ObjectMapperDecorator();
		decorator.decorate(objectMapper);
		mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
		return mappingJackson2HttpMessageConverter;
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, mappingJackson2HttpMessageConverter());
	}
}
