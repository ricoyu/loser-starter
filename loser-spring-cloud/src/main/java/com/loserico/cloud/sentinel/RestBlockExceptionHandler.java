package com.loserico.cloud.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.loserico.json.jackson.JacksonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.loserico.common.lang.errors.ErrorTypes.AUTHORITY_BLOCK_EXCEPTION;
import static com.loserico.common.lang.errors.ErrorTypes.DEGRADE_EXCEPTION;
import static com.loserico.common.lang.errors.ErrorTypes.FLOW_EXCEPTION;
import static com.loserico.common.lang.errors.ErrorTypes.HOT_PARAM_BLOCK_EXCEPTION;
import static com.loserico.common.lang.errors.ErrorTypes.SYSTEM_BLOCK_EXCEPTION;
import static com.loserico.common.lang.utils.StringUtils.joinWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * REST 风格统一流控异常
 * 注意, 热点参数是需要在Controller方法上加@SentinelResource注解的, 
 * 而@SentinelResource注解是由SentinelResourceAspect#invokeResourceWithSentinel处理的, 默认会向上抛异常, 
 * 所以得提供一个blockHandler, 所以这里不需要提供热点参数的处理
 * <p>
 * Copyright: (C), 2022-08-25 18:02
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class RestBlockExceptionHandler implements BlockExceptionHandler {
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
		Map<String, Object> params = new HashMap<>();
		if (e instanceof FlowException) {
			log.warn(FLOW_EXCEPTION.message());
			params.put("code", FLOW_EXCEPTION.code());
			params.put("desc", FLOW_EXCEPTION.message());
		} else if (e instanceof DegradeException) {
			log.warn(DEGRADE_EXCEPTION.message());
			params.put("code", DEGRADE_EXCEPTION.code());
			params.put("desc", DEGRADE_EXCEPTION.message());
		} else if (e instanceof AuthorityException) {
			log.warn(AUTHORITY_BLOCK_EXCEPTION.message());
			params.put("code", AUTHORITY_BLOCK_EXCEPTION.code());
			params.put("desc", AUTHORITY_BLOCK_EXCEPTION.message());
		} else if (e instanceof ParamFlowException) {
			log.warn(HOT_PARAM_BLOCK_EXCEPTION.message());
			params.put("code", HOT_PARAM_BLOCK_EXCEPTION.code());
			params.put("desc", HOT_PARAM_BLOCK_EXCEPTION.message());
		} else if (e instanceof SystemBlockException) {
			log.warn(SYSTEM_BLOCK_EXCEPTION.message());
			params.put("code", SYSTEM_BLOCK_EXCEPTION.code());
			params.put("desc", SYSTEM_BLOCK_EXCEPTION.message());
		}
		
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		CORS.builder().allowAll().build(httpServletResponse);
		httpServletResponse.setStatus(HttpStatus.OK.value());
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		JacksonUtils.writeValue(response.getWriter(), params);
	}

	private static final class CORS {
		
		public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
		public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
		public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
		
		private static final Logger logger = LoggerFactory.getLogger(CORS.class);
		
		public static CorsBuilder builder() {
			return new CorsBuilder();
		}
		
		public static class CorsBuilder {
			
			private CorsBuilder() {
			}
			
			private List<String> allowedOrigins = new ArrayList<>();
			private List<String> allowedMethods = new ArrayList<>();
			private List<String> allowedHeaders = new ArrayList<>();
			
			public CorsBuilder allowedOrigins(String... origins) {
				for (int i = 0; i < origins.length; i++) {
					String origin = origins[i];
					if (isNotBlank(origin)) {
						this.allowedOrigins.add(trim(origin));
					} else {
						logger.info("给定的Origin为空，忽略之");
					}
				}
				return this;
			}
			
			public CorsBuilder allowedMethods(String... methods) {
				for (int i = 0; i < methods.length; i++) {
					String method = methods[i];
					if (isNotBlank(method)) {
						this.allowedMethods.add(trim(method));
					} else {
						logger.info("给定的Mehtod为空，忽略之");
					}
				}
				return this;
			}
			
			public CorsBuilder allowedHeaders(String... headers) {
				for (int i = 0; i < headers.length; i++) {
					String header = headers[i];
					if (isNotBlank(header)) {
						this.allowedHeaders.add(trim(header));
					} else {
						logger.info("给定的Header为空，忽略之");
					}
				}
				return this;
			}
			
			public CorsBuilder allowAll() {
				this.allowedHeaders.clear();
				this.allowedHeaders.add("*");
				
				this.allowedMethods.clear();
				this.allowedMethods.add("*");
				
				this.allowedOrigins.clear();
				this.allowedOrigins.add("*");
				return this;
			}
			
			public void build(HttpServletResponse response) {
				response.setCharacterEncoding("UTF-8");
				response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, joinWith(", ", allowedHeaders));
				response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, joinWith(", ", allowedOrigins));
				response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, joinWith(", ", allowedMethods));
			}
		}
	}
}
