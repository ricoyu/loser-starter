package com.loserico.boot.security.service;

import com.loserico.common.lang.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *  
 * <p>
 * Copyright: Copyright (c) 2021-05-14 10:35
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@Component
public class AccessTokenService {

    public String getAccessToken() {
        String token = StringUtils.uniqueKey(66); //这是token

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getName(); //username
        log.info("用户[{}]成功获取token[{}]", username, token);
        return token;
    }
}
