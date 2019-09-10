package org.springblade.core.aliyun.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.AllArgsConstructor;
import org.springblade.core.aliyun.AliyunTemplate;
import org.springblade.core.oss.config.OssConfiguration;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.BladeOssRule;
import org.springblade.core.oss.rule.OssRule;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 oss 支持
 *
 * @version 1.0
 * @author：xy
 * @createTime：2019-09-09
 */
@Configuration
@AllArgsConstructor
@AutoConfigureAfter(OssConfiguration.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.name", havingValue = "aliyun")
public class AliyunConfiguration {
	private OssProperties ossProperties;

	@Bean
	@ConditionalOnMissingBean(OssRule.class)
	public OssRule ossRule() {
		return new BladeOssRule(ossProperties.getTenantMode());
	}

	@Bean(destroyMethod = "shutdown")
	public OSS ossClient() {
		OSS build = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKey(), ossProperties.getSecretKey());
		return build;
	}

	@Bean
	@ConditionalOnMissingBean(AliyunTemplate.class)
	@ConditionalOnBean({OssRule.class})
	public AliyunTemplate aliyunTemplate(OSS ossClient, OssProperties ossProperties, OssRule ossRule) {
		return new AliyunTemplate(ossClient, ossProperties, ossRule);
	}

}
