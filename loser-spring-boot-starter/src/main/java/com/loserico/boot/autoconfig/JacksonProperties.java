package com.loserico.boot.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Copyright: (C), 2020/4/23 12:45
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.jackson")
public class JacksonProperties {
	
	private boolean enabled = true;
	
	/**
	 * 是否将日期时间类型序列化成格林威治时间1970-01-01 00:00:00依赖的毫秒数
	 */
	private boolean epochBased = false;
	
	/**
	 * 支持基于自定义Enum对象的code或者desc属性反序列化成对应的enum对象
	 */
	private List<String> enumProperties = Arrays.asList("code", "desc");
}
