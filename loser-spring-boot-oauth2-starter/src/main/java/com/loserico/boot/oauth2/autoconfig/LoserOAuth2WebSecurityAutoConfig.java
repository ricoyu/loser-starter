package com.loserico.boot.oauth2.autoconfig;

import com.loserico.oauth2.advice.TokenEndpointLoggerAspect;
import com.loserico.oauth2.endpoint.Oauth2AuthenticationentryPoint;
import com.loserico.oauth2.handler.LoginFailureHandler;
import com.loserico.oauth2.listener.AuthenticationFailureListener;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 这个配置类不能用@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class), 因为Spring Security Oauth包会自动引入一个Bean
 * AuthorizationServerSecurityConfiguration, 他是WebSecurityConfigurerAdapter的子类
 * <p>
 * Copyright: Copyright (c) 2022-10-03 10:22
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class LoserOAuth2WebSecurityAutoConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.formLogin()
				.and()
				.authorizeRequests()
				.anyRequest()
				.authenticated()
				.and()
				.csrf().disable().cors();
		//配置 BasicAuthenticationFilter
		http.httpBasic().authenticationEntryPoint(oauth2AuthenticationentryPoint());
	}
	
	
	
	/**
	 * configure方法配置了AuthenticationManager, 这里就将其暴露为一个Spring Bean
	 * 这个类是用来做用户名/密码登录的
	 * <p>
	 * 问题: bean名字配成authenticationManager的话, 调用 /oauth/token 会抛StackoverflowException
	 * 名字改为authenticationManagerBean就没问题
	 *
	 * @return AuthenticationManager
	 */
	@SneakyThrows
	@Bean
	public AuthenticationManager authenticationManagerBean() {
		return super.authenticationManagerBean();
	}
	
	/**
	 * 带随机盐的加密器
	 *
	 * @return PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * 用来打印error log的
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(TokenEndpointLoggerAspect.class)
	public TokenEndpointLoggerAspect tokenEndpointLoggerAspect() {
		return new TokenEndpointLoggerAspect();
	}
	
	@Bean
	public AuthenticationFailureListener authenticationFailureListener() {
		return new AuthenticationFailureListener();
	}
	
	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}
	
	@Bean
	public Oauth2AuthenticationentryPoint oauth2AuthenticationentryPoint() {
		return new Oauth2AuthenticationentryPoint();
	}
}
