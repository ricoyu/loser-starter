package com.loserico.boot.autoconfig;

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
@ConfigurationProperties(prefix = "loser.mongo")
public class LoserMongoProperties {
	
	/**
	 * Mongo Shell 脚本文件所在文件夹路径
	 */
	private String location;
	
	/**
	 * Mongo Shell 脚本文件后缀
	 */
	private String suffix;
}
