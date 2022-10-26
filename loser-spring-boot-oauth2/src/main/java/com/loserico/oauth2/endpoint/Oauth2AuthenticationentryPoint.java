package com.loserico.oauth2.endpoint;

import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.web.utils.RestUtils;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.loserico.common.lang.errors.ErrorTypes.TOKEN_EXPIRED;
import static com.loserico.common.lang.errors.ErrorTypes.USERNAME_PASSWORD_MISMATCH;

/**
 * <p>
 * Copyright: (C), 2022-10-25 16:29
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class Oauth2AuthenticationentryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		Result result = null;
		if (authException instanceof InsufficientAuthenticationException) {
			result = Results.status(USERNAME_PASSWORD_MISMATCH).build();
		} else {
			result = Results.status(TOKEN_EXPIRED).build();
		}
		RestUtils.writeJson(response, result);
	}
}
