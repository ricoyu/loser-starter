package com.loserico.security.handler;

import com.loserico.cache.auth.AuthUtils;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.web.utils.RestUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_EXPIRED;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_INVALID;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_MISSING;
import static com.loserico.security.constants.SecurityConstants.BEARER_TOKEN_PREFIX;
import static com.loserico.security.constants.SecurityConstants.REQUEST_HEADER_AUTHORIZATION;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * Copyright: Copyright (c) 2021-05-14 11:26
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
	
	private static final Logger log = getLogger(LoginSuccessHandler.class);
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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
		String actualToken = null;
		if (isBlank(accessToken)) {
			Result result = Results.status(TOKEN_MISSING).build();
			RestUtils.writeJson(response, result);
			return;
		}
		
		String username = AuthUtils.username(accessToken);
		boolean success = AuthUtils.logout(accessToken);
		if (!success) {
			logoutFail(response);
			return;
		}
		
		logoutSuccess(response, username);
	}
	
	
	private boolean isDebug(ServletRequest request) {
		return "true".equals(request.getParameter("debug"))
				&& ("127.0.0.1".equals(request.getRemoteAddr()) || "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()));
	}
	
	private void logoutFail(HttpServletResponse response) {
		Result result = Results.status(TOKEN_EXPIRED).build();
		RestUtils.writeJson(response, result);
	}
	
	private void logoutSuccess(HttpServletResponse response, String username) {
		Result result = Results.success().build();
		RestUtils.writeJson(response, result);
	}
}
