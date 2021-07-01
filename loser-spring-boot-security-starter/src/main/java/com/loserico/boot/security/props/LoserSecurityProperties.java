package com.loserico.boot.security.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright: (C), 2021-04-01 16:06
 * <p>
 * <p>
 * Company: Information & Data Security Solutions Co., Ltd.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "loser.security")
public class LoserSecurityProperties {
	
	/**
	 * 认证中心提供基于用户名密码方式的登录功能, 如果启用, 用户需要注册一个UserDetailsService实现
	 */
	private boolean authCenterEnabled = false;
	
	/**
	 * 如果启动认证中心模式, 配置登录URL, 默认 /login
	 */
	private String loginUrl = "/login";
	
	/**
	 * 如果启动认证中心模式, 配置登出URL, 默认 /logout
	 */
	private String logoutUrl = "/logout";
	
	/**
	 * 登录失败允许的重试次数, 默认5次
	 */
	private Integer maxRetries = 5;
	
	/**
	 * 是否启动就执行过期token清理
	 */
	private boolean clearOnStart = false;
	
	/**
	 * Spring Security的RoleVoter在投票的时候, 会检查@Secured("ROLE_ADMIN")等注解上是否写了ROLE_前缀<p>
	 * 如果不想要加ROLE_前缀或者想配置另外一个前缀, 可以通过这个配置项来指定
	 */
	private String rolePrefix = "ROLE_";
	
	/**
	 * Token是否加密处理过<p>
	 * 客户端将请求的URI, access_toen, 客户端当前的timestamp用公钥加密, 将加密后的字符串作为access_token传递过来<p>
	 * 先组合成这样一个字符串 uri=/saleOrder/search&access_token=dHDG13ms4868gFNfuk&timestamp=123128371823<p>
	 * 在对这个字符串做RSA公钥加密, 加密后作为Authorization请求头传入, 同时请求的URL后面要加上timestamp=123128371823参数
	 */
	private boolean tokenEncrypted = true;
	
	/**
	 * 表单登录时, 传输的密码是否RSA公钥加密传输
	 */
	private boolean passwordEncrypted = true;
	
	/**
	 * SpringBoot应用的ContentPath或者是Nginx那边反向代理配置的location前缀
	 */
	private String contextPath;
	
	/**
	 * 指定的URI可以匿名访问
	 */
	private List<String> whiteList = new ArrayList<>();
	
	private PicCode picCode;
	
	/**
	 * 是否要对应用限流, 开启@RateLimit注解支持
	 */
	private boolean rateLimit = true;
	
	/**
	 * 是否要开启防止重复提交, 开始@AntiDupSubmit注解支持
	 */
	private boolean antiDuplicateSubmit = true;
	
	@Data
	public static class PicCode {
		
		/**
		 * 是否要集成验证码功能, 访问URL /pic-code
		 */
		private boolean enabled = false;
		
		/**
		 * 图片验证码多久过期, 默认5分钟
		 */
		private long ttl = 5;
	}
}
