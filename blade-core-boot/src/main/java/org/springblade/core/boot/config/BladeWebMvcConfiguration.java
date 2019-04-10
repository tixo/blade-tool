/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
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
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.core.boot.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.props.BladeUploadProperties;
import org.springblade.core.boot.resolver.TokenArgumentResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WEB配置
 *
 * @author Chill
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@EnableConfigurationProperties({
	BladeUploadProperties.class
})
public class BladeWebMvcConfiguration implements WebMvcConfigurer {

	private final BladeUploadProperties bladeUploadProperties;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String path = bladeUploadProperties.getSavePath();
		registry.addResourceHandler("/upload/**")
			.addResourceLocations("file:" + path + "/upload/");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new TokenArgumentResolver());
	}

}
