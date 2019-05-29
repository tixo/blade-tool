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

package org.springblade.core.test;


import org.junit.runners.model.InitializationError;
import org.springblade.core.launch.BladeApplication;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.launch.constant.NacosConstant;
import org.springblade.core.launch.constant.SentinelConstant;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设置启动参数
 *
 * @author L.cm
 */
public class BladeSpringRunner extends SpringJUnit4ClassRunner {

	public BladeSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		setUpTestClass(clazz);
	}

	private void setUpTestClass(Class<?> clazz) {
		BladeBootTest bladeBootTest = AnnotationUtils.getAnnotation(clazz, BladeBootTest.class);
		if (bladeBootTest == null) {
			throw new BladeBootTestException(String.format("%s must be @BladeBootTest .", clazz));
		}
		String appName = bladeBootTest.appName();
		String profile = bladeBootTest.profile();
		boolean isLocalDev = BladeApplication.isLocalDev();
		Properties props = System.getProperties();
		props.setProperty("blade.env", profile);
		props.setProperty("blade.name", appName);
		props.setProperty("blade.is-local", String.valueOf(isLocalDev));
		props.setProperty("blade.dev-mode", profile.equals(AppConstant.PROD_CODE) ? "false" : "true");
		props.setProperty("blade.service.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("spring.application.name", appName);
		props.setProperty("spring.profiles.active", profile);
		props.setProperty("info.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("info.desc", appName);
		props.setProperty("spring.cloud.nacos.discovery.server-addr", NacosConstant.NACOS_ADDR);
		props.setProperty("spring.cloud.nacos.config.server-addr", NacosConstant.NACOS_ADDR);
		props.setProperty("spring.cloud.nacos.config.prefix", NacosConstant.NACOS_CONFIG_PREFIX);
		props.setProperty("spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
		props.setProperty("spring.cloud.sentinel.transport.dashboard", SentinelConstant.SENTINEL_ADDR);
		props.setProperty("spring.main.allow-bean-definition-overriding", "true");
		// 加载自定义组件
		if (bladeBootTest.enableLoader()) {
			SpringApplicationBuilder builder = new SpringApplicationBuilder(clazz);
			List<LauncherService> launcherList = new ArrayList<>();
			ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
			launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())
				.forEach(launcherService -> launcherService.launcher(builder, appName, profile, isLocalDev));
		}
		System.err.println(String.format("---[junit.test]:[%s]---启动中，读取到的环境变量:[%s]", appName, profile));
	}

}
