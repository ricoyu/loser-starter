package com.loserico.boot.security.processor;

/**
 * 如果账号是第一次登录, 执行一些特殊逻辑, 如提示用户修改密码之类
 * <p>
 * Copyright: (C), 2021-06-15 11:52
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public interface FirstLoginProcessor {
	
	public void process(String username);
}
