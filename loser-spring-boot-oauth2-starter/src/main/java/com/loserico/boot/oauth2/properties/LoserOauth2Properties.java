package com.loserico.boot.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2022-10-25 8:24
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.oauth2")
public class LoserOauth2Properties {
	
	/**
	 * 是否启动为一个Spring Security Oauth2 Server
	 */
	//private boolean enabled = false;
	private JWT jwt;
	
	@Data
	public static class JWT {
		
		/**
		 * 是否启用JWT token
		 */
		private boolean enabled = false;
	}
}
