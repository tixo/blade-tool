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
package org.springblade.core.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多租户配置
 *
 * @author Chill
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "blade.tenant")
public class BladeTenantProperties {

	/**
	 * 多租户字段名称
	 */
	private String column = "tenant_code";

	/**
	 * 多租户数据表
	 */
	private List<String> tables = new ArrayList<>();

	/**
	 * 多租户系统数据表
	 */
	private List<String> bladeTables = Arrays.asList("blade_notice", "blade_log_api", "blade_log_error", "blade_log_usual", "blade_oss");
}
