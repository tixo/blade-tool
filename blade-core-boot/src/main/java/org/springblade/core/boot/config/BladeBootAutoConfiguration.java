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
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.tool.constant.SystemConstant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * blade自动配置类
 *
 * @author Chill
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(BladeProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@AllArgsConstructor
public class BladeBootAutoConfiguration {

	private BladeProperties bladeProperties;

	/**
	 * 全局变量定义
	 */
	@Bean
	public SystemConstant fileConst() {
		SystemConstant me = SystemConstant.me();

		//设定开发模式
		me.setDevMode(("dev".equals(bladeProperties.getEnv())));

		//设定文件上传远程地址
		me.setDomain(bladeProperties.get("upload-domain", "http://localhost:8888"));

		//设定文件上传是否为远程模式
		me.setRemoteMode(bladeProperties.getBoolean("remote-mode", true));

		//远程上传地址
		me.setRemotePath(bladeProperties.get("remote-path", System.getProperty("user.dir") + "/work/blade"));

		//设定文件上传头文件夹
		me.setUploadPath(bladeProperties.get("upload-path", "/upload"));

		//设定文件下载头文件夹
		me.setDownloadPath(bladeProperties.get("download-path", "/download"));

		//设定上传图片是否压缩
		me.setCompress(bladeProperties.getBoolean("compress", false));

		//设定上传图片压缩比例
		me.setCompressScale(bladeProperties.getDouble("compress-scale", 2.00));

		//设定上传图片缩放选择:true放大;false缩小
		me.setCompressFlag(bladeProperties.getBoolean("compress-flag", false));

		return me;
	}

}
