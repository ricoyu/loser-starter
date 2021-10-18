package com.loserico.boot.autoconfig.properties;

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
	 * 在事务方法内另起线程去执行一个任务, 正常这个任务是不受当前事务控制的<p>
	 * 如果想在当前事务内运行, 可以开启这个选项
	 */
	private boolean asyncTransaction = true;
	
	/**
	 * 是否开启@PostInitialize注解支持
	 */
	private boolean enablePostInitialize = true;
	
	private String name;
}
