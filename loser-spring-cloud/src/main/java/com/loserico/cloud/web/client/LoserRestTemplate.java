package com.loserico.cloud.web.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
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
 * 支持在服务启动阶段从注册中心获取服务地址进行调用的版本
 * <p>
 * Copyright: (C), 2020/5/22 8:48
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@Slf4j
public class LoserRestTemplate extends RestTemplate {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	public <T> T doExecute(URI url,
	                          @Nullable HttpMethod method,
	                          @Nullable RequestCallback requestCallback,
	                          @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
		
		Assert.notNull(url, "URI is required");
		Assert.notNull(method, "HttpMethod is required");
		ClientHttpResponse response = null;
		try {
			
			log.info("请求的url路径为:{}", url);
			//把服务名替换成我们的IP
			url = replaceUrl(url);
			
			log.info("替换后的路径:{}", url);
			
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			return (responseExtractor != null ? responseExtractor.extractData(response) : null);
		} catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + resource + "\": " + ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	
	/**
	 * 从注册中心根据微服务名称拉取对应IP
	 *
	 * @param url
	 * @return URI
	 */
	private URI replaceUrl(URI url) {
		//1:从URI中解析调用的serviceName部分
		String serviceName = url.getHost();
		log.info("调用微服务的名称:{}", serviceName);
		
		//2:解析我们的请求路径 reqPath=/selectProductInfoById/1
		String reqPath = url.getPath();
		log.info("请求path:{}", reqPath);
		
		//通过微服务的名称去nacos服务端获取对应的实例列表
		List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceName);
		if (serviceInstanceList.isEmpty()) {
			throw new RuntimeException("没有可用的微服务实例列表:" + serviceName);
		}
		
		String serviceIp = chooseTargetIp(serviceInstanceList);
		
		String source = serviceIp + reqPath;
		try {
			return new URI(source);
		} catch (URISyntaxException e) {
			log.error("根据source:{} 构建URI异常", source);
		}
		return url;
	}
	
	/**
	 * 从服务列表中 随机选举一个ip
	 *
	 * @param serviceInstanceList
	 * @return
	 */
	private String chooseTargetIp(List<ServiceInstance> serviceInstanceList) {
		//采取随机的获取一个
		Random random = new Random();
		Integer randomIndex = random.nextInt(serviceInstanceList.size());
		String serviceIp = serviceInstanceList.get(randomIndex).getUri().toString();
		log.info("随机选取的服务IP:{}", serviceIp);
		return serviceIp;
	}
	
}
