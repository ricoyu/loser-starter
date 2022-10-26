package com.loserico.boot.oauth2.autoconfig;

import com.loserico.boot.oauth2.properties.LoserOauth2Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright: (C), 2022-10-25 8:23
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(LoserOauth2Properties.class)
@ConditionalOnMissingBean(AuthorizationServerConfigurerAdapter.class)
public class LoserOauth2AuthorizationServerAutoConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private LoserOauth2Properties loserOauth2Properties;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired(required = false)
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	@Autowired
	private List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
	
	@Bean
	public TokenStore tokenStore() {
		if (loserOauth2Properties.getJwt().isEnabled()) {
			return new JwtTokenStore(jwtAccessTokenConverter());
		} else {
			return new RedisTokenStore(redisConnectionFactory);
		}
	}
	
	
	@Bean
	@ConditionalOnProperty(value = "loser.oauth2.jwt.enabled", matchIfMissing = false)
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		//jwt的密钥(用来保证jwt字符串的安全性, jwt可以防止篡改但是不能防窃听, 所以jwt不要放敏感信息)
		converter.setKeyPair(keyPair());
		return converter;
	}
	
	/**
	 * KeyPair是非对称加密的公钥和私钥的保存者
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(value = "loser.oauth2.jwt.enabled", matchIfMissing = false)
	public KeyPair keyPair() {
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
		return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//这这里，认证服务器委托一个AuthenticationManager 来验证我们的用户信息
		//这里必须配一下, 不然用refresh_token换acces_token会报没有defaultUserDetailsService
		endpoints.userDetailsService(userDetailsService);
		endpoints.authenticationManager(authenticationManager);
		if (loserOauth2Properties.getJwt().isEnabled()) {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
			endpoints.tokenStore(tokenStore())
					.tokenEnhancer(tokenEnhancerChain);
		}
	}
	
	/**
	 * 把第三方客户端信息存储到db中
	 *
	 * @param clients the client details configurer
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}
	
	/**
	 * 第三方客户端读取组件 专门用于读取oauth_client_details
	 *
	 * @return
	 */
	public ClientDetailsService clientDetailsService() {
		return new JdbcClientDetailsService(dataSource);
	}
	
	/**
	 * 表示的资源服务器 校验token的时候需要干什么(这里表示需要带入自己appid,和app secret)来进行验证
	 *
	 * @param security a fluent configurer for security features
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.checkTokenAccess("isAuthenticated()")
				.tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret
	}
	
}
