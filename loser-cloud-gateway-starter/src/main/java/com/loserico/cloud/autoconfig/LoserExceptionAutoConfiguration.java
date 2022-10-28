package com.loserico.cloud.autoconfig;

import com.loserico.cloud.gateway.advice.GatewayExceptionHandlerAdvice;
import com.loserico.cloud.gateway.handler.LoserErrorWebExceptionHandler;
import com.loserico.cloud.gateway.properties.LoserGatewayExceptionProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * 网关异常处理自动配置
 * <p>
 * Copyright: Copyright (c) 2020-05-02 10:45
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 *
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class, LoserGatewayExceptionProperties.class})
public class LoserExceptionAutoConfiguration {
	
	private ServerProperties serverProperties;
	
	private ApplicationContext applicationContext;
	
	private ResourceProperties resourceProperties;
	
	private List<ViewResolver> viewResolvers;
	
	private ServerCodecConfigurer serverCodecConfigurer;
	
	public LoserExceptionAutoConfiguration(ServerProperties serverProperties,
	                                       ResourceProperties resourceProperties,
	                                       ObjectProvider<List<ViewResolver>> viewResolversProvider,
	                                       ServerCodecConfigurer serverCodecConfigurer,
	                                       ApplicationContext applicationContext) {
		this.serverProperties = serverProperties;
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;
		this.viewResolvers = viewResolversProvider.getIfAvailable(() -> Collections.emptyList());
		this.serverCodecConfigurer = serverCodecConfigurer;
	}
    
    /**
     * ErrorWebExceptionHandler把实际错误处理交给GatewayExceptionHandlerAdvice
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "gatewayExceptionHandlerAdvice")
    public GatewayExceptionHandlerAdvice gatewayExceptionHandlerAdvice() {
        return new GatewayExceptionHandlerAdvice();
    }
	
	@Bean
	public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
		DefaultErrorWebExceptionHandler exceptionHandler = new LoserErrorWebExceptionHandler(
				errorAttributes, this.resourceProperties,
				this.serverProperties.getError(), this.applicationContext);
		exceptionHandler.setViewResolvers(this.viewResolvers);
		exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}
}
