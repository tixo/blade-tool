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
package org.springblade.core.cloud.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.lang.Nullable;

/**
 * 默认 Fallback，避免写过多fallback类
 *
 * @param <T> 泛型标记
 * @author Chill
 */
@Slf4j(topic = "FeignFallBack")
public final class BladeFallbackFactory<T> implements FallbackFactory<T> {
	public static final BladeFallbackFactory INSTANCE = new BladeFallbackFactory();

	private BladeFallbackFactory() {
	}

	@SuppressWarnings("unchecked")
	T create(final Class<?> type, final Throwable cause) {
		// 重写 Feign ErrorDecoder，message 知己为 body 数据，反序列化为 R
		final R result = cause instanceof BladeFeignException ? ((BladeFeignException) cause).getResult() : R.fail(ResultCode.INTERNAL_SERVER_ERROR, cause.getMessage());
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		enhancer.setUseCache(true);
		enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
			log.error("Fallback class:[{}] method:[{}] message:[{}]", type.getName(), method.getName(), cause.getMessage());
			return result;
		});
		return (T) enhancer.create();
	}

	@Nullable
	@Override
	public T create(Throwable cause) {
		return null;
	}
}
