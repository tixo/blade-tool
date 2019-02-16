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
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixDelegatingContract;
import feign.hystrix.SetterFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 自定义 Hystrix Feign 实现默认 fallBack
 *
 * @author Chill
 */
public class BladeHystrixFeign {

	public static Builder builder(FeignContext feignContext) {
		return new Builder(feignContext);
	}

	public static final class Builder extends Feign.Builder {
		private Contract contract = new Contract.Default();
		private SetterFactory setterFactory = new SetterFactory.Default();
		private final FeignContext feignContext;

		public Builder(FeignContext feignContext) {
			this.feignContext = feignContext;
		}

		/**
		 * Allows you to override hystrix properties such as thread pools and command keys.
		 *
		 * @param setterFactory setterFactory
		 * @return BladeHystrixFeign Builder
		 */
		public Builder setterFactory(SetterFactory setterFactory) {
			this.setterFactory = setterFactory;
			return this;
		}

		@Override
		public <T> T target(Target<T> target) {
			Class<T> targetType = target.type();
			FeignClient feignClient = AnnotatedElementUtils.getMergedAnnotation(targetType, FeignClient.class);
			String factoryName = feignClient.name();
			SetterFactory setterFactoryBean = this.getOptional(factoryName, feignContext, SetterFactory.class);
			if (setterFactoryBean != null) {
				this.setterFactory(setterFactoryBean);
			}
			Class<?> fallback = feignClient.fallback();
			if (fallback != void.class) {
				return targetWithFallback(factoryName, feignContext, target, this, fallback);
			}
			Class<?> fallbackFactory = feignClient.fallbackFactory();
			if (fallbackFactory != void.class) {
				return targetWithFallbackFactory(factoryName, feignContext, target, this, fallbackFactory);
			}
			return build().newInstance(target);
		}

		@SuppressWarnings("unchecked")
		private <T> T targetWithFallbackFactory(String feignClientName, FeignContext context,
												Target<T> target,
												Builder builder,
												Class<?> fallbackFactoryClass) {
			FallbackFactory<? extends T> fallbackFactory = (FallbackFactory<? extends T>)
				getFromContext("fallbackFactory", feignClientName, context, fallbackFactoryClass, FallbackFactory.class);
		/* We take a sample fallback from the fallback factory to check if it returns a fallback
		that is compatible with the annotated feign interface. */
			Object exampleFallback = fallbackFactory.create(new RuntimeException());
			Assert.notNull(exampleFallback,
				String.format(
					"Incompatible fallbackFactory instance for feign client %s. Factory may not produce null!",
					feignClientName));
			if (!target.type().isAssignableFrom(exampleFallback.getClass())) {
				throw new IllegalStateException(
					String.format(
						"Incompatible fallbackFactory instance for feign client %s. Factory produces instances of '%s', but should produce instances of '%s'",
						feignClientName, exampleFallback.getClass(), target.type()));
			}
			return builder.target(target, fallbackFactory);
		}


		private <T> T targetWithFallback(String feignClientName, FeignContext context,
										 Target<T> target,
										 Builder builder, Class<?> fallback) {
			T fallbackInstance = getFromContext("fallback", feignClientName, context, fallback, target.type());
			return builder.target(target, fallbackInstance);
		}

		@SuppressWarnings("unchecked")
		private <T> T getFromContext(String fallbackMechanism, String feignClientName, FeignContext context,
									 Class<?> beanType, Class<T> targetType) {
			Object fallbackInstance = context.getInstance(feignClientName, beanType);
			if (fallbackInstance == null) {
				throw new IllegalStateException(String.format(
					"No %s instance of type %s found for feign client %s",
					fallbackMechanism, beanType, feignClientName));
			}

			if (!targetType.isAssignableFrom(beanType)) {
				throw new IllegalStateException(
					String.format(
						"Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
						fallbackMechanism, beanType, targetType, feignClientName));
			}
			return (T) fallbackInstance;
		}

		@Nullable
		private <T> T getOptional(String feignClientName, FeignContext context, Class<T> beanType) {
			return context.getInstance(feignClientName, beanType);
		}

