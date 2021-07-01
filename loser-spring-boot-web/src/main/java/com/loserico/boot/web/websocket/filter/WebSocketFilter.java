package com.loserico.boot.web.websocket.filter;

import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.utils.StringUtils;
import com.loserico.common.lang.vo.Results;
import com.loserico.common.spring.utils.ServletUtils;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 客户端要求后端Websocket服务器推送消息时, 如果是分布式部署的, 那么有可能值推送连接到某一台服务器上的websocket客户端
 * 所以需要额外处理, 使得所有websocket服务器上连接的客户端都被通知到
 * <p>
 * 这个filter拦截特定的URI, 然后通过Redis发布一条消息, 消息的channel是uri的后半部分, 消息内容是request body部分
 * <p>
 * Copyright: (C), 2020-09-11 15:36
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class WebSocketFilter implements Filter {
	
	private static final String BACK_SLASH = "/";
	private static final String REDIS_KEY_DELIMINATER = ":";
	private static final String QUESTION_MARK = "?";
	
	@Value("${loser.websocket.pathPrefix:/ws/push/**}")
	private String pathPrefix;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String path = ServletUtils.requestPath(request);
		
		if (!ServletUtils.pathMatch(path, pathPrefix)) {
			chain.doFilter(request, response);
			return;
		}
		
		String pathSuffix = null;
		
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		//如果是path正则, 直接获取path后半部分
		if (antPathMatcher.isPattern(pathPrefix)) {
			log.debug("====== {} is a regrx pattern ======", pathPrefix);
			pathSuffix = antPathMatcher.extractPathWithinPattern(pathPrefix, path);
			log.debug("====== path suffix is {} ======", pathSuffix);
		} else {
			log.debug("====== {} is not a regrx pattern ======", pathPrefix);
			//手工截取path后半部分
			pathSuffix = path.replace(pathPrefix, "");
			log.debug("====== path suffix is {} ======", pathSuffix);
			if (pathSuffix.indexOf(BACK_SLASH) == 0) {
				pathSuffix = pathSuffix.substring(1, pathSuffix.length());
			}
		}
		
		/*
		 * 如果截取到的路径后半部分是/update/aaa?name=rico
		 * 带参数部分的话把参数部分截掉
		 */
		int paramStartIndex = pathSuffix.indexOf(QUESTION_MARK);
		if (paramStartIndex != -1) {
			pathSuffix = pathSuffix.substring(0, paramStartIndex);
		}
		
		pathSuffix = StringUtils.trimTrailingCharacter(pathSuffix, BACK_SLASH);
		pathSuffix = StringUtils.trimLeadingCharacter(pathSuffix, BACK_SLASH);
		
		//把/替换成:
		String redisChannel = pathSuffix.replaceAll(BACK_SLASH, REDIS_KEY_DELIMINATER);
		log.debug("====== channel is {} ======", redisChannel);
		
		String data = ServletUtils.readRequestBody((HttpServletRequest) request);
		log.debug("====== message is {} ======", data);
		JedisUtils.publish(redisChannel, data);
		
		//匹配了这个filter, 后面的filter就无需执行了, 这里就是一个endpoint
		RestUtils.writeJson(response, Results.success().build());
		return;
	}
	
	
}
