package com.loserico.boot.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.common.lang.context.ApplicationContextHolder;
import com.loserico.common.spring.annotation.processor.PostInitializeGroupOrderedBeanProcessor;
import com.loserico.common.spring.transaction.TransactionEvents;
import com.loserico.json.jackson.ObjectMapperFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.List;
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
@EnableConfigurationProperties({LoserProperties.class, JacksonProperties.class})
@Configuration
@Slf4j
public class LoserAutoConfiguration {
	
	@Autowired
	private LoserProperties loserProperties;
	
	@Autowired
	private JacksonProperties jacksonProperties;
	
	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone(loserProperties.getTimezone()));
	}
	
	@Bean
	@Primary
	@ConditionalOnProperty(value = "loser.jackson.enabled", havingValue = "true")
	public ObjectMapper objectMapper() {
		ObjectMapperFactoryBean objectMapperFactoryBean = new ObjectMapperFactoryBean();
		List<String> enumProperties = jacksonProperties.getEnumProperties();
		for (String enumProperty : enumProperties) {
			objectMapperFactoryBean.getEnumProperties().add(enumProperty);
		}
		objectMapperFactoryBean.setEpochBased(jacksonProperties.isEpochBased());
		try {
			return objectMapperFactoryBean.getObject();
		} catch (Exception e) {
			log.error("msg", e);
		}
		return null;
	}
	
	@Bean
	@ConditionalOnMissingBean(ApplicationContextHolder.class)
	public ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}
	
	@Bean
	@ConditionalOnMissingBean(TransactionEvents.class)
	@ConditionalOnProperty(value = "loser.asyncTransaction", havingValue = "true")
	public TransactionEvents transactionEvents() {
		return new TransactionEvents();
	}
	
	@Bean
	@ConditionalOnMissingBean(PostInitializeGroupOrderedBeanProcessor.class)
	@ConditionalOnProperty(value = "loser.enablePostInitialize", havingValue = "true")
	public PostInitializeGroupOrderedBeanProcessor postInitializeBeanProcessor() {
		PostInitializeGroupOrderedBeanProcessor beanProcessor = new PostInitializeGroupOrderedBeanProcessor();
		beanProcessor.setContextCount(1);
		return beanProcessor;
	}
}
