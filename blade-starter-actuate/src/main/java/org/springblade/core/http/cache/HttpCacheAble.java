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

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Http cache
 * cache-control
 * <p>
 * max-age 大于0 时 直接从游览器缓存中 提取
 * max-age 小于或等于0 时 向server 发送http 请求确认 ,该资源是否有修改
 *
 * @author L.cm
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpCacheAble {

	/**
	 * 缓存的时间,默认0,单位秒
	 *
	 * @return {long}
	 */
	@AliasFor("maxAge")
	long value();

	/**
	 * 缓存的时间,默认0,单位秒
	 *
	 * @return {long}
	 */
	@AliasFor("value")
	long maxAge() default 0;

}
