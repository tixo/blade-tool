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
package org.springblade.core.cloud.config;

import org.springblade.core.cloud.feign.BladeFeignConfiguration;
import org.springblade.core.cloud.http.RestTemplateConfiguration;
import org.springblade.core.cloud.hystrix.BladeAccountGetter;
import org.springblade.core.cloud.hystrix.BladeHystrixAccountGetter;
import org.springblade.core.secure.BladeUser;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户信息获取配置类
 *
 * @author Chill
 */
@Configuration
@ConditionalOnClass(BladeUser.class)
@AutoConfigureBefore({
	RestTemplateConfiguration.class,
	BladeFeignConfiguration.class
})
public class AccountAutoConfiguration {

	@Bean
	public BladeHystrixAccountGetter bladeHystrixAccountGetter() {
		return new BladeAccountGetter();
	}

}
