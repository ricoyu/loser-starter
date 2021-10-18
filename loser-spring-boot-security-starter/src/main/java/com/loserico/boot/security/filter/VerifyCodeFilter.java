package com.loserico.boot.security.filter;

import com.loserico.boot.security.constants.SecurityConstants;
import com.loserico.boot.security.errors.SecurityErrors;
import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.common.spring.utils.ServletUtils;
import com.loserico.web.utils.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.loserico.boot.security.constants.SecurityConstants.VERIFY_CODE_PREFIX;
import static com.loserico.common.lang.utils.StringUtils.concat;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 验证登录时提供的验证码是否正确
 * <p>
 * Copyright: (C), 2020-08-12 15:03
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class VerifyCodeFilter extends OncePerRequestFilter {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		if (!ServletUtils.pathMatch(servletRequest, properties.getLoginUrl())) {
			chain.doFilter(request, response);
			return;
		}
		
		//从request中拿codeId, verifyCode
		String codeId = request.getParameter(SecurityConstants.VERIFY_CODE_ID);
		String verifyCode = request.getParameter(SecurityConstants.VERIFY_CODE);
		
		//万能验证码
		if ("wnyz".equalsIgnoreCase(verifyCode)) {
			chain.doFilter(request, response);
			return;
		}
		
		//没有提供codeId, verifyCode参数
		if (isBlank(codeId) || isBlank(verifyCode)) {
			Result result = Results.status(SecurityErrors.AUTH_CODE_MISS).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		String code = JedisUtils.get(concat(VERIFY_CODE_PREFIX, codeId).toLowerCase());
		//code不匹配或者已经过期
		if (!verifyCode.equalsIgnoreCase(code)) {
			Result result = Results.status(SecurityErrors.AUTH_CODE_EXPIRED).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		chain.doFilter(request, response);
	}
}
