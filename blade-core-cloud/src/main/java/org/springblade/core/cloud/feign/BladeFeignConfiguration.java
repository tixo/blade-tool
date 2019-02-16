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
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;
import org.springblade.core.tool.convert.EnumToStringConverter;
import org.springblade.core.tool.convert.StringToEnumConverter;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.util.ArrayList;

/**
 * feign 配置增强
 *
 * @author Chill
 */
@Configuration
@ConditionalOnClass(Feign.class)
public class BladeFeignConfiguration {

	@Configuration("hystrixFeignConfiguration")
	@ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
	protected static class HystrixFeignConfiguration {
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		@ConditionalOnProperty("feign.hystrix.enabled")
		public Feign.Builder feignHystrixBuilder(
			RequestInterceptor requestInterceptor,
			ErrorDecoder errorDecoder, FeignContext feignContext) {
			return BladeHystrixFeign.builder(feignContext)
				.decode404()
				.requestInterceptor(requestInterceptor)
				.errorDecoder(errorDecoder);
		}

		@Bean
		@ConditionalOnMissingBean
		public RequestInterceptor requestInterceptor() {
			return new BladeFeignRequestHeaderInterceptor();
		}

		@Bean
		@ConditionalOnMissingBean
		public ErrorDecoder errorDecoder() {
			return new BladeErrorDecoder(JsonUtil.getInstance());
		}
	}

	/**
	 * mica enum 《-》 String 转换配置
	 *
	 * @param conversionService ConversionService
	 * @return SpringMvcContract
	 */
	@Bean
	public Contract feignContract(@Qualifier("mvcConversionService") ConversionService conversionService) {
		ConverterRegistry converterRegistry = ((ConverterRegistry) conversionService);
		converterRegistry.addConverter(new EnumToStringConverter());
		converterRegistry.addConverter(new StringToEnumConverter());
		return new BladeSpringMvcContract(new ArrayList<>(), conversionService);
	}

}
