package com.loserico.boot.web.autoconfig.processor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.loserico.boot.web.autoconfig.properties.JacksonDeserializer;
import com.loserico.boot.web.autoconfig.properties.JacksonSerializer;
import com.loserico.boot.web.autoconfig.properties.LoserJacksonProperties;
import com.loserico.common.lang.utils.CollectionUtils;
import com.loserico.common.lang.utils.ReflectionUtils;
import com.loserico.json.ObjectMapperDecorator;
import com.loserico.json.jackson.JacksonUtils;
import com.loserico.json.jackson.escapes.CustomCharacterEscapes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 在Spring容器启动的最后, 所以bean都ready后初始化一下JacksonUtils
 * <p>
 * Copyright: (C), 2020/4/30 11:05
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class ObjectMapperBeanPostProcessor implements SmartInitializingSingleton {
	
	@Autowired
	private LoserJacksonProperties loserJacksonProperties;
	
	/**
	 * 从容器中取objectMapper
	 * 这个objectMapper有可能经过Spring处理过的, 比如加入了一些MixIn
	 * 所以JacksonUtils要拿这个现成的, 否则有些序列化/反序列化可能不支持
	 */
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void afterSingletonsInstantiated() {
		log.info("初始化JacksonUtils......");
		Class<JacksonUtils> jacksonUtilsClass = JacksonUtils.class;
		if (JacksonUtils.objectMapper() != objectMapper) {
			log.info(">>>>>>JacksonUtils already initialized? then do it again<<<<<<");
			ObjectMapperDecorator decorator = new ObjectMapperDecorator();
			decorator.decorate(objectMapper);
			ReflectionUtils.setField(jacksonUtilsClass, "objectMapper", objectMapper);
		}
		
		// 配置输出JSON字段名不用双引号括起来
		if (!loserJacksonProperties.isFieldNameQuote()) {
			objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
			objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			
			//系列化字符串时候, Jackson会把双引号转义, 如\", 这里配置不需要转义
			objectMapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
		}
		
		/*
		 * 配置自定义反序列化器
		 */
		if (CollectionUtils.isNotEmpty(loserJacksonProperties.getDeserializers())) {
			SimpleModule customModule = new SimpleModule();
			for (JacksonDeserializer deserializer : loserJacksonProperties.getDeserializers()) {
				if (deserializer.getType() == null || deserializer.getDeserializer() == null) {
					continue;
				}
				
				try {
					customModule.addDeserializer(deserializer.getType(), (JsonDeserializer) deserializer.getDeserializer().newInstance());
					objectMapper.registerModule(customModule);
				} catch (InstantiationException e) {
					log.error("实例化 {} 失败!", deserializer.getDeserializer(), e);
				} catch (IllegalAccessException e) {
					log.error("实例化 {} 失败! 没有public构造函数?", deserializer.getDeserializer(), e);
				}
			}
		}
		
		/*
		 * 配置自定义序列化器
		 */		
		if (CollectionUtils.isNotEmpty(loserJacksonProperties.getSerializers())) {
			SimpleModule customModule = new SimpleModule();
			for (JacksonSerializer serializer : loserJacksonProperties.getSerializers()) {
				if (serializer.getType() == null || serializer.getSerializer() == null) {
					continue;
				}
				
				try {
					customModule.addSerializer(serializer.getType(), (JsonSerializer) serializer.getSerializer().newInstance());
					objectMapper.registerModule(customModule);
				} catch (InstantiationException e) {
					log.error("实例化 {} 失败!", serializer.getSerializer(), e);
				} catch (IllegalAccessException e) {
					log.error("实例化 {} 失败! 没有public构造函数?", serializer.getSerializer(), e);
				}
			}
		}
	}
}
