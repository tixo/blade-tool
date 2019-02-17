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
package org.springblade.core.cloud.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Hystrix Headers 配置
 *
 * @author Chill
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("blade.hystrix.headers")
public class BladeHystrixHeadersProperties {
	/**
	 * 用于 聚合层 向调用层传递用户信息 的请求头，默认：X-BLADE-ACCOUNT
	 */
	private String account = "X-BLADE-ACCOUNT";
	/**
	 * RestTemplate 和 Feign 透传到下层的 Headers 名称表达式
	 */
	@Nullable
	private String pattern = "BLADE*";
	/**
	 * RestTemplate 和 Feign 透传到下层的 Headers 名称列表
	 */
	private List<String> allowed = Arrays.asList("X-Real-IP", "x-forwarded-for", "Authorization", "blade-auth");
}
