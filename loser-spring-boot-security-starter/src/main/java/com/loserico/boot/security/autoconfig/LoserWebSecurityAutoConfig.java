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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

import static com.loserico.boot.security.constants.SecurityConstants.PIC_CODE_URL;

/**
 * <p>
 * Copyright: (C), 2021-05-13 17:39
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		prePostEnabled = true,
		securedEnabled = true,
		jsr250Enabled = true)
@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
public class LoserWebSecurityAutoConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	/**
	 * 这个需要客户端代码提供一个实例
	 */
	@Autowired(required = false)
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers()
				.xssProtection()
				.and()
				.contentSecurityPolicy("script-src 'self'");
		
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
				http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
						.and()
						.authorizeRequests();
		
		//配置不需要认证就可以访问的URLs
		String[] anonymousUrls = anonymousUrls();
		if (anonymousUrls.length > 0) {
			registry.antMatchers(anonymousUrls).anonymous();
		}
		
		//这些是必需要配置的
		HttpSecurity httpSecurity = registry.anyRequest().authenticated().and();
		//提供SpringSecurity过滤器链对Request Body的可重复读取
		//httpSecurity.addFilterBefore(new HttpServletRequestRepeatedReadFilter(), WebAsyncManagerIntegrationFilter.class);
		
		httpSecurity.addFilterBefore(exceptionFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(tokenDecryptProcessingFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(preAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		
		//动态添加登录失败重试次数限制
		if (properties.isAuthCenterEnabled()) {
			httpSecurity.addFilterBefore(retryCountFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		}
		
		//动态添加verifyCodeFilter
		PicCode picCode = properties.getPicCode();
		if (picCode != null && picCode.isEnabled()) {
			httpSecurity.addFilterBefore(verifyCodeFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		}
		
		//表单登录功能支持
		if (properties.isAuthCenterEnabled()) {
			httpSecurity.addFilterAt(usernamePasswordAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
		}
		
		//添加登出功能
		if (properties.isAuthCenterEnabled()) {
			httpSecurity.logout()
					.logoutUrl(properties.getLogoutUrl())
					.logoutSuccessHandler(logoutSuccessHandler());
		}
		
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(preAuthenticationProvider()); //用于支持token认证
		if (properties.isAuthCenterEnabled()) {
			auth.authenticationProvider(daoAuthenticationProvider());
		}
	}
	
	
	/**
	 * 负责调用UserDetailService根据username从数据库获取UserDetails
	 */
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public AuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	/**
	 * 根据token从Redis中获取认证信息
	 */
	@Bean
	public AuthenticationProvider preAuthenticationProvider() {
		PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
		authenticationProvider.setPreAuthenticatedUserDetailsService(preAuthenticationUserDetailsService());
		return authenticationProvider;
	}
	
	/**
	 * 默认用bcrypt加密算法加密
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	/**
	 * 这个跟Redis打交道
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(PreAuthenticationUserDetailsService.class)
	public PreAuthenticationUserDetailsService preAuthenticationUserDetailsService() {
		return new PreAuthenticationUserDetailsService();
	}
	
	@Bean
	public SecurityExceptionFilter exceptionFilter() {
		return new SecurityExceptionFilter();
	}
	
	/**
	 * 这个filter在UsernamePasswordAuthenticationFilter之前执行<p>
	 * 用于token认证
	 */
	public PreAuthenticationFilter preAuthenticationFilter() throws Exception {
		PreAuthenticationFilter authenticationFilter = new PreAuthenticationFilter();
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFilter;
	}
	
	
	/**
	 * 这个filter在UsernamePasswordAuthenticationFilter之前执行<p>
	 * 用于token认证, 这样需要用到Spring容器的功能, 所以注册为一个Spring Bean
	 *
	 * @return TokenDecryptProcessingFilter
	 */
	@Bean
	public TokenDecryptProcessingFilter tokenDecryptProcessingFilter() {
		return new TokenDecryptProcessingFilter();
	}
	
	/**
	 * 提供验证用户登录时提供的图片验证码功能
	 */
	@Bean
	@ConditionalOnProperty(prefix = "loser.security.pic-code", name = "enabled", havingValue = "true", matchIfMissing = false)
	public VerifyCodeFilter verifyCodeFilter() {
		properties.getWhiteList().add("/pic-code");
		return new VerifyCodeFilter();
	}
	
	/**
	 * 登录失败重试次数限制
	 */
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public RetryCountFilter retryCountFilter() {
		return new RetryCountFilter();
	}
	
	
	/**
	 * 表单提交认证
	 *
	 * @return
	 * @throws Exception
	 */
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
		properties.getWhiteList().add("/login");
		UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		authenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
		authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(properties.getLoginUrl(), "POST"));
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		authenticationFilter.setRsaEncrypted(properties.isPasswordEncrypted());
		return authenticationFilter;
	}
	
	
	/**
	 * 表单登录成功后在这里生成token
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public AuthenticationFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "loser.security", name = "auth-center-enabled", havingValue = "true", matchIfMissing = false)
	public AccessTokenService accessTokenService() {
		return new AccessTokenService();
	}
	
	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new LogoutSuccessHandler();
	}
	
	/**
	 * 这些URL不需要认证就可以访问
	 *
	 * @return
	 */
	private String[] anonymousUrls() {
		List<String> whiteList = properties.getWhiteList();
		whiteList.add(properties.getLoginUrl());
		
		/*
		 * 如果集成了图片验证码功能的话, 要把图片验证码的URL也添加到白名单里面
		 */
		if (properties.getPicCode() != null && properties.getPicCode().isEnabled()) {
			whiteList.add(PIC_CODE_URL);
		}
		return whiteList.toArray(new String[whiteList.size()]);
	}
	
}
