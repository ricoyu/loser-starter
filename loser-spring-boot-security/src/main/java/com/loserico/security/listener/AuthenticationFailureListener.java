package com.loserico.security.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

/**
 * <p>
 * Copyright: (C), 2022-10-12 18:20
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		log.error("", event.getException());
	}
}
