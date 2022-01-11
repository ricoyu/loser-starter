package com.loserico.boot.web.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 * Copyright: (C), 2021-07-06 11:24
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.jackson")
public class LoserJacksonProperties {
	
	/**
	 * 序列化成JSON串, 字段名是否要加双引号 <br/>
	 * JSON规范来说是应该加的, 但是只要字段名符合JavaScript变量命名规范, 不加双引号也是可以的
	 */
	private boolean fieldNameQuote = true;
	
	/**
	 * 为特定的类配置自定义反序列化器
	 * <ul>
	 *     <li/>type 指定要反序列化的类全限定名
	 *     <li/>deserializer 反序列化器全限定类名
	 * </ul>
	 * 
	 */
	private List<JacksonDeserializer> deserializers;
	
	/**
	 * 为特定的类配置自定义序列化器
	 * <ul>
	 *     <li/>type 指定要序列化的类全限定名
	 *     <li/>serializer 序列化器全限定类名
	 * </ul>
	 *
	 */
	private List<JacksonSerializer> serializers;
}
