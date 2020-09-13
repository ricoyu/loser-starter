package com.loserico.boot.autoconfig;

import com.loserico.boot.web.websocket.filter.WebSocketFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2020-09-11 17:14
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "loser.websocket", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({LoserWebsocketProperties.class})
public class LoserWebsocketAutoConfiguration {
	
	@Autowired
	private LoserWebsocketProperties loserWebsocketProperties;
	
	@Bean
	public WebSocketFilter webSocketFilter() {
		return new WebSocketFilter();
	}
	/*@Bean
	public FilterRegistrationBean<WebSocketFilter> webSocketFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebSocketFilter());
		filterRegistrationBean.setUrlPatterns(asList(loserWebsocketProperties.getPathPrefix()));
		return filterRegistrationBean;
	}*/
}
