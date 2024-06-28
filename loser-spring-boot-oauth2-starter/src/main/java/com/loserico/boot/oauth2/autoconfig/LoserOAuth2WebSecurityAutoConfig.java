package com.loserico.boot.oauth2.autoconfig;

import com.loserico.oauth2.advice.TokenEndpointLoggerAspect;
import com.loserico.oauth2.endpoint.Oauth2AuthenticationentryPoint;
import com.loserico.oauth2.handler.LoginFailureHandler;
import com.loserico.oauth2.listener.AuthenticationFailureListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class LoserOAuth2WebSecurityAutoConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private Oauth2AuthenticationentryPoint oauth2AuthenticationentryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                        .failureHandler(loginFailureHandler())
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(oauth2AuthenticationentryPoint));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
