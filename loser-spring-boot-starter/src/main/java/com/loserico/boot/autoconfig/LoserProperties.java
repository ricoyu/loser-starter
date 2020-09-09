package com.loserico.boot.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020/4/23 12:52
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser")
public class LoserProperties {
	
	/**
	 * Tomcat容器启动时设置时区
	 */
	private String timezone = "Asia/Shanghai";
	
	/**
	 * 在事务方法内另起线程执行以操作, 这个操作也需要在事务内运行时可以通过开启该配置来实现
	 */
	private boolean asyncTransaction = true;
	
	/**
	 * 是否开启@PostInitialize注解支持
	 */
	private boolean enablePostInitialize = true;
	
	private String name;
}
