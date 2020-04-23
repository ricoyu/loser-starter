package com.loserico.cloud.gateway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 当前时间在给定时间范围内, 包含两边界值
 * <p>
 * Copyright: (C), 2020/4/22 14:33
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class TimeBetweenRoutePredicateFactory extends AbstractRoutePredicateFactory<TimeBetweenRoutePredicateFactory.Config> {
	
	public TimeBetweenRoutePredicateFactory() {
		super(Config.class);
	}
	
	@Override
	public Predicate<ServerWebExchange> apply(Config config) {
		LocalTime startTime = config.getStartTime();
		LocalTime endTime = config.getEndTime();
		return new Predicate<ServerWebExchange>() {
			@Override
			public boolean test(ServerWebExchange serverWebExchange) {
				LocalTime now = LocalTime.now();
				boolean matched = now.compareTo(startTime) >= 0 && now.compareTo(endTime) <= 0;
				log.info("matched:{}, 当前时间:{}" + (matched ? "在" : "不在") + "时间范围{}-{}内", matched, now, startTime, endTime);
				return matched;
			}
		};
	}
	
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("startTime", "endTime");
	}
	
	@Data
	public static class Config {
		private LocalTime startTime;
		private LocalTime endTime;
	}
}
