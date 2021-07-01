package com.loserico.boot.es.autoconfig;

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
@ConfigurationProperties(prefix = "loser.es")
public class LoserESProperties {
	
	/**
	 * 是否启动时初始化Elasticsearch客户端连接
	 */
	private boolean init = true;
	
	/**
	 * template_name,template_file形式 <br/>
	 * 将定义好的 Index Template 写到指定的文件里面
	 * 文件可以从classpath下读, 也可以从文件系统下读
	 * <ol>
	 *     <li/>event_template,classpath:event_template.json 从classpath根目录下读
	 *     <li/>event_template,event_template.json           从work dir下读
	 *     <li/>event_template,/root/event_template.json     从指定目录下读
	 * </ol>
	 */
	private String[] templates;
	
}
