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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;
import feign.hystrix.FallbackFactory;
import feign.hystrix.SetterFactory;
import org.springframework.lang.Nullable;
import rx.Completable;
import rx.Observable;
import rx.Single;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static feign.Util.checkNotNull;

/**
 * Hystrix处理类
 *
 * @author Chill
 */
final class BladeHystrixInvocationHandler implements InvocationHandler {

	private final Target<?> target;
	private final Map<Method, MethodHandler> dispatch;
	@Nullable
	private final FallbackFactory<?> fallbackFactory;
	private final Map<Method, Method> fallbackMethodMap;
	private final Map<Method, Setter> setterMethodMap;
	private final String EQUALS = "equals";
	private final String HASH_CODE = "hashCode";
	private final String TO_STRING = "toString";

	BladeHystrixInvocationHandler(
		Target<?> target, Map<Method,
		MethodHandler> dispatch,
		SetterFactory setterFactory,
		@Nullable FallbackFactory<?> fallbackFactory) {
		this.target = checkNotNull(target, "target");
		this.dispatch = checkNotNull(dispatch, "dispatch");
		this.fallbackFactory = fallbackFactory;
		this.fallbackMethodMap = toFallbackMethod(dispatch);
		this.setterMethodMap = toSetters(setterFactory, target, dispatch.keySet());
	}

	/**
	 * If the method param of InvocationHandler.invoke is not accessible, i.e in a package-private
	 * interface, the fallback call in hystrix command will fail cause of access restrictions. But
	 * methods in dispatch are copied methods. So setting access to dispatch method doesn't take
	 * effect to the method in InvocationHandler.invoke. Use map to store a copy of method to invoke
	 * the fallback to bypass this and reducing the count of reflection calls.
	 *
	 * @return cached methods map for fallback invoking
	 */
	private static Map<Method, Method> toFallbackMethod(Map<Method, MethodHandler> dispatch) {
		Map<Method, Method> result = new LinkedHashMap<>(dispatch.size());
		for (Method method : dispatch.keySet()) {
			method.setAccessible(true);
			result.put(method, method);
		}
		return result;
	}

	/**
	 * Process all methods in the target so that appropriate setters are created.
	 */
	private static Map<Method, Setter> toSetters(
		SetterFactory setterFactory, Target<?> target, Set<Method> methods) {
		Map<Method, Setter> result = new LinkedHashMap<>(methods.size());
		for (Method method : methods) {
			method.setAccessible(true);
			result.put(method, setterFactory.create(target, method));
		}
		return result;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		// early exit if the invoked method is from java.lang.Object
		// code is the same as ReflectiveFeign.FeignInvocationHandler
		if (EQUALS.equals(method.getName())) {
			try {
				Object otherHandler = args.length > 0 && args[0] != null
					? Proxy.getInvocationHandler(args[0]) : null;
				return equals(otherHandler);
			} catch (IllegalArgumentException e) {
				return false;
			}
		} else if (HASH_CODE.equals(method.getName())) {
			return hashCode();
		} else if (TO_STRING.equals(method.getName())) {
			return toString();
		}

		HystrixCommand<Object> hystrixCommand = new HystrixCommand<Object>(setterMethodMap.get(method)) {
			@Override
			protected Object run() throws Exception {
				try {
					return BladeHystrixInvocationHandler.this.dispatch.get(method).invoke(args);
				} catch (Exception e) {
					throw e;
				} catch (Throwable t) {
					throw (Error) t;
				}
			}

			@Override
			@Nullable
			@SuppressWarnings("unchecked")
			protected Object getFallback() {
				final FallbackFactory<?> localFallbackFactory = fallbackFactory == null ? BladeFallbackFactory.INSTANCE : fallbackFactory;
				Object fallback;
				try {
					if (localFallbackFactory instanceof BladeFallbackFactory) {
						fallback = ((BladeFallbackFactory) localFallbackFactory).create(target.type(), getExecutionException());
					} else {
						fallback = localFallbackFactory.create(getExecutionException());
					}
					Object result = fallbackMethodMap.get(method).invoke(fallback, args);
					if (isReturnsHystrixCommand(method)) {
						return ((HystrixCommand) result).execute();
					} else if (isReturnsObservable(method)) {
						// Create a cold Observable
						return ((Observable) result).toBlocking().first();
					} else if (isReturnsSingle(method)) {
						// Create a cold Observable as a Single
						return ((Single) result).toObservable().toBlocking().first();
					} else if (isReturnsCompletable(method)) {
						((Completable) result).await();
						return null;
					} else {
						return result;
					}
				} catch (IllegalAccessException e) {
					// shouldn't happen as method is public due to being an interface
					throw new AssertionError(e);
				} catch (InvocationTargetException e) {
					// Exceptions on fallback are tossed by Hystrix
					throw new AssertionError(e.getCause());
				}
			}
		};

		if (isReturnsHystrixCommand(method)) {
			return hystrixCommand;
		} else if (isReturnsObservable(method)) {
			// Create a cold Observable
			return hystrixCommand.toObservable();
		} else if (isReturnsSingle(method)) {
			// Create a cold Observable as a Single
			return hystrixCommand.toObservable().toSingle();
		} else if (isReturnsCompletable(method)) {
			return hystrixCommand.toObservable().toCompletable();
		}
		return hystrixCommand.execute();
	}

	private boolean isReturnsCompletable(Method method) {
		return Completable.class.isAssignableFrom(method.getReturnType());
	}

	private boolean isReturnsHystrixCommand(Method method) {
		return HystrixCommand.class.isAssignableFrom(method.getReturnType());
	}

	private boolean isReturnsObservable(Method method) {
		return Observable.class.isAssignableFrom(method.getReturnType());
	}

	private boolean isReturnsSingle(Method method) {
		return Single.class.isAssignableFrom(method.getReturnType());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BladeHystrixInvocationHandler) {
			BladeHystrixInvocationHandler other = (BladeHystrixInvocationHandler) obj;
			return target.equals(other.target);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public String toString() {
		return target.toString();
	}
}
