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
package org.springblade.core.cloud.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import lombok.AllArgsConstructor;
import org.springblade.core.cloud.props.BladeHystrixHeadersProperties;
import org.springframework.lang.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hystrix传递ThreaLocal中的一些变量
 *
 * <p>
 * https://github.com/Netflix/Hystrix/issues/92#issuecomment-260548068
 * https://github.com/spring-cloud/spring-cloud-sleuth/issues/39
 * https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-core/src/main/java/org/springframework/cloud/netflix/hystrix/security
 * https://github.com/spring-projects/spring-security/blob/master/core/src/main/java/org/springframework/security/concurrent/DelegatingSecurityContextCallable.java
 * </p>
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
	@Nullable
	private final HystrixConcurrencyStrategy existingConcurrencyStrategy;
	@Nullable
	private final BladeHystrixAccountGetter accountGetter;
	private final BladeHystrixHeadersProperties properties;

	@Override
	public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
		return existingConcurrencyStrategy != null
			? existingConcurrencyStrategy.getBlockingQueue(maxQueueSize)
			: super.getBlockingQueue(maxQueueSize);
	}

	@Override
	public <T> HystrixRequestVariable<T> getRequestVariable(
		HystrixRequestVariableLifecycle<T> rv) {
		return existingConcurrencyStrategy != null
			? existingConcurrencyStrategy.getRequestVariable(rv)
			: super.getRequestVariable(rv);
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
											HystrixProperty<Integer> corePoolSize,
											HystrixProperty<Integer> maximumPoolSize,
											HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
											BlockingQueue<Runnable> workQueue) {
		return existingConcurrencyStrategy != null
			? existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue)
			: super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
		return existingConcurrencyStrategy != null
			? existingConcurrencyStrategy.getThreadPool(threadPoolKey, threadPoolProperties)
			: super.getThreadPool(threadPoolKey, threadPoolProperties);
	}

	@Override
	public <T> Callable<T> wrapCallable(Callable<T> callable) {
		Callable<T> wrapCallable = new BladeHttpHeadersCallable<>(callable, accountGetter, properties);
		return existingConcurrencyStrategy != null
			? existingConcurrencyStrategy.wrapCallable(wrapCallable)
			: super.wrapCallable(wrapCallable);
	}
}
