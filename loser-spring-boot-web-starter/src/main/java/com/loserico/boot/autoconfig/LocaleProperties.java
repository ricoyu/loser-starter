package com.loserico.boot.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Copyright: (C), 2020-09-02 15:51
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.locale")
public class LocaleProperties {
	
	/**
	 * 是否启用动态切换locale
	 */
	private boolean enabled = false;
	
	/**
	 * 默认lang, url里面可以通过参数lang=zh_CN来切换
	 */
	private String paramName = "lang";
	
	/**
	 * 通过Cookie切换locale时, 设置的Cookie的名字
	 */
	private String cookieName = "lang";
	
	/**
	 * ignoreInvalidLocale: lang=zh_CN这个参数值如果传了不合法的locale字符串, 是否忽略, 默认true
	 */
	private boolean ignoreInvalidLocale = true;
}
