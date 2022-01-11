package com.loserico.boot.web.autoconfig;

import com.loserico.boot.converter.LocalTimeConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright: (C), 2020/4/23 15:05
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
public class LoserConverterAutoConfiguration {
	
	/**
	 * properties或者yml中时间转LocalTime支持
	 *
	 * @return LocalTimeConverter
	 */
	@Bean
	@ConditionalOnMissingBean(LocalTimeConverter.class)
	public LocalTimeConverter localTimeConverter() {
		return new LocalTimeConverter();
	}
}
