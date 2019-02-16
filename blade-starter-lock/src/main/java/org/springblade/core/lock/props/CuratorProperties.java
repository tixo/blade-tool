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
package org.springblade.core.lock.props;

import lombok.Getter;
import lombok.Setter;
import org.springblade.core.launch.constant.ZookeeperConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类
 *
 * @author Chill
 */
@Getter
@Setter
@ConfigurationProperties("blade.lock")
public class CuratorProperties {
	private int retryCount = 5;
	private int elapsedTimeMs = 5000;
	private int sessionTimeoutMs = 60000;
	private int connectionTimeoutMs = 5000;
	private String connectString = ZookeeperConstant.ZOOKEEPER_CONNECT_STRING;
}
