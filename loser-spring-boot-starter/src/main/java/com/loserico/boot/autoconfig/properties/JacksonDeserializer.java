package com.loserico.boot.autoconfig.properties;

import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.Data;

/**
 * <p>
 * Copyright: (C), 2021-07-09 16:49
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
public class JacksonDeserializer {
	
	/**
	 * 要为哪个对象指定反序列化器
	 */
	private Class type;
	
	/**
	 * 自定义反序列化器
	 */
	private Class<? extends JsonDeserializer> deserializer;
}
