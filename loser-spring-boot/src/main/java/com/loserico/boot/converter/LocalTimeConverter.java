package com.loserico.boot.converter;

import com.loserico.common.lang.utils.DateUtils;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

@ConfigurationPropertiesBinding
public class LocalTimeConverter implements Converter<String, LocalTime> {
	
	@Override
	public LocalTime convert(String source) {
		return DateUtils.toLocalTime(source);
	}
}
