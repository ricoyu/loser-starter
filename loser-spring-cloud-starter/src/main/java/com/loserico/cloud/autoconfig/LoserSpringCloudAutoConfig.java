package com.loserico.cloud.autoconfig;

import com.loserico.cloud.web.client.LoserRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Cloud相关基础设置自动配置
 * <p>
 * Copyright: (C), 2020/5/22 17:39
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Configuration
public class LoserSpringCloudAutoConfig {
	
	@Bean
	@ConditionalOnClass({DiscoveryClient.class, RestTemplate.class})
	public LoserRestTemplate loserRestTemplate() {
		return new LoserRestTemplate();
	}
	
	@Bean
	@LoadBalanced
	@ConditionalOnClass({DiscoveryClient.class, RestTemplate.class})
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
