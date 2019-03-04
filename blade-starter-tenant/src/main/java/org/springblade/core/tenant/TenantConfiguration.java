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
package org.springblade.core.tenant;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户配置类
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(MybatisConfiguration.class)
@EnableConfigurationProperties(BladeTenantProperties.class)
public class TenantConfiguration {

	/**
	 * 多租户配置类
	 */
	private final BladeTenantProperties properties;

	/**
	 * 自定义租户处理器
	 *
	 * @return TenantHandler
	 */
	@Bean
	@ConditionalOnMissingBean(TenantHandler.class)
	public TenantHandler bladeTenantHandler() {
		return new BladeTenantHandler(properties);
	}

	/**
	 * 自定义租户id生成器
	 *
	 * @return TenantId
	 */
	@Bean
	@ConditionalOnMissingBean(TenantId.class)
	public TenantId tenantId() {
		return new BladeTenantId();
	}

	/**
	 * 分页插件
	 *
	 * @param tenantHandler 自定义租户处理器
	 * @return PaginationInterceptor
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor(TenantHandler tenantHandler) {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		List<ISqlParser> sqlParserList = new ArrayList<>();
		TenantSqlParser tenantSqlParser = new TenantSqlParser();
		tenantSqlParser.setTenantHandler(tenantHandler);
		sqlParserList.add(tenantSqlParser);
		paginationInterceptor.setSqlParserList(sqlParserList);
		return paginationInterceptor;
	}

}
