package com.loserico.boot.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020-08-14 10:15
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.auth")
public class LoserAuthProperties {
	
	/**
	 * 是否启用基于Redis的认证模式
	 */
	private boolean enabled = false;
	
	/**
	 * 是否启动就执行过期token清理
	 */
	private boolean clearOnStart = false;
}
