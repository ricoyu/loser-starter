package com.loserico.cloud.gateway.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020/5/2 11:07
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.gateway.exception")
public class LoserGatewayExceptionProperties {
	
	/**
	 * 异常信息里面是否同时返回debug message(Stack trace)
	 * 开发环境可以打开
	 */
	private boolean debugMsg = false;
	
}
