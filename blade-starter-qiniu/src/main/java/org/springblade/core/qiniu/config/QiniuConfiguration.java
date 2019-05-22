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
package org.springblade.core.qiniu.config;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import org.springblade.core.qiniu.QiniuTemplate;
import org.springblade.core.qiniu.props.QiniuProperties;
import org.springblade.core.qiniu.rule.BladeQiniuRule;
import org.springblade.core.qiniu.rule.IQiniuRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Qiniu配置类
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(QiniuProperties.class)
@ConditionalOnProperty(value = "qiniu.enable", havingValue = "true")
public class QiniuConfiguration {

	private QiniuProperties qiniuProperties;

	@Bean
	@ConditionalOnMissingBean(IQiniuRule.class)
	public IQiniuRule qiniuRule() {
		return new BladeQiniuRule(qiniuProperties.getTenantMode());
	}

	@Bean
	public com.qiniu.storage.Configuration qiniuConfiguration() {
		return new com.qiniu.storage.Configuration(Zone.zone0());
	}

	@Bean
	public Auth auth() {
		return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
	}

	@Bean
	@ConditionalOnBean(com.qiniu.storage.Configuration.class)
	public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
		return new UploadManager(cfg);
	}

	@Bean
	@ConditionalOnBean(com.qiniu.storage.Configuration.class)
	public BucketManager bucketManager(com.qiniu.storage.Configuration cfg) {
		return new BucketManager(auth(), cfg);
	}

	@Bean
	@ConditionalOnMissingBean(QiniuTemplate.class)
	@ConditionalOnBean({Auth.class, UploadManager.class, BucketManager.class, IQiniuRule.class})
	public QiniuTemplate qiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager, QiniuProperties qiniuProperties, IQiniuRule qiniuRule) {
		return new QiniuTemplate(auth, uploadManager, bucketManager, qiniuProperties, qiniuRule);
	}

}
