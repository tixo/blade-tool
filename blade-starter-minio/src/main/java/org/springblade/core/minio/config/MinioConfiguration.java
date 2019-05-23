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
package org.springblade.core.minio.config;

import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.minio.MinioTemplate;
import org.springblade.core.minio.props.MinioProperties;
import org.springblade.core.minio.rule.BladeMinioRule;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置类
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(value = "minio.enable", havingValue = "true")
public class MinioConfiguration {

	private MinioProperties minioProperties;

	@Bean
	@ConditionalOnMissingBean(OssRule.class)
	public OssRule minioRule() {
		return new BladeMinioRule(minioProperties.getTenantMode());
	}

	@Bean
	@SneakyThrows
	@ConditionalOnMissingBean(MinioClient.class)
	public MinioClient minioClient() {
		return new MinioClient(
			minioProperties.getEndpoint(),
			minioProperties.getAccessKey(),
			minioProperties.getSecretKey()
		);
	}

	@Bean
	@ConditionalOnBean({MinioClient.class, OssRule.class})
	@ConditionalOnMissingBean(MinioTemplate.class)
	public MinioTemplate minioTemplate(MinioClient minioClient, OssRule minioRule, MinioProperties minioProperties) {
		return new MinioTemplate(minioClient, minioRule, minioProperties);
	}

}
