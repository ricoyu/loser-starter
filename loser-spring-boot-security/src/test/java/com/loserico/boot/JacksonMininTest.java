package com.loserico.boot;

import com.loserico.security.mixin.AnonymousAuthenticationTokenMixin;
import com.loserico.security.mixin.BadCredentialsExceptionMixin;
import com.loserico.security.mixin.JaasGrantedAuthorityMixin;
import com.loserico.security.mixin.OAuth2AuthenticationMixin;
import com.loserico.security.mixin.OAuth2RequestMixin;
import com.loserico.security.mixin.RememberMeAuthenticationTokenMixin;
import com.loserico.security.mixin.SwitchUserGrantedAuthorityMixin;
import com.loserico.security.mixin.TokenRequestMixin;
import com.loserico.security.mixin.UnmodifiableListMixin;
import com.loserico.security.mixin.UnmodifiableSetMixin;
import com.loserico.security.mixin.UserMixin;
import com.loserico.security.mixin.UsernamePasswordAuthenticationTokenMixin;
import com.loserico.common.lang.utils.IOUtils;
import com.loserico.json.jackson.JacksonUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Collections;

/**
 * <p>
 * Copyright: (C), 2020/4/29 20:33
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class JacksonMininTest {
	
	@BeforeClass
	public static void init() {
		JacksonUtils.addMixIn(OAuth2Authentication.class, OAuth2AuthenticationMixin.class);
		JacksonUtils.addMixIn(SwitchUserGrantedAuthority.class, SwitchUserGrantedAuthorityMixin.class);
		JacksonUtils.addMixIn(JaasGrantedAuthority.class, JaasGrantedAuthorityMixin.class);
		JacksonUtils.addMixIn(AnonymousAuthenticationToken.class, AnonymousAuthenticationTokenMixin.class);
		JacksonUtils.addMixIn(RememberMeAuthenticationToken.class, RememberMeAuthenticationTokenMixin.class);
		JacksonUtils.addMixIn(Collections.<Object>unmodifiableSet(Collections.emptySet()).getClass(), UnmodifiableSetMixin.class);
		JacksonUtils.addMixIn(Collections.<Object>unmodifiableList(Collections.emptyList()).getClass(), UnmodifiableListMixin.class);
		JacksonUtils.addMixIn(User.class, UserMixin.class);
		JacksonUtils.addMixIn(UsernamePasswordAuthenticationToken.class, UsernamePasswordAuthenticationTokenMixin.class);
		JacksonUtils.addMixIn(BadCredentialsException.class, BadCredentialsExceptionMixin.class);
		JacksonUtils.addMixIn(TokenRequest.class, TokenRequestMixin.class);
		JacksonUtils.addMixIn(OAuth2Request.class, OAuth2RequestMixin.class);
	}
	
	@Test
	public void testDeserializeOAuth2Authentication() {
		String json = IOUtils.readClassPathFileAsString("oauth-authentication.json");
		//System.out.println(json);
		OAuth2Authentication authentication = JacksonUtils.toObject(json, OAuth2Authentication.class);
		Assert.assertNotNull(authentication);
	}
}
