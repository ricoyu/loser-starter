package com.loserico.boot.security.autoconfig;

import com.loserico.boot.security.props.LoserXSSProperties;
import com.loserico.security.filter.XSSFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * <p>
 * Copyright: (C), 2021-02-23 10:14
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties({LoserXSSProperties.class})
@ConditionalOnProperty(prefix = "loser.xss", name = "enabled", matchIfMissing = false)
public class LoserXSSAutoConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.headers(headers -> {
			headers.addHeaderWriter(new XXssProtectionHeaderWriter())
					.addHeaderWriter(new ContentSecurityPolicyHeaderWriter("script-src 'self'"));
		});
		return http.build();
	}

	@Bean
	public FilterRegistrationBean<XSSFilter> xssFilter() {
		FilterRegistrationBean<XSSFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(new XSSFilter());
		filter.setOrder(Integer.MIN_VALUE);
		return filter;
	}
}
