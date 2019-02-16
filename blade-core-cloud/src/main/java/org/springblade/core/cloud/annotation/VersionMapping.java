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

package org.springblade.core.cloud.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * 版本号处理
 *
 * <p>
 *     1. url 版本号：添加到 url 前
 *     2. Accept 版本：application/vnd.mica.VERSION+json
 * </p>
 *
 * @author L.cm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
@UrlVersion
@ApiVersion
@Validated
public @interface VersionMapping {
	/**
	 * Alias for {@link RequestMapping#name}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String name() default "";

	/**
	 * Alias for {@link RequestMapping#value}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};

	/**
	 * Alias for {@link RequestMapping#path}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] path() default {};

	/**
	 * Alias for {@link RequestMapping#params}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] params() default {};

	/**
	 * Alias for {@link RequestMapping#headers}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] headers() default {};

	/**
	 * Alias for {@link RequestMapping#consumes}.
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] consumes() default {};

	/**
	 * Alias for {@link RequestMapping#produces}.
	 * default json utf-8
	 * @return {String[]}
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] produces() default {};

	/**
	 * Alias for {@link UrlVersion#value}.
	 * @return {String}
	 */
	@AliasFor(annotation = UrlVersion.class, attribute = "value")
	String urlVersion() default "";

	/**
	 * Alias for {@link ApiVersion#value}.
	 * @return {String}
	 */
	@AliasFor(annotation = ApiVersion.class, attribute = "value")
	String apiVersion() default "";

}
