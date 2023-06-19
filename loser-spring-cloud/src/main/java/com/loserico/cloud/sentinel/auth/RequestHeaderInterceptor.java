package com.loserico.cloud.sentinel.auth;

import com.loserico.common.lang.resource.YamlReader;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 支持Sentinel授权规则流控, 需要在调用方配置这个Bean
 * <p>
 * Copyright: Copyright (c) 2023-03-22 11:11
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
public class RequestHeaderInterceptor implements RequestInterceptor {
	
	private YamlReader yamlReader = new YamlReader("bootstrap");
	private YamlReader yamlReader2 = new YamlReader("application");
	
	@Value("${spring.application.name}")
	private String serviceName;
	
	@Override
	public void apply(RequestTemplate template) {
		String applicationName = yamlReader.getString("spring.application.name");
		if (isBlank(applicationName)) {
			applicationName = yamlReader2.getString("spring.application.name");
		}
		template.query("serviceName", applicationName); //添加调用方的serviceName
	}

}
