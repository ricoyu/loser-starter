package com.loserico.boot.web.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020-09-11 17:11
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.websocket")
public class LoserWebsocketProperties {
	
	/**
	 * 是否启用WebSocket支持
	 */
	private boolean enabled = false;
	
	/**
	 * Websocket 过滤器匹配的URI前缀
	 */
	private String pathPrefix = "/ws/push/**";
}
