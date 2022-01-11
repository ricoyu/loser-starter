package com.loserico.boot.web.autoconfig;

import com.loserico.boot.web.autoconfig.processor.ObjectMapperBeanPostProcessor;
import com.loserico.boot.web.autoconfig.properties.LoserJacksonProperties;
import com.loserico.boot.web.autoconfig.properties.LoserProperties;
import com.loserico.common.lang.context.ApplicationContextHolder;
import com.loserico.common.spring.annotation.processor.PostInitializeGroupOrderedBeanProcessor;
import com.loserico.common.spring.transaction.TransactionEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

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
@EnableConfigurationProperties({LoserProperties.class, LoserJacksonProperties.class})
@Configuration
@Slf4j
public class LoserAutoConfiguration {
	
	@Autowired
	private LoserProperties loserProperties;
	
	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone(loserProperties.getTimezone()));
	}
	
	@Bean
	@ConditionalOnMissingBean(ApplicationContextHolder.class)
	public ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}
	
	@Bean
	@ConditionalOnMissingBean(TransactionEvents.class)
	@ConditionalOnProperty(value = "loser.asyncTransaction", matchIfMissing = true, havingValue = "true")
	public TransactionEvents transactionEvents() {
		return new TransactionEvents();
	}
	
	@Bean
	@ConditionalOnMissingBean(PostInitializeGroupOrderedBeanProcessor.class)
	@ConditionalOnProperty(value = "loser.enablePostInitialize", matchIfMissing = true, havingValue = "true")
	public PostInitializeGroupOrderedBeanProcessor postInitializeBeanProcessor() {
		PostInitializeGroupOrderedBeanProcessor beanProcessor = new PostInitializeGroupOrderedBeanProcessor();
		beanProcessor.setContextCount(1);
		return beanProcessor;
	}
	
	@Bean
	public ObjectMapperBeanPostProcessor objectMapperPostProcessor() {
		return new ObjectMapperBeanPostProcessor();
	}
}
