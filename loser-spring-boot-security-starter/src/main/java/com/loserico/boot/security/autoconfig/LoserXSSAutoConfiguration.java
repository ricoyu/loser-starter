package com.loserico.boot.security.autoconfig;

import com.loserico.boot.security.props.LoserXSSProperties;
import com.loserico.security.filter.XSSFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
public class LoserXSSAutoConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers()
				.xssProtection()
				.and()
				.contentSecurityPolicy("script-src 'self'");
	}
	
	@Bean
	public FilterRegistrationBean<XSSFilter> xssFilter() {
		FilterRegistrationBean<XSSFilter> filter = new FilterRegistrationBean<>();
		filter.setOrder(Integer.MIN_VALUE);
		return filter;
	}
}
