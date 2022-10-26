package com.loserico.gateway.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties("loser.gateway.auth")
public class GatewayAuthProperties {
	
	private Set<String> shouldSkipUrls;
	
}
