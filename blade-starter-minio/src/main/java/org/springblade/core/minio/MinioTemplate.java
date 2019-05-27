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
package org.springblade.core.minio;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.messages.Bucket;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.minio.enums.PolicyType;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.oss.model.OssFile;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * MinIOTemplate
 *
 * @author Chill
 */
@AllArgsConstructor
public class MinioTemplate implements OssTemplate {

	/**
	 * MinIO客户端
	 */
	private MinioClient client;

	/**
	 * 存储桶命名规则
	 */
	private OssRule ossRule;

	/**
	 * 配置类
	 */
	private OssProperties ossProperties;


	@Override
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (!client.bucketExists(getBucketName(bucketName))) {
			client.makeBucket(getBucketName(bucketName));
			client.setBucketPolicy(getBucketName(bucketName), getPolicyType(getBucketName(bucketName), PolicyType.READ));
		}
	}

	@SneakyThrows
	public Bucket getBucket() {
		return getBucket(getBucketName());
	}

	@SneakyThrows
	public Bucket getBucket(String bucketName) {
		Optional<Bucket> bucketOptional = client.listBuckets().stream().filter(bucket -> bucket.name().equals(getBucketName(bucketName))).findFirst();
		return bucketOptional.orElse(null);
	}

	@SneakyThrows
	public List<Bucket> listBuckets() {
		return client.listBuckets();
	}

	@Override
	@SneakyThrows
	public void removeBucket(String bucketName) {
		client.removeBucket(getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return client.bucketExists(getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		client.copyObject(getBucketName(bucketName), fileName, destBucketName);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		client.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String bucketName, String fileName) {
		ObjectStat stat = client.statObject(getBucketName(bucketName), fileName);
		OssFile ossFile = new OssFile();
		ossFile.setName(Func.isEmpty(stat.name()) ? fileName : stat.name());
		ossFile.setLink(fileLink(ossFile.getName()));
		ossFile.setHash(String.valueOf(stat.hashCode()));
		ossFile.setLength(stat.length());
		ossFile.setPutTime(stat.createdTime());
		ossFile.setContentType(stat.contentType());
		return ossFile;
	}

	@Override
	public String filePath(String fileName) {
		return getBucketName().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	public String filePath(String bucketName, String fileName) {
		return getBucketName(bucketName).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName()).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String bucketName, String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName(bucketName)).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(MultipartFile file) {
		return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String bucketName, String fileName, MultipartFile file) {
		return putFile(bucketName, file.getOriginalFilename(), file.getInputStream());
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String fileName, InputStream stream) {
		return putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	@SneakyThrows
	public BladeFile putFile(String bucketName, String fileName, InputStream stream) {
		makeBucket(bucketName);
		fileName = getFileName(fileName);
		client.putObject(getBucketName(bucketName), fileName, stream, (long) stream.available(), null, null, "application/octet-stream");
		BladeFile file = new BladeFile();
		file.setName(fileName);
		file.setLink(fileLink(bucketName, fileName));
		return file;
	}

	@Override
	@SneakyThrows
	public void removeFile(String fileName) {
		removeFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFile(String bucketName, String fileName) {
		client.removeObject(getBucketName(bucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFiles(List<String> fileNames) {
		removeFiles(ossProperties.getBucketName(), fileNames);

	}

	@Override
	@SneakyThrows
	public void removeFiles(String bucketName, List<String> fileNames) {
		client.removeObjects(getBucketName(bucketName), fileNames);
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @return String
	 */
	private String getBucketName() {
		return getBucketName(ossProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return ossRule.bucketName(bucketName);
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getFileName(String originalFilename) {
		return ossRule.fileName(originalFilename);
	}

	/**
	 * 获取存储桶策略
	 *
	 * @param policyType 策略枚举
	 * @return String
	 */
	public String getPolicyType(PolicyType policyType) {
		return getPolicyType(getBucketName(), policyType);
	}

	/**
	 * 获取存储桶策略
	 *
	 * @param bucketName 存储桶名称
	 * @param policyType 策略枚举
	 * @return String
	 */
	public String getPolicyType(String bucketName, PolicyType policyType) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n");
		builder.append("    \"Statement\": [\n");
		builder.append("        {\n");
		builder.append("            \"Action\": [\n");
		builder.append("                \"s3:GetBucketLocation\",\n");

		switch (policyType) {
			case WRITE:
				builder.append("                \"s3:ListBucketMultipartUploads\"\n");
				break;
			case READ_WRITE:
				builder.append("                \"s3:ListBucket\",\n");
				builder.append("                \"s3:ListBucketMultipartUploads\"\n");
				break;
			default:
				builder.append("                \"s3:ListBucket\"\n");
				break;
		}

		builder.append("            ],\n");
		builder.append("            \"Effect\": \"Allow\",\n");
		builder.append("            \"Principal\": \"*\",\n");
		builder.append("            \"Resource\": \"arn:aws:s3:::");
		builder.append(bucketName);
		builder.append("\"\n");
		builder.append("        },\n");
		builder.append("        {\n");
		builder.append("            \"Action\": ");

		switch (policyType) {
			case WRITE:
				builder.append("[\n");
				builder.append("                \"s3:AbortMultipartUpload\",\n");
				builder.append("                \"s3:DeleteObject\",\n");
				builder.append("                \"s3:ListMultipartUploadParts\",\n");
				builder.append("                \"s3:PutObject\"\n");
				builder.append("            ],\n");
				break;
			case READ_WRITE:
				builder.append("[\n");
				builder.append("                \"s3:AbortMultipartUpload\",\n");
				builder.append("                \"s3:DeleteObject\",\n");
				builder.append("                \"s3:GetObject\",\n");
				builder.append("                \"s3:ListMultipartUploadParts\",\n");
				builder.append("                \"s3:PutObject\"\n");
				builder.append("            ],\n");
				break;
			default:
				builder.append("\"s3:GetObject\",\n");
				break;
		}

		builder.append("            \"Effect\": \"Allow\",\n");
		builder.append("            \"Principal\": \"*\",\n");
		builder.append("            \"Resource\": \"arn:aws:s3:::");
		builder.append(bucketName);
		builder.append("/*\"\n");
		builder.append("        }\n");
		builder.append("    ],\n");
		builder.append("    \"Version\": \"2012-10-17\"\n");
		builder.append("}\n");
		return builder.toString();
	}

}
