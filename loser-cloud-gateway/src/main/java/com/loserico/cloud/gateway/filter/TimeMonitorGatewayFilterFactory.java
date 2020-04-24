package com.loserico.cloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 记录API执行时间
 * 用法:
 * filters:
 *   TimeMonitor=enabled,true
 * <p>
 * Copyright: (C), 2020/4/24 10:56
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class TimeMonitorGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
	
	private static final String COUNT_START_TIME = "countStartTime";
	
	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return new TimeMonitorGatewayFilter(config);
	}
	
	public static class TimeMonitorGatewayFilter implements GatewayFilter, Ordered {
		
		private NameValueConfig nameValueConfig;
		
		public TimeMonitorGatewayFilter(NameValueConfig nameValueConfig) {
			this.nameValueConfig = nameValueConfig;
		}
		
		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			String name = nameValueConfig.getName();
			String value = nameValueConfig.getValue();
			log.info("name:{},value:{}", name, value);
			
			if ("false".equalsIgnoreCase(value)) {
				return null;
			}
			exchange.getAttributes().put(COUNT_START_TIME, System.currentTimeMillis());
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				Long startTime = exchange.getAttribute(COUNT_START_TIME);
				if (startTime != null) {
					StringBuilder sb = new StringBuilder(exchange.getRequest().getURI().getRawPath())
							.append(": ")
							.append(System.currentTimeMillis() - startTime)
							.append("ms");
					sb.append(" params:").append(exchange.getRequest().getQueryParams());
					log.info(sb.toString());
				}
			}));
		}
		
		/**
		 * order越小越先执行
		 *
		 * @return
		 */
		@Override
		public int getOrder() {
			return -100;
		}
	}
}
