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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

/**
 * Http Cache 服务
 *
 * @author L.cm
 */
public class HttpCacheService implements InitializingBean {
	private final BladeHttpCacheProperties properties;
	private final CacheManager cacheManager;
	private Cache cache;

	public HttpCacheService(BladeHttpCacheProperties properties, CacheManager cacheManager) {
		this.properties = properties;
		this.cacheManager = cacheManager;
	}

	public boolean get(String key) {
		Boolean result = cache.get(key, Boolean.class);
		return result != null && Boolean.TRUE.equals(result);
	}

	public void set(String key) {
		cache.put(key, Boolean.TRUE);
	}

	public void remove(String key) {
		cache.evict(key);
	}

	public void clear() {
		cache.clear();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(cacheManager, "cacheManager must not be null!");
		String cacheName = properties.getCacheName();
		this.cache = cacheManager.getCache(cacheName);
		Assert.notNull(this.cache, "HttpCacheCache cacheName: " + cacheName + " is not config.");
	}
}
