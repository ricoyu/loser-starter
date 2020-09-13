package com.loserico.boot.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020-09-10 14:27
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.cache")
public class LoserCacheProperties {
	
	/**
	 * 是否开启loser-cahce高级功能支持, 如@RedisListener注解支持
	 */
	private boolean enabled = true;
	
}
