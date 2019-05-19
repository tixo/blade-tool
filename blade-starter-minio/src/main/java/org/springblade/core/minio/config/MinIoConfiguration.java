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
import org.springblade.core.minio.MinIoTemplate;
import org.springblade.core.minio.props.MinIoProperties;
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
@EnableConfigurationProperties(MinIoProperties.class)
@ConditionalOnProperty(value = "blade.minio.endpoint", havingValue = "true")
public class MinIoConfiguration {

	private MinIoProperties minioProperties;

	@Bean
	@SneakyThrows
	@ConditionalOnMissingBean(MinIoTemplate.class)
	public MinIoTemplate minioTemplate() {
		MinioClient minioClient = new MinioClient(
			minioProperties.getEndpoint(),
			minioProperties.getAccessKey(),
			minioProperties.getSecretKey()
		);
		return new MinIoTemplate(minioClient);
	}

}
