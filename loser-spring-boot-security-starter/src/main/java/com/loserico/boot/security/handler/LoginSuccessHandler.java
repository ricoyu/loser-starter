package com.loserico.boot.security.handler;

import com.loserico.boot.security.policy.LoginPolicyService;
import com.loserico.boot.security.policy.LoginPolicyService.Policy;
import com.loserico.boot.security.processor.FirstLoginProcessor;
import com.loserico.boot.security.processor.LoginInfoProvider;
import com.loserico.boot.security.processor.LoginSuccessPostProcessor;
import com.loserico.boot.security.processor.SingleLoginMessageProcessor;
import com.loserico.boot.security.service.AccessTokenService;
import com.loserico.boot.security.service.LoginDurationService;
import com.loserico.cache.JedisUtils;
import com.loserico.cache.auth.AuthUtils;
import com.loserico.common.lang.concurrent.LoserExecutors;
import com.loserico.common.lang.context.ThreadContext;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.common.spring.utils.ServletUtils;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.loserico.boot.security.constants.SecurityConstants.RETRY_COUNT_KEY_PREFIX;
import static com.loserico.boot.security.constants.ThreadLocalSecurityConstants.LOGIN_INFO;
import static com.loserico.common.lang.errors.ErrorTypes.ALREADY_LOGIN;

/**
 * 登录成功后负责生成token
 * <p>
 * Copyright: Copyright (c) 2021-03-30 16:49
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private static final ThreadPoolExecutor POOL = LoserExecutors.of("loser-login-pool").build();
	
	@Autowired
	private AccessTokenService accessTokenService;
	
	@Autowired(required = false)
	private LoginPolicyService loginPolicyService;
	
	@Autowired(required = false)
	private LoginDurationService loginDurationService;
	
	@Autowired(required = false)
	private SingleLoginMessageProcessor singleLoginMessageProcessor;
	
	@Autowired(required = false)
	private FirstLoginProcessor firstLoginProcessor;
	
	@Autowired(required = false)
	private LoginInfoProvider loginInfoProvider;
	
	@Autowired(required = false)
	private LoginSuccessPostProcessor loginSuccessPostProcessor;
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
		String accessToken = accessTokenService.getAccessToken();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		/*
		 * 如果该账号是首次登录, 是否需要做特殊处理? 比如强制修改密码?
		 */
		if (firstLoginProcessor != null) {
			POOL.execute(() -> {
				try {
					firstLoginProcessor.process(username);
				} catch (Throwable e) {
					log.error("", e);
				}
			});
		}
		
		User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication()
				.getAuthorities();
		String ip = ServletUtils.getRemoteRealIP(request);
		
		//没有设置登录策略, 那么默认单点登录
		if (loginPolicyService == null) {
			doLogin(response, accessToken, username, userDetails, authorities, ip, true);
			return;
		}
		
		Policy policy = loginPolicyService.loginPolicy(username);
		
		//如果登录策略是可以多处登录, 那么不用管是否已经在别处登录
		if (policy == Policy.MULTIPLE) {
			doLogin(response, accessToken, username, userDetails, authorities, ip, false);
			return;
		}
		
		/*
		 * 如果登录策略是踢掉前一个登录后重新登录:
		 * 1. 那么先要把前一个登录的人登出一下
		 * 2. 发一条消息(方便通知其被迫下线)
		 * 3. 重新登录
		 *
		 * 这些步骤在lua脚本里面实现了
		 */
		if (policy == Policy.KICK_OFF) {
			doLogin(response, accessToken, username, userDetails, authorities, ip, true);
			return;
		}
		
		//不允许再次登录
		boolean logined = AuthUtils.isLogined(username);
		if (policy == Policy.SINGLE && logined) {
			Result result = null;
			if (singleLoginMessageProcessor != null) {
				result = Results.status(ALREADY_LOGIN.code(), singleLoginMessageProcessor.onSingletonLogin(username)).build();
			} else {
				result = Results.status(ALREADY_LOGIN).build();
			}
			RestUtils.writeJson(response, result);
			return;
		}
		doLogin(response, accessToken, username, userDetails, authorities, ip, false);
	}
	
	private void doLogin(HttpServletResponse response,
	                     String accessToken,
	                     String username,
	                     User userDetails, List<? extends GrantedAuthority> authorities,
	                     String ip,
	                     boolean singleSignOn) {
		Map<String, Object> loginInfo = ThreadContext.get(LOGIN_INFO);
		if (loginInfo == null) {
			loginInfo = new HashMap<>();
			loginInfo.put("ip", ip);
		} else {
			loginInfo.put("ip", ip);
		}
		
		if (loginInfoProvider != null) {
			Map<String, Object> loginInfoMap = loginInfoProvider.loginInfo(username);
			if (loginInfoMap != null) {
				loginInfo.putAll(loginInfoMap);
			}
		}
		
		long expires = 30L;
		if (loginDurationService != null) {
			expires = loginDurationService.expiresInMinutes(username);
		}
		AuthUtils.login(username, accessToken, expires, TimeUnit.MINUTES, userDetails, authorities, loginInfo, singleSignOn);
		Result result = Results.success().result(accessToken);
		RestUtils.writeJson(response, result);
		
		//登录成功后把之前登录失败的次数清零
		String retryCountKey = RETRY_COUNT_KEY_PREFIX + username;
		JedisUtils.del(retryCountKey);
		
		POOL.execute(() -> {
			try {
				loginSuccessPostProcessor.onSuccess(username);
			} catch (Throwable e) {
				log.error("", e);
			}
		});
	}
	
}
