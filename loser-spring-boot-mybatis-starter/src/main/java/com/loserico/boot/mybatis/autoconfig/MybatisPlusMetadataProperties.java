package com.loserico.boot.mybatis.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * <p>
 * Copyright: (C), 2021-04-07 10:37
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.mybatis-plus.metadata")
public class MybatisPlusMetadataProperties {
	
	/**
	 * 是否开启对Entity的METADATA自动注入<p>
	 * <ul>
	 * METADATA是指
	 * <li/>creator
	 * <li/>createTime
	 * <li/>modifier
	 * <li/>modifyTime
	 * </ul>
	 * 等等, 通过useDefaults开启这几个默认的字段名, 否则自己手工指定<p>
	 * 开启后Entity的字段上要加 @TableField(fill = FieldFill.INSERT_UPDATE)
	 */
	private boolean autoInject = false;
	
	/**
	 * 是否使用默认的元字段名
	 * METADATA是指
	 * <li/>creator
	 * <li/>createTime
	 * <li/>modifier
	 * <li/>modifyTime
	 * </ul>
	 */
	private boolean useDefaults = false;
	
	/**
	 * 格式:<p>
	 * key:字段名 value:字段类型<p>
	 * <p>
	 * 如: creator:String
	 */
	private Map<String, String> metaFields;
}
