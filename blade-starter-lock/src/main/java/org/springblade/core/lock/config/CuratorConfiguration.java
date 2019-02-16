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
package org.springblade.core.lock.config;

import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springblade.core.lock.locker.BladeLocker;
import org.springblade.core.lock.props.CuratorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({
	CuratorProperties.class
})
public class CuratorConfiguration {

	CuratorProperties curatorProperties;

	@Bean(initMethod = "start")
	public CuratorFramework curatorFramework() {
		return CuratorFrameworkFactory.newClient(
			curatorProperties.getConnectString(),
			curatorProperties.getSessionTimeoutMs(),
			curatorProperties.getConnectionTimeoutMs(),
			new RetryNTimes(curatorProperties.getRetryCount(), curatorProperties.getElapsedTimeMs()));
	}

	@Bean
	public BladeLocker bladeLocker() {
		return new BladeLocker();
	}

}
