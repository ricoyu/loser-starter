package com.loserico.gateway.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2022-10-17 20:18
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties("loser.gateway")
public class GatewayProperties {
	
	private String clientId;
	
	private String clientSecret;
	
	private String checkTokenUrl;
}
