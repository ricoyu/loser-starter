package com.loserico.cloud.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

/**
 * 是为了实现服务启动的时候从授权中心拉取公钥
 * 不能用@loadBalanced注解的RestTemplate, 因为在容器启动过程中这个RestTemplate还没有准备好, 只能自己实现一个
 * <p>
 * Copyright: (C), 2022-10-27 10:00
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class LoserRestTemplate extends RestTemplate {
	
	private DiscoveryClient discoveryClient;
	
	private Logger log = LoggerFactory.getLogger(LoserRestTemplate.class);
	
	public LoserRestTemplate(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}
	
	@Override
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
		Assert.notNull(url, "URI is required");
		Assert.notNull(method, "HttpMethod is required");
		ClientHttpResponse response = null;
		try {
			//判断url的拦截路径,然后去Nacos获取地址随机选取一个
			log.info("请求的url路径为:{}", url);
			url = replaceUrl(url);
			log.info("替换后的路径:{}",url);
			
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			
			response = request.execute();
			handleResponse(url, method, response);
			return (responseExtractor != null ? responseExtractor.extractData(response) : null);
			
		} catch (IOException e) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException("I/O error on " + method.name() + " request for \"" + resource + "\": " + e.getMessage(), e);
		}
	}
	
	private URI replaceUrl(URI url) {
		String sourceUrl = url.toString();
		String[] urlParts = sourceUrl.split("//");
		int index = urlParts[1].replaceFirst("/", "@").indexOf("@");
		String serviceName = urlParts[1].substring(0, index);
		
		//通过微服务的名称去nacos服务端获取 对应的实例列表
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
		if (instances.isEmpty()) {
			throw new RuntimeException("没有可用的微服务实例列表:" + serviceName);
		}
		
		//采取随机的获取一个
		Random random = new Random();
		int randomIndex = random.nextInt(instances.size());
		log.info("随机下标:{}", randomIndex);
		String serviceIp = instances.get(randomIndex).getUri().toString();
		log.info("随机选择的服务IP:{}", serviceIp);
		String targetSource = urlParts[1].replace(serviceName, serviceIp);
		try {
			return new URI(targetSource);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return url;
	}
}
