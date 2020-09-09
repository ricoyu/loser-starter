package com.loserico.boot.autoconfig.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loserico.common.lang.utils.ReflectionUtils;
import com.loserico.json.ObjectMapperDecorator;
import com.loserico.json.jackson.JacksonUtils;
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
			log.info(">>>>>>JacksonUtils竟然已被被初始化了? 那就重来一遍吧<<<<<<");
			ObjectMapperDecorator decorator = new ObjectMapperDecorator();
			decorator.decorate(objectMapper);
			ReflectionUtils.setField(jacksonUtilsClass, "objectMapper", objectMapper);
		}
	}
}