		/**
		 * target
		 *
		 * @param target   Target
		 * @param fallback fallback
		 * @param <T>      泛型标记
		 * @return T
		 * @see #target(Class, String, Object)
		 */
		public <T> T target(Target<T> target, @Nullable T fallback) {
			return build(fallback != null ? new FallbackFactory.Default<T>(fallback) : null)
				.newInstance(target);
		}

		/**
		 * target
		 *
		 * @param target          Target
		 * @param fallbackFactory fallbackFactory
		 * @param <T>             泛型标记
		 * @return T
		 * @see #target(Class, String, FallbackFactory)
		 */
		public <T> T target(Target<T> target, FallbackFactory<? extends T> fallbackFactory) {
			return build(fallbackFactory).newInstance(target);
		}

		/**
		 * Like {@link Feign#newInstance(Target)}, except with {@link HystrixCommand#getFallback()
		 * fallback} support.
		 *
		 * <p>
		 * Fallbacks are known values, which you return when there's an error invoking an http
		 * method. For example, you can return a cached result as opposed to raising an error to the
		 * caller. To use this feature, pass a safe implementation of your target interface as the last
		 * parameter.
		 * </p>
		 *
		 * @param apiType  apiType
		 * @param url      url
		 * @param fallback fallback
		 * @param <T>      泛型标记
		 * @return T
		 * @see #target(Target, Object)
		 */
		public <T> T target(Class<T> apiType, String url, T fallback) {
			return target(new Target.HardCodedTarget<T>(apiType, url), fallback);
		}

		/**
		 * except you can inspect a source exception before creating a fallback object.
		 *
		 * @param apiType         apiType
		 * @param url             url
		 * @param fallbackFactory fallbackFactory
		 * @param <T>             泛型标记
		 * @return T
		 */
		public <T> T target(Class<T> apiType, String url, FallbackFactory<? extends T> fallbackFactory) {
			return target(new Target.HardCodedTarget<T>(apiType, url), fallbackFactory);
		}

		@Override
		public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Builder contract(Contract contract) {
			this.contract = contract;
			return this;
		}

		@Override
		public Feign build() {
			return build(null);
		}

		/**
		 * Configures components needed for hystrix integration.
		 */
		Feign build(@Nullable final FallbackFactory<?> nullableFallbackFactory) {
			super.invocationHandlerFactory((target, dispatch) ->
				new BladeHystrixInvocationHandler(target, dispatch, setterFactory, nullableFallbackFactory));
			super.contract(new HystrixDelegatingContract(contract));
			return super.build();
		}

		/**
		 * Covariant overrides to support chaining to new fallback method.
		 * @param logLevel log 级别
		 * @return Builder
		 */
		@Override
		public Builder logLevel(Logger.Level logLevel) {
			return (Builder) super.logLevel(logLevel);
		}

		@Override
		public Builder client(Client client) {
			return (Builder) super.client(client);
		}

		@Override
		public Builder retryer(Retryer retryer) {
			return (Builder) super.retryer(retryer);
		}

		@Override
		public Builder logger(Logger logger) {
			return (Builder) super.logger(logger);
		}

		@Override
		public Builder encoder(Encoder encoder) {
			return (Builder) super.encoder(encoder);
		}

		@Override
		public Builder decoder(Decoder decoder) {
			return (Builder) super.decoder(decoder);
		}

		@Override
		public Builder mapAndDecode(ResponseMapper mapper, Decoder decoder) {
			return (Builder) super.mapAndDecode(mapper, decoder);
		}

		@Override
		public Builder decode404() {
			return (Builder) super.decode404();
		}

		@Override
		public Builder errorDecoder(ErrorDecoder errorDecoder) {
			return (Builder) super.errorDecoder(errorDecoder);
		}

		@Override
		public Builder options(Request.Options options) {
			return (Builder) super.options(options);
		}

		@Override
		public Builder requestInterceptor(RequestInterceptor requestInterceptor) {
			return (Builder) super.requestInterceptor(requestInterceptor);
		}

		@Override
		public Builder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
			return (Builder) super.requestInterceptors(requestInterceptors);
		}
	}

}
