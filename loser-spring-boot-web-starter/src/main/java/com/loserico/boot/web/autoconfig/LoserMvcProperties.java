package com.loserico.boot.web.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2022-01-26 13:56
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.mvc")
public class LoserMvcProperties {
	
	/**
	 * 保障接口幂等性的token有效期, 单位秒, 默认1小时
	 */
	private Integer idemtotentTokenTtl = 60 * 60;
}
