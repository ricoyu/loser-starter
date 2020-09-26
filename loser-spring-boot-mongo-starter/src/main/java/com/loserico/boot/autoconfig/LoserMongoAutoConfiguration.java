package com.loserico.boot.autoconfig;

import com.loserico.mongo.dao.MongoDao;
import com.loserico.mongo.support.ExternalScriptsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>
 * Copyright: Copyright (c) 2020-09-23 13:43
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(LoserMongoProperties.class)
@ConditionalOnBean({MongoTemplate.class})
@AutoConfigureAfter(MongoDataAutoConfiguration.class)
@Slf4j
public class LoserMongoAutoConfiguration {
	
	@Autowired
	private LoserMongoProperties loserMongoProperties;
	
	@Bean
	public ExternalScriptsHelper externalScriptsHelper() {
		ExternalScriptsHelper helper = new ExternalScriptsHelper();
		helper.setLocation(loserMongoProperties.getLocation());
		helper.setFileSuffix(loserMongoProperties.getSuffix());
		return helper;
	}
	
	@Bean
	public MongoDao mongoDao() {
		return new MongoDao();
	}
}
