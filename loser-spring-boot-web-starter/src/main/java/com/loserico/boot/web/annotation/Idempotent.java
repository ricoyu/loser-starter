package com.loserico.boot.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 接口幂等性, 基于token机制实现
 *
 * <ol>
 *     <li/>客户端先发一个请求去获取 token, 请求uri /idempotent-token 服务端生成一个全局唯一的ID作为token保存在redis中, 同时把这个ID返回给客户端
 *     <li/>客户端第二次调用业务请求的时候必须携带这个 token, 放到Idempotent-Token 请求头里
 *     <li/>服务端会校验这个token, 如果校验成功, 则执行业务, 并删除redis中的token
 *     <li/>如果校验失败, 则表示重复操作, 直接返回指定的结果给客户端
 * </ol>
 * Token一小时默认过期, 不可以重复使用
 * <p>
 * Copyright: (C), 2022-01-21 16:28
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Idempotent {
}
