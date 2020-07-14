package com.loserico.cloud.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020/4/24 11:24
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.gateway")
public class LoserGatewayProperties {
	
	private boolean timeMonitorFilterEnabled = true;
	
	private boolean timeBetweenRouteEnabled = true;
}
