package com.loserico.boot.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.loserico.boot.mybatis.autoconfig.MybatisPlusMetadataProperties;
import com.loserico.common.lang.context.ThreadContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * Copyright: (C), 2021-04-07 10:49
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class LoserMetaObjectHandler implements MetaObjectHandler {
	
	@Autowired
	private MybatisPlusMetadataProperties metadataProperties;
	
	@Override
	public void insertFill(MetaObject metaObject) {
		if (metadataProperties.isUseDefaults()) {
			String username = ThreadContext.get("username");
			strictInsertFill(metaObject, "creator", String.class, username);
			strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
			strictInsertFill(metaObject, "modifier", String.class, username);
			strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
		} else {
			Map<String, String> metaFields = metadataProperties.getMetaFields();
			if (metaFields.isEmpty()) {
				return;
			}
			
			for (String field : metaFields.keySet()) {
				String fieldClass = metaFields.get(field);
				if ("String".equalsIgnoreCase(fieldClass)) {
					String value = ThreadContext.get("username");
					strictInsertFill(metaObject, field, String.class, value);
					continue;
				}
				if ("Long".equalsIgnoreCase(fieldClass)) {
					Long value = ThreadContext.get("userId");
					strictInsertFill(metaObject, field, Long.class, value);
					continue;
				}
				if ("Integer".equalsIgnoreCase(fieldClass)) {
					Integer value = ThreadContext.get("userId");
					strictInsertFill(metaObject, field, Integer.class, value);
					continue;
				}
				if ("Date".equalsIgnoreCase(fieldClass)) {
					strictInsertFill(metaObject, field, Date.class, new Date());
					continue;
				}
				if ("LocalDatetime".equalsIgnoreCase(fieldClass)) {
					strictInsertFill(metaObject, field, LocalDateTime.class, LocalDateTime.now());
					continue;
				}
			}
		}
	}
	
	@Override
	public void updateFill(MetaObject metaObject) {
		if (metadataProperties.isUseDefaults()) {
			String username = ThreadContext.get("username");
			strictUpdateFill(metaObject, "modifier", String.class, username);
			strictUpdateFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
		} else {
			Map<String, String> metaFields = metadataProperties.getMetaFields();
			if (metaFields.isEmpty()) {
				return;
			}
			
			for (String field : metaFields.keySet()) {
				String fieldClass = metaFields.get(field);
				if ("String".equalsIgnoreCase(fieldClass)) {
					String value = ThreadContext.get("username");
					strictUpdateFill(metaObject, field, String.class, value);
					continue;
				}
				if ("Long".equalsIgnoreCase(fieldClass)) {
					Long value = ThreadContext.get("userId");
					strictUpdateFill(metaObject, field, Long.class, value);
					continue;
				}
				if ("Integer".equalsIgnoreCase(fieldClass)) {
					Integer value = ThreadContext.get("userId");
					strictUpdateFill(metaObject, field, Integer.class, value);
					continue;
				}
				if ("Date".equalsIgnoreCase(fieldClass)) {
					strictUpdateFill(metaObject, field, Date.class, new Date());
					continue;
				}
				if ("LocalDatetime".equalsIgnoreCase(fieldClass)) {
					strictUpdateFill(metaObject, field, LocalDateTime.class, LocalDateTime.now());
					continue;
				}
			}
		}
	}
}
