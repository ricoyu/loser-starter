package com.loserico.boot.security.filter;

import com.loserico.boot.security.errors.SecurityErrors;
import com.loserico.boot.security.processor.RetryCountMessageProcessor;
import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.boot.security.service.RetryCountService;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.errors.ErrorTypes;
import com.loserico.common.lang.vo.Results;
import com.loserico.common.spring.utils.ServletUtils;
import com.loserico.security.constants.LoserSecurityConstants;
import com.loserico.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.loserico.boot.security.constants.SecurityConstants.RETRY_COUNT_KEY_PREFIX;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class RetryCountFilter extends OncePerRequestFilter {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@Autowired(required = false)
	private RetryCountMessageProcessor retryCountMessageProcessor;
	
	@Autowired(required = false)
	private RetryCountService retryCountService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		//只有调用登录接口才需要执行过滤
		if (!ServletUtils.pathMatch(request, properties.getLoginUrl())) {
			chain.doFilter(request, response);
			return;
		}
		
		String username = request.getParameter(LoserSecurityConstants.SPRING_SECURITY_FORM_USERNAME_KEY);
		if (isBlank(username)) {
			log.info("没有输入用户名 {}", username);
			RestUtils.writeJson(response, Results.status(ErrorTypes.USERNAME_PASSWORD_MISMATCH).build());
			return;
		}
		
		/*
		 * 5次错误密码后冻结账号5分钟
		 */
		String retryCountKey = RETRY_COUNT_KEY_PREFIX + username;
		Long retryCount = JedisUtils.getLong(retryCountKey);
		int maxRetryCount = properties.getMaxRetries();
		if (retryCountService != null) {
			maxRetryCount = retryCountService.maxRetryCount(username);
		}
		
		log.info("Retry count [{}]", retryCount);
		if (retryCount != null && retryCount >= maxRetryCount) {
			log.info("超过登录失败次数限制 {}", retryCount);
			if (retryCountMessageProcessor != null) {
				//如果系统可以动态配置失败多少次冻结几分钟, 那么错误消息也要动态构建
				String message = retryCountMessageProcessor.retryCountExceeded(username);
				RestUtils.writeJson(response, Results.status(SecurityErrors.RETRY_COUNT_EXCEED.code(), message).build());
			} else {
				RestUtils.writeJson(response, Results.status(SecurityErrors.RETRY_COUNT_EXCEED).build());
			}
			return;
		}
		
		chain.doFilter(request, response);
	}
}
