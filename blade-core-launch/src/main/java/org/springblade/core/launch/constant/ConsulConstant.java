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
package org.springblade.core.launch.constant;

/**
 * Consul常量.
 *
 * @author Chill
 */
public interface ConsulConstant {

	/**
	 * consul dev 地址
	 */
	String CONSUL_HOST = "http://localhost";

	/**
	 * consul端口
	 */
	String CONSUL_PORT = "8500";

	/**
	 * consul端口
	 */
	String CONSUL_CONFIG_FORMAT = "yaml";

	/**
	 * consul端口
	 */
	String CONSUL_WATCH_DELAY = "1000";

	/**
	 * consul端口
	 */
	String CONSUL_WATCH_ENABLED = "true";
}
