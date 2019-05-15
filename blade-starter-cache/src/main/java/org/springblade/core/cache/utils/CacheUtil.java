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
package org.springblade.core.cache.utils;

import lombok.SneakyThrows;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * 缓存工具类
 *
 * @author Chill
 */
public class CacheUtil {

	private static CacheManager cacheManager = SpringUtil.getBean(CacheManager.class);

	/**
	 * 获取缓存对象
	 *
	 * @param cacheName 缓存名
	 * @return Cache
	 */
	public static Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @return
	 */
	@Nullable
	public static Object get(String cacheName, String keyPrefix, Object key) {
		if (Func.hasEmpty(cacheName, keyPrefix, key)) {
			return null;
		}
		return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key))).get();
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @param type
	 * @param <T>
	 * @return
	 */
	@Nullable
	public static <T> T get(String cacheName, String keyPrefix, Object key, @Nullable Class<T> type) {
		if (Func.hasEmpty(cacheName, keyPrefix, key)) {
			return null;
		}
		return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)), type);
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName   缓存名
	 * @param keyPrefix   缓存键前缀
	 * @param key         缓存键值
	 * @param valueLoader 重载对象
	 * @param <T>
	 * @return
	 */
	@Nullable
	@SneakyThrows
	public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader) {
		if (Func.hasEmpty(cacheName, keyPrefix, key, valueLoader.call())) {
			return null;
		}
		return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)), valueLoader);
	}

	/**
	 * 设置缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @param value     缓存值
	 */
	public static void put(String cacheName, String keyPrefix, Object key, @Nullable Object value) {
		getCache(cacheName).put(keyPrefix.concat(String.valueOf(key)), value);
	}

}
