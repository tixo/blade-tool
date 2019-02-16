/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */

package org.springblade.core.http.cache;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

/**
 * Http Cache 配置
 *
 * @author L.cm
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(BladeHttpCacheProperties.class)
@ConditionalOnProperty(value = "blade.http.cache.enabled", havingValue = "true")
public class HttpCacheConfiguration implements WebMvcConfigurer {
	private static final String DEFAULT_STATIC_PATH_PATTERN = "/**";
	private final WebMvcProperties webMvcProperties;
	private final BladeHttpCacheProperties properties;
	private final CacheManager cacheManager;

	@Bean
	public HttpCacheService httpCacheService() {
		return new HttpCacheService(properties, cacheManager);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Set<String> excludePatterns = new HashSet<>(properties.getExcludePatterns());
		String staticPathPattern = webMvcProperties.getStaticPathPattern();
		// 如果静态 目录 不为 /**
		if (!DEFAULT_STATIC_PATH_PATTERN.equals(staticPathPattern.trim())) {
			excludePatterns.add(staticPathPattern);
		}
		HttpCacheInterceptor httpCacheInterceptor = new HttpCacheInterceptor(httpCacheService());
		registry.addInterceptor(httpCacheInterceptor)
			.addPathPatterns(properties.getIncludePatterns().toArray(new String[0]))
			.excludePathPatterns(excludePatterns.toArray(new String[0]));
	}
}
