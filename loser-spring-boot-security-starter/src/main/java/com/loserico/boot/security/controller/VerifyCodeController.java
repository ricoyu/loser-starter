package com.loserico.boot.security.controller;

import com.loserico.boot.security.constants.SecurityConstants;
import com.loserico.boot.security.props.LoserSecurityProperties;
import com.loserico.boot.security.props.LoserSecurityProperties.PicCode;
import com.loserico.cache.JedisUtils;
import com.loserico.common.lang.utils.StringUtils;
import com.loserico.common.lang.vo.Result;
import com.loserico.common.lang.vo.Results;
import com.loserico.security.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.loserico.boot.security.constants.SecurityConstants.PIC_CODE_URL;
import static com.loserico.boot.security.constants.SecurityConstants.VERIFY_CODE_PREFIX;
import static com.loserico.common.lang.utils.StringUtils.concat;
import static java.util.concurrent.TimeUnit.*;

/**
 * <p>
 * Copyright: (C), 2021-05-17 13:49
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
@RestController
public class VerifyCodeController {
	
	@Autowired
	private LoserSecurityProperties properties;
	
	@GetMapping(PIC_CODE_URL)
	public Result verificationCode() {
		//图片验证码唯一ID
		String codeId = StringUtils.uniqueKey(12);
		//生成随机字串
		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
		//生成图片
		String base64Encoded = VerifyCodeUtils.outputImage(verifyCode);
		
		PicCode picCode = properties.getPicCode();
		//放到Redis, 5分钟有效期
		JedisUtils.set(concat(VERIFY_CODE_PREFIX, codeId).toLowerCase(), verifyCode, picCode.getTtl(), MINUTES);
		
		Map<String, Object> results = new HashMap<>(2);
		results.put(SecurityConstants.VERIFY_CODE_ID, codeId);
		results.put(SecurityConstants.VERIFY_CODE, base64Encoded);
		
		return Results.success().result(results);
	}
	
}
