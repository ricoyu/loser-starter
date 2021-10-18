package com.loserico.boot.security.processor;

/**
 * <p>
 * Copyright: (C), 2021-10-12 15:09
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface LoginSuccessPostProcessor {
	
	public void onSuccess(String username);
}
