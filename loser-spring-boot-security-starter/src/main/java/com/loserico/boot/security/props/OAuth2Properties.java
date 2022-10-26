package com.loserico.boot.security.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2022-10-21 16:06
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.oauth2")
public class OAuth2Properties {
	
	private String checkTokenUrl = "http://oauth-service/oauth/check_token";
	
	private String clientId;
	
	private String clientSecret;
}
