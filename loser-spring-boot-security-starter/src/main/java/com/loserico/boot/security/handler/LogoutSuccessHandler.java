package com.loserico.boot.security.handler;

import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.cache.auth.AuthUtils;
import com.loserico.codec.RsaUtils;
import com.loserico.codec.exception.PrivateDecryptException;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.loserico.boot.security.constants.SecurityConstants.BEARER_TOKEN_PREFIX;
import static com.loserico.boot.security.constants.SecurityConstants.REQUEST_HEADER_AUTHORIZATION;
import static com.loserico.common.lang.errors.ErrorTypes.RSA_DECRYPT_FAIL;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_EXPIRED;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_INVALID;
import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_MISSING;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
@Slf4j
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
	
	@Autowired
	private LoserSecurityProperties properties;
	
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
		/*
		 * 本地debug只要传真正的token就可以了, 显式配置Token不加密也直接传token
		 */
		if (isDebug(request) || !properties.isTokenEncrypted()) {
			log.info("debug 模式, token不需要解密!");
		} else {
			try {
				log.info("开始用RSA私钥解密 token:{}", accessToken);
				if (properties.isTokenEncrypted()) {
					accessToken = RsaUtils.privateDecrypt(accessToken);
				}
			} catch (PrivateDecryptException e) {
				log.error("解密失败", e);
				log.error("Token不合法 {}", accessToken);
				
				Result result = Results.status(RSA_DECRYPT_FAIL).build();
				RestUtils.writeJson(response, result);
				return;
			}
		}
		
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
