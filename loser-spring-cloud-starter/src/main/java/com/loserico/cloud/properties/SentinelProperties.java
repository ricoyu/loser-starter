package com.loserico.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2023-03-20 16:28
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.sentinel")
public class SentinelProperties {
	
	/**
	 * 开启后会自动注册Bean: RestBlockExceptionHandler, SentinelResourceAspect
	 */
	private boolean enabled;

	private boolean restExceptionEnabled = true;

	/**
	 * 是否开启sentinel的授权规则限流, 开启后会注册一个MyRequestOriginParser Bean
	 * 只需要在feign的被调用方开启
	 */
	private Boolean sentinelAuthEnabled = true;
}
