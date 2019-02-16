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
package org.springblade.core.log4j2;

import org.springblade.core.auto.service.AutoService;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 日志启动器
 *
 * @author L.cm
 */
@AutoService(LauncherService.class)
public class LogLauncherServiceImpl implements LauncherService {

	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev) {
		System.setProperty("logging.config", String.format("classpath:log/log4j2_%s.xml", profile));
		// RocketMQ-Client 4.2.0 Log4j2 配置文件冲突问题解决：https://www.jianshu.com/p/b30ae6dd3811
		System.setProperty("rocketmq.client.log.loadconfig", "false");
		//  RocketMQ-Client 4.3 设置默认为 slf4j
		System.setProperty("rocketmq.client.logUseSlf4j", "true");
		// 非本地 将 全部的 System.err 和 System.out 替换为log
		if (!isLocalDev) {
			System.setOut(LogPrintStream.out());
			System.setErr(LogPrintStream.err());
		}
	}

}
