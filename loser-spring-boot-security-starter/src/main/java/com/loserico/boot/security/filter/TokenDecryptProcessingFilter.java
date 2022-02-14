package com.loserico.boot.security.filter;

import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.codec.RsaUtils;
import com.loserico.codec.exception.PrivateDecryptException;
import com.loserico.common.lang.context.ThreadContext;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.security.exception.TimestampInvalidException;
import com.loserico.security.exception.TimestampMissingException;
import com.loserico.security.utils.SecurityRequestUtils;
import com.loserico.security.vo.AuthRequest;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.loserico.boot.security.constants.SecurityConstants.BEARER_TOKEN_PREFIX;
import static com.loserico.boot.security.constants.SecurityConstants.PIC_CODE_URL;
import static com.loserico.boot.security.constants.SecurityConstants.REQUEST_HEADER_AUTHORIZATION;
import static com.loserico.boot.security.constants.ThreadLocalSecurityConstants.AUTH_REQUEST;
import static com.loserico.boot.security.constants.ThreadLocalSecurityConstants.USERNAME;
import static com.loserico.common.lang.errors.ErrorTypes.INVALID_URI_ACCESS;
import static com.loserico.common.lang.errors.ErrorTypes.RSA_DECRYPT_FAIL;
import static com.loserico.common.lang.errors.ErrorTypes.TIMESTAMP_INVALID;
import static com.loserico.common.lang.errors.ErrorTypes.TIMESTAMP_MISMATCH;
import static com.loserico.common.lang.errors.ErrorTypes.TIMESTAMP_MISSING;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_INVALID;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_MISSING;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 将客户端传过来的加密后的access_token解密
 * <p>
 * "uri=/saleOrder/search&access_token=dHDG13ms4868gFNfuYuprzXbOEgNwivuBh4MZabX5StkcayNHmKMK2jXIILL5WkK2k&timestamp="+timestamp
 * 客户端将请求的URI, access_toen, 客户端当前的timestamp用登录后返回给客户端的公钥加密, 将加密后的字符串作为access_token传递过来
 * <p>
 * 后端对access_token解密, 检查请求的URI是否匹配, 客户端/服务器时钟是否同步(timestmap不能晚于当前服务器时间12秒), 请求是否过期(默认4秒)
 * <p>
 * Copyright: Copyright (c) 2021-05-14 10:23
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class TokenDecryptProcessingFilter extends OncePerRequestFilter {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		
		/*
		 * 如果在白名单里的URI就不要拦截
		 * 注意白名单是支持/device/log/download/**这种形式的, 所以要用AntMatche, 不能用字符串匹配
		 */
		Set<String> whiteList = anonymousUrls();
		List<AntPathRequestMatcher> whiteListMatchers = whiteList.stream()
				.map((urlPatterm) -> new AntPathRequestMatcher(urlPatterm, null, false))
				.collect(toList());
		for (AntPathRequestMatcher matcher : whiteListMatchers) {
			if (matcher.matches(request)) {
				chain.doFilter(request, response);
				return;
			}
		}
		
		/*
		 * 如果ThreadContext中已经放了username, 表示已经以某种方式验证过了, 这里就不要再次验证了
		 */
		String username = ThreadContext.get(USERNAME);
		if (isNotBlank(username)) {
			chain.doFilter(request, response);
			return;
		}
		
		String accessToken = request.getHeader(REQUEST_HEADER_AUTHORIZATION);
		
		if (isBlank(accessToken)) {
			Result result = Results.status(TOKEN_MISSING).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		boolean startsWith = accessToken.startsWith(BEARER_TOKEN_PREFIX);
		if (!startsWith) {
			Result result = Results.status(TOKEN_INVALID).build();
			RestUtils.writeJson(response, result);
			return;
		}
		//去掉Bearer 前缀, 拿到真正的Token
		accessToken = accessToken.replaceAll(BEARER_TOKEN_PREFIX, "");
		
		/*
		 * 本地debug只要传真正的token就可以了, 显式配置Token不加密也直接传token
		 */
		if (isDebug(request) || !properties.isTokenEncrypted()) {
			AuthRequest authRequest = new AuthRequest();
			authRequest.setAccessToken(accessToken);
			ThreadContext.put(AUTH_REQUEST, authRequest);
			
			chain.doFilter(request, response);
			return;
		}
		
		AuthRequest authRequest = null;
		try {
			log.info("开始用RSA私钥解密 token:{}", accessToken);
			if (properties.isTokenEncrypted()) {
				accessToken = RsaUtils.privateDecrypt(accessToken);
			}
			authRequest = SecurityRequestUtils.parseAuthRequest(uri, accessToken);
		} catch (PrivateDecryptException e) {
			log.error("解密失败", e);
			log.error("Token不合法 {}", accessToken);
			
			Result result = Results.status(RSA_DECRYPT_FAIL).build();
			RestUtils.writeJson(response, result);
			return;
		} catch (TimestampMissingException e) { //没有传timestamp参数
			log.info("请求参数缺少timestamp参数!");
			Result result = Results.status(TIMESTAMP_MISSING).build();
			RestUtils.writeJson(response, result);
			return;
		} catch (TimestampInvalidException e) { //timestamp参数不是有效的毫秒数
			log.info("timestmap必须是合法的毫秒数!");
			Result result = Results.status(TIMESTAMP_INVALID).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		/*
		 * 请求的URI和声称要访问的URI不一致
		 */
		if (!authRequest.requestPathMatchs(properties.getContextPath())) {
			log.error("token中的URI: {0} 和实际请求的UTI: {1} 不一致，请求被拦截!", authRequest.getUri(), authRequest.getActualUri());
			Result result = Results.status(INVALID_URI_ACCESS).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		//从request中获取timestamp参数, 不存在则返回-1
		long timestmap = timestamp(request);
		//url里面传的timestamp参数与token中解密出来的timestamp参数不一致
		if (!authRequest.matches(timestmap)) {
			log.error("timestamp参数: {} 与token中解密出来的timestamp: {} 不匹配", timestmap, authRequest.getTimestamp());
			Result result = Results.status(TIMESTAMP_MISMATCH).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		ThreadContext.put(AUTH_REQUEST, authRequest);
		chain.doFilter(request, response);
	}
	
	private long timestamp(ServletRequest request) {
		String timestamp = request.getParameter("timestamp");
		if (isBlank(timestamp)) {
			return -1L;
		}
		try {
			return Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			log.error("将timestamp:{} 转成long失败", timestamp);
		}
		return -1L;
	}
	
	private boolean isDebug(ServletRequest request) {
		return "true".equals(request.getParameter("debug"))
				&& ("127.0.0.1".equals(request.getRemoteAddr()) || "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()));
	}
	
	/**
	 * 这些URL不需要认证就可以访问
	 *
	 * @return
	 */
	private Set<String> anonymousUrls() {
		Set<String> whiteList = new HashSet<>();
		whiteList.addAll(properties.getWhiteList());
		whiteList.add(properties.getLoginUrl());
		
		/*
		 * 如果集成了图片验证码功能的话, 要把图片验证码的URL也添加到白名单里面
		 */
		if (properties.getPicCode() != null && properties.getPicCode().isEnabled()) {
			whiteList.add(PIC_CODE_URL);
		}
		return whiteList;
	}
}
