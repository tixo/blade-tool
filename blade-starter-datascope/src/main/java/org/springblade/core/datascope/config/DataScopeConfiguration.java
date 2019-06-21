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
package org.springblade.core.datascope.config;

import org.springblade.core.datascope.interceptor.DataScopeInterceptor;
import org.springblade.core.datascope.rule.BladeDataScopeRule;
import org.springblade.core.datascope.rule.DataScopeRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据权限配置类
 *
 * @author Chill
 */
@Configuration
public class DataScopeConfiguration {

	@Bean
	@ConditionalOnMissingBean(DataScopeRule.class)
	public DataScopeRule dataScopeRule() {
		return new BladeDataScopeRule();
	}

	@Bean
	@ConditionalOnBean(DataScopeRule.class)
	@ConditionalOnMissingBean(DataScopeInterceptor.class)
	public DataScopeInterceptor interceptor(DataScopeRule dataScopeRule) {
		return new DataScopeInterceptor(dataScopeRule);
	}

}
