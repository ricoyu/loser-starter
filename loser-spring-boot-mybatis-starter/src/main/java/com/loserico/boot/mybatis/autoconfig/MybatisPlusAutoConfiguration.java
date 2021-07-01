package com.loserico.boot.mybatis.autoconfig;

import com.loserico.boot.mybatis.handler.LoserMetaObjectHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus自动化配置类
 * <p>
 * Copyright: (C), 2021-04-07 10:35
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(MybatisPlusMetadataProperties.class)
public class MybatisPlusAutoConfiguration {
	
	private MybatisPlusMetadataProperties mybatisPlusMetadataProperties;
	
	/**
	 * 配置Mybatis-Plus自动注入creator, createTime, modifier, modifyTime这几个字段的值
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(LoserMetaObjectHandler.class)
	@ConditionalOnProperty(value = "loser.mybatis-plus.metadata.auto-inject", matchIfMissing = false, havingValue = "true")
	public LoserMetaObjectHandler metaObjectHandler() {
		return new LoserMetaObjectHandler();
	}
}
