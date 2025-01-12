package com.loserico.boot.security.autoconfig;

import com.loserico.boot.security.filter.PreAuthenticationFilter;
import com.loserico.boot.security.filter.RetryCountFilter;
import com.loserico.boot.security.filter.TokenDecryptProcessingFilter;
import com.loserico.boot.security.filter.VerifyCodeFilter;
import com.loserico.boot.security.handler.LoginFailureHandler;
import com.loserico.boot.security.handler.LoginSuccessHandler;
import com.loserico.boot.security.handler.LogoutSuccessHandler;
import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.boot.security.props.LoserSecurityProperties.PicCode;
import com.loserico.boot.security.service.AccessTokenService;
import com.loserico.boot.security.service.PreAuthenticationUserDetailsService;
import com.loserico.security.endpoint.RestAuthenticationEntryPoint;
import com.loserico.security.filter.SecurityExceptionFilter;
import com.loserico.security.filter.UsernamePasswordAuthenticationFilter;
import com.loserico.web.filter.HttpServletRequestRepeatedReadFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

import static com.loserico.boot.security.constants.SecurityConstants.PIC_CODE_URL;

/**
 * 这里配置SpringSecurity的主要核心组件/主流程
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class LoserWebSecurityAutoConfig {

	@Autowired
	private LoserSecurityProperties properties;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired(required = false)
	private UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
				//Spring Security不会创建或使用HTTP会话（HttpSession）来存储任何与用户身份验证相关的信息。
				.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint))
				.authorizeHttpRequests(authorizeRequests -> {
					//可以通过配置loser.security.white-list来配置白名单, 即这些白名单中得url不需要认证就可以访问
					List<String> whiteList = properties.getWhiteList();
					authorizeRequests.requestMatchers(anonymousUrls()).permitAll();
					authorizeRequests.anyRequest().authenticated();
				});

		/*
		 * 在过滤器链上发生未处理的异常时, RestExceptionAdvice是处理不到的,
		 * 所以通过这个Filter来统一捕获, 然后通过HandlerExceptionResolver代理给RestExceptionAdvice来处理
		 */
		httpSecurity.addFilterBefore(exceptionFilter(), UsernamePasswordAuthenticationFilter.class)
				//提供SpringSecurity过滤器链对Request Body的可重复读取
				.addFilterBefore(new HttpServletRequestRepeatedReadFilter(), WebAsyncManagerIntegrationFilter.class)
				//这个过滤器用于解密token, 只在配置了loser.security.token-encrypted=true时才生效
				.addFilterBefore(tokenDecryptProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(preAuthenticationFilter(authenticationManager(httpSecurity)),
						UsernamePasswordAuthenticationFilter.class);

		//动态添加登录失败重试次数限制
		if (properties.isAuthCenterEnabled()) {
			httpSecurity.addFilterAt(usernamePasswordAuthenticationFilter(authenticationManager(httpSecurity)),
					org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		}

		PicCode picCode = properties.getPicCode();
		if (picCode != null && picCode.isEnabled()) {
			httpSecurity.addFilterBefore(verifyCodeFilter(), UsernamePasswordAuthenticationFilter.class);
		}

		if (properties.isAuthCenterEnabled()) {
			httpSecurity.addFilterAt(usernamePasswordAuthenticationFilter(authenticationManager(httpSecurity)),
					UsernamePasswordAuthenticationFilter.class);
		}

		if (properties.isAuthCenterEnabled()) {
			httpSecurity.logout(logout -> logout
					.logoutUrl(properties.getLogoutUrl())
					.logoutSuccessHandler(logoutSuccessHandler()));
		}

		return httpSecurity.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/resources/**", "/static/**");
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder =
				http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.authenticationProvider(preAuthenticationProvider());
		if (properties.isAuthCenterEnabled()) {
			authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
		}
		return authenticationManagerBuilder.build();
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true",
			matchIfMissing = false)
	public AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationProvider preAuthenticationProvider() {
		PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
		authenticationProvider.setPreAuthenticatedUserDetailsService(preAuthenticationUserDetailsService());
		return authenticationProvider;
	}

	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	@ConditionalOnMissingBean(PreAuthenticationUserDetailsService.class)
	public PreAuthenticationUserDetailsService preAuthenticationUserDetailsService() {
		return new PreAuthenticationUserDetailsService();
	}

	@Bean
	public SecurityExceptionFilter exceptionFilter() {
		return new SecurityExceptionFilter();
	}

	@Bean
	public PreAuthenticationFilter preAuthenticationFilter(AuthenticationManager authenticationManager) {
		PreAuthenticationFilter authenticationFilter = new PreAuthenticationFilter();
		authenticationFilter.setAuthenticationManager(authenticationManager);
		return authenticationFilter;
	}

	@Bean
	public TokenDecryptProcessingFilter tokenDecryptProcessingFilter() {
		return new TokenDecryptProcessingFilter();
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security.pic-code", name = "enabled", havingValue = "true", matchIfMissing
			= false)
	public VerifyCodeFilter verifyCodeFilter() {
		properties.getWhiteList().add("/pic-code");
		return new VerifyCodeFilter();
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true",
			matchIfMissing = false)
	public RetryCountFilter retryCountFilter() {
		return new RetryCountFilter();
	}

	@Bean
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		properties.getWhiteList().add("/login");
		UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		authenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
		authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(properties.getLoginUrl(), "POST"));
		authenticationFilter.setAuthenticationManager(authenticationManager);
		authenticationFilter.setRsaEncrypted(properties.isPasswordEncrypted());
		return authenticationFilter;
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true",
			matchIfMissing = false)
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler();
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true",
			matchIfMissing = false)
	public AuthenticationFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}

	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true",
			matchIfMissing = false)
	public AccessTokenService accessTokenService() {
		return new AccessTokenService();
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new LogoutSuccessHandler();
	}

	private String[] anonymousUrls() {
		List<String> whiteList = properties.getWhiteList();
		whiteList.add(properties.getLoginUrl());

		if (properties.getPicCode() != null && properties.getPicCode().isEnabled()) {
			whiteList.add(PIC_CODE_URL);
		}
		return whiteList.toArray(new String[0]);
	}
}
