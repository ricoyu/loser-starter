package com.loserico.oauth2.handler;

import com.loserico.common.lang.concurrent.LoserExecutors;
import com.loserico.common.lang.context.ThreadContext;
import com.loserico.common.lang.errors.ErrorType;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.security.constants.LoserSecurityConstants;
import com.loserico.security.constants.SpringSecurityExceptions;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
	
	private static final ThreadPoolExecutor POOL = LoserExecutors.of("login-fail-pool")
			.corePoolSize(1)
			.maximumPoolSize(100)
			.queueSize(1000)
			.build();
	
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException e) throws IOException, ServletException {
		
		String username = ThreadContext.get(LoserSecurityConstants.SPRING_SECURITY_FORM_USERNAME_KEY);
		
		boolean processed = false;
		//如果需要在框架外对认证异常消息做处理, 可以实现AuthenticationFailMessageProcessor
		
		//表示只需返回框架提供的默认认证异常消息
			ErrorType errorType = SpringSecurityExceptions.errorType(e.getClass());
			if (errorType == null) {
				if (e.getCause() != null && e.getCause().getClass() == NullPointerException.class) {
					errorType = ErrorTypes.INTERNAL_SERVER_ERROR;
				} else {
					errorType = ErrorTypes.USERNAME_PASSWORD_MISMATCH;
				}
			}
			Result result = Results.status(errorType).build();
			RestUtils.writeJson(response, result);
		
		if (isBlank(username)) {
			return;
		}
	}
	
}
