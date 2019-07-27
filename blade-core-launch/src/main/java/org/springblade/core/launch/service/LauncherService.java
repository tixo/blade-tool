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
package org.springblade.core.launch.service;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

/**
 * launcher 扩展 用于一些组件发现
 *
 * @author Chill
 */
public interface LauncherService extends Ordered, Comparable<LauncherService> {

	/**
	 * 启动时 处理 SpringApplicationBuilder
	 *
	 * @param builder    SpringApplicationBuilder
	 * @param appName    SpringApplicationAppName
	 * @param profile    SpringApplicationProfile
	 * @param isLocalDev SpringApplicationIsLocalDev
	 */
	void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev);

	/**
	 * 获取排列顺序
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return 0;
	}

	/**
	 * 对比排序
	 *
	 * @param o LauncherService
	 * @return compare
	 */
	@Override
	default int compareTo(LauncherService o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}

}
