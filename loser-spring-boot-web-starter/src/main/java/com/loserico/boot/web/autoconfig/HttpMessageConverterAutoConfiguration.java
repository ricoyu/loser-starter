package com.loserico.boot.web.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.json.ObjectMapperDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
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
		/*
		 * Controller通过一个Bean接收json数据, 对bean中的enum类型属性等的增强, 默认不支持这些类型的绑定
		 */
		decorator.decorate(objectMapper);
		mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
		return mappingJackson2HttpMessageConverter;
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON_UTF8);
		list.add(MediaType.APPLICATION_JSON);
		MappingJackson2HttpMessageConverter messageConverter = mappingJackson2HttpMessageConverter();
		messageConverter.setSupportedMediaTypes(list);
		converters.add(1, messageConverter);
		
		/*
		 * 添加这个是处理在返回String类型的结果时, 多了一个双引号问题
		 */
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.TEXT_PLAIN);
		//构造函数必须传默认编码, 不然返回字符串带中文的湖乱码
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(UTF_8);
		stringHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
		converters.add(0, stringHttpMessageConverter);
	}
}
