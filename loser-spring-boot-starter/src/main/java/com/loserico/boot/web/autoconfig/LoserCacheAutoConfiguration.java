package com.loserico.boot.web.autoconfig;

import com.loserico.boot.annotation.processor.RedisListenerProcessor;
import com.loserico.boot.web.autoconfig.properties.LoserCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2020-09-10 14:30
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "loser.cache", value = "enabled", havingValue = "true")
@EnableConfigurationProperties({LoserCacheProperties.class})
public class LoserCacheAutoConfiguration {
	
	@Bean
	public RedisListenerProcessor redisListenerProcessor() {
		return new RedisListenerProcessor();
	}
}
