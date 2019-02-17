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

package org.springblade.core.http.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Http Cache 配置
 *
 * @author L.cm
 */
@ConfigurationProperties("blade.http.cache")
public class BladeHttpCacheProperties {
	/**
	 * Http-cache 的 spring cache名，默认：bladeHttpCache
	 */
	@Getter
	@Setter
	private String cacheName = "bladeHttpCache";
	/**
	 * 默认拦截/**
	 */
	@Getter
	private final List<String> includePatterns = new ArrayList<String>() {{
		add("/**");
	}};
	/**
	 * 默认排除静态文件目录
	 */
	@Getter
	private final List<String> excludePatterns = new ArrayList<>();
}
