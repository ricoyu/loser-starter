package com.loserico.cloud.gateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.DefaultBlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.fastjson.JSON;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关限流统一处理
 * <p>
 * Copyright: (C), 2020/4/25 13:55
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class GatewayBlockRequestHandler extends DefaultBlockRequestHandler {
	
	@Override
	public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
		if (acceptsHtml(exchange)) {
			return htmlErrorResponse(ex);
		}
		
		// JSON result
		return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(buildErrorResult(ex)));
	}
	
	private boolean acceptsHtml(ServerWebExchange exchange) {
		try {
			List<MediaType> acceptedMediaTypes = exchange.getRequest().getHeaders().getAccept();
			acceptedMediaTypes.remove(MediaType.ALL);
			MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
			return acceptedMediaTypes.stream()
					.anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
		} catch (InvalidMediaTypeException ex) {
			return false;
		}
	}
	
	private Mono<ServerResponse> htmlErrorResponse(Throwable ex) {
		return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
				.contentType(MediaType.TEXT_PLAIN)
				.body(BodyInserters.fromObject(JSON.toJSONString(buildErrorResult(ex))));
	}
	
	private Result buildErrorResult(Throwable ex) {
		if (ex instanceof ParamFlowException) {
			return Results.status(HttpStatus.TOO_MANY_REQUESTS.value() + "", "fallback").build();
		} else if (ex instanceof DegradeException) {
			return Results.status(HttpStatus.TOO_MANY_REQUESTS.value() + "", "fallback").build();
		} else {
			return Results.status(HttpStatus.BAD_GATEWAY.value() + "", "gateway error").build();
		}
	}
}
