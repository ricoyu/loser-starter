package com.loserico.boot.es.autoconfig;

import com.loserico.common.lang.concurrent.Concurrent;
import com.loserico.common.lang.utils.IOUtils;
import com.loserico.common.lang.utils.StringUtils;
import com.loserico.search.ElasticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * <p>
 * Copyright: (C), 2020/4/14 16:22
 * <p>
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
@EnableConfigurationProperties({LoserESProperties.class})
@Configuration
@Slf4j
public class LoserESAutoConfiguration {
	
	private static final String WORKING_DIR = System.getProperty("user.dir");
	
	private static final String FILE_SEPRATOR = System.getProperty("file.separator");
	
	/**
	 * 从classpath读
	 */
	private static final String CLASSPATH_PREFIX = "classpath:";
	
	/**
	 * 从磁盘绝对路径去读
	 */
	private static final String FILE_SYSTEM_PREFIX = "/";
	
	/**
	 * 有这个符号表示配置了Windows的磁盘文件路径
	 */
	private static final String WINDOWS_FILE_SYSTEM = ":\\";
	
	@Autowired
	private LoserESProperties properties;
	
	@PostConstruct
	public void init() {
		//不需要启动时初始化Index Template
		if (!properties.isInit()) {
			return;
		}
		//用检查index存在与否来实现初始化客户端连接的目的
		ElasticUtils.Admin.existsIndex("ricoyu");
		
		String[] templates = properties.getTemplates();
		//没有配置templateName和templateFileName, 也不需要初始化
		if (templates == null || templates.length == 0) {
			return;
		}
		
		List<String[]> templatePair = new ArrayList<>();
		for (int i = 0; i < templates.length; i++) {
			//拆开templateName,templateFileName 
			String[] template = StringUtils.split(templates[i], ",");
			if (template.length != 2) {
				return;
			}
			String templateName = template[0];
			String templateFileName = template[1];
			
			if (isBlank(templateName) || isBlank(templateFileName)) {
				return;
			}
			
			String content = null;
			if (templateFileName.startsWith(CLASSPATH_PREFIX)) {
				content = IOUtils.readClassPathFileAsString(templateFileName);
			} else if (templateFileName.startsWith(FILE_SYSTEM_PREFIX) || templateFileName.contains(WINDOWS_FILE_SYSTEM)) {
				content = IOUtils.readFileAsString(templateFileName);
			} else {
				content = IOUtils.readFileAsString(WORKING_DIR + FILE_SEPRATOR + templateFileName);
			}
			templatePair.add(new String[]{templateName, content});
		}
		
		//配置初始化多个Index Template时走并发
		if (templatePair.size() > 1) {
			templatePair.forEach((nameAndContent) -> {
				Concurrent.execute(() -> ElasticUtils.Admin.putIndexTemplate(nameAndContent[0], nameAndContent[1]));
			});
			Concurrent.await();
		} else {
			String[] nameAndContent = templatePair.get(0);
			ElasticUtils.Admin.putIndexTemplate(nameAndContent[0], nameAndContent[1]);
		}
		log.info("Put Index Template Done!");
	}
}
