package com.loserico.boot.security.service;

import com.loserico.cache.auth.AuthUtils;
import com.loserico.common.lang.context.ThreadContext;
import com.loserico.security.constants.SecurityConstants;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Token验证用这个
 * <p>
 * Copyright: Copyright (c) 2018-07-24 13:41
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 */
public class PreAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	
	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
		String accessToken = ThreadContext.get(SecurityConstants.ACCESS_TOKEN);
		return AuthUtils.userDetails(accessToken, User.class);
	}

}
