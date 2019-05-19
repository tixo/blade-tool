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
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.minio.model.MinioItem;
import org.springblade.core.minio.rule.IMinioRule;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MinIOTemplate
 *
 * @author Chill
 */
@AllArgsConstructor
public class MinioTemplate {

	/**
	 * MinIO客户端
	 */
	private MinioClient client;

	/**
	 * 存储桶命名规则
	 */
	private IMinioRule minIoRule;

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return minIoRule.bucketName(bucketName);
	}

	/**
	 * 创建 存储桶
	 *
	 * @param bucketName 存储桶名称
	 */
	@SneakyThrows
	public Bucket makeBucket(String bucketName) {
		if (!client.bucketExists(getBucketName(bucketName))) {
			client.makeBucket(getBucketName(bucketName));
		}
		return getBucket(getBucketName(bucketName));
	}

	/**
	 * 获取单个 存储桶
	 *
	 * @param bucketName 存储桶名称
	 */
	@SneakyThrows
	public Bucket getBucket(String bucketName) {
		Optional<Bucket> bucketOptional = client.listBuckets().stream().filter(bucket -> bucket.name().equals(getBucketName(bucketName))).findFirst();
		return bucketOptional.orElse(null);
	}


	/**
	 * 获取全部 存储桶
	 */
	@SneakyThrows
	public List<Bucket> listBuckets() {
		return client.listBuckets();
	}

	/**
	 * 删除 存储桶
	 *
	 * @param bucketName 存储桶名称
	 * @return
	 */
	@SneakyThrows
	public void removeBucket(String bucketName) {
		client.removeBucket(getBucketName(bucketName));
	}

	/**
	 * 获取 存储桶 生命周期
	 *
	 * @param bucketName 存储桶名称
	 */
	@SneakyThrows
	public String getBucketLifeCycle(String bucketName) {
		return client.getBucketLifeCycle(getBucketName(bucketName));
	}

	/**
	 * 删除 存储桶 生命周期
	 *
	 * @param bucketName 存储桶名称
	 */
	@SneakyThrows
	public void deleteBucketLifeCycle(String bucketName) {
		client.deleteBucketLifeCycle(getBucketName(bucketName));
	}

	/**
	 * 存储桶是否存在
	 *
	 * @param bucketName 存储桶名称
	 * @return boolean
	 */
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return client.bucketExists(getBucketName(bucketName));
	}

	/**
	 * 获取存储桶策略
	 *
	 * @param bucketName 存储桶名称
	 */
	@SneakyThrows
	public void getBucketPolicy(String bucketName) {
		client.getBucketPolicy(getBucketName(bucketName));
	}

	/**
	 * 设置存储桶策略
	 *
	 * @param bucketName 存储桶名称
	 * @param policy     名称
	 */
	@SneakyThrows
	public void setBucketPolicy(String bucketName, String policy) {
		client.setBucketPolicy(getBucketName(bucketName), policy);
	}

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param objectName     存储桶对象名称
	 * @param destBucketName 目标存储桶名称
	 */
	@SneakyThrows
	public void copyObject(String bucketName, String objectName, String destBucketName) {
		client.copyObject(getBucketName(bucketName), objectName, destBucketName);
	}

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param objectName     存储桶对象名称
	 * @param destBucketName 目标存储桶名称
	 * @param destObjectName 目标存储桶对象名称
	 */
	@SneakyThrows
	public void copyObject(String bucketName, String objectName, String destBucketName, String destObjectName) {
		client.copyObject(getBucketName(bucketName), objectName, getBucketName(destBucketName), destObjectName);
	}

	/**
	 * 获取文件信息
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return InputStream
	 */
	@SneakyThrows
	public ObjectStat statObject(String bucketName, String objectName) {
		return client.statObject(getBucketName(bucketName), objectName);
	}

	/**
	 * 获取文件流
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return InputStream
	 */
	@SneakyThrows
	public InputStream getObject(String bucketName, String objectName) {
		return client.getObject(getBucketName(bucketName), objectName);
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @return
	 */
	@SneakyThrows
	public List<MinioItem> listObjects(String bucketName) {
		return buildItems(client.listObjects(getBucketName(bucketName)));
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @param prefix     对象名前缀
	 * @return
	 */
	@SneakyThrows
	public List<MinioItem> listObjects(String bucketName, String prefix) {
		return buildItems(client.listObjects(getBucketName(bucketName), prefix));
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @param prefix     对象名前缀
	 * @param recursive  是否递归
	 * @return
	 */
	@SneakyThrows
	public List<MinioItem> listObjects(String bucketName, String prefix, boolean recursive) {
		return buildItems(client.listObjects(getBucketName(bucketName), prefix, recursive));
	}

	/**
	 * 构建对象类
	 *
	 * @param results 结果集
	 * @return
	 */
	@SneakyThrows
	private List<MinioItem> buildItems(Iterable<Result<Item>> results) {
		List<MinioItem> items = new ArrayList<>();
		while (results.iterator().hasNext()) {
			items.add(new MinioItem(results.iterator().next().get()));
		}
		return items;
	}

	/**
	 * 获取文件外链
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return String
	 */
	@SneakyThrows
	public String getObjectUrl(String bucketName, String objectName) {
		return client.presignedGetObject(getBucketName(bucketName), objectName);
	}


	/**
	 * 获取文件外链
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @param expires    过期时间
	 * @return String
	 */
	@SneakyThrows
	public String getObjectUrl(String bucketName, String objectName, Integer expires) {
		return client.presignedGetObject(getBucketName(bucketName), objectName, expires);
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @param stream     文件流
	 */
	@SneakyThrows
	public void putObject(String bucketName, String objectName, InputStream stream) {
		putObject(getBucketName(bucketName), objectName, stream, (long) stream.available(), "application/octet-stream");
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @param stream     文件流
	 * @param size       大小
	 */
	@SneakyThrows
	public void putObject(String bucketName, String objectName, InputStream stream, long size) {
		putObject(getBucketName(bucketName), objectName, stream, size, "application/octet-stream");
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName  存储桶名称
	 * @param objectName  存储桶对象名称
	 * @param stream      文件流
	 * @param size        大小
	 * @param contextType 类型
	 */
	@SneakyThrows
	public void putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) {
		makeBucket(bucketName);
		client.putObject(getBucketName(bucketName), objectName, stream, size, null, null, contextType);
	}

	/**
	 * 删除文件
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 */
	@SneakyThrows
	public void removeObject(String bucketName, String objectName) {
		client.removeObject(getBucketName(bucketName), objectName);
	}

	/**
	 * 批量删除文件
	 *
	 * @param bucketName  存储桶名称
	 * @param objectNames 存储桶对象名称集合
	 */
	@SneakyThrows
	public void removeObjects(String bucketName, List<String> objectNames) {
		client.removeObjects(getBucketName(bucketName), objectNames);
	}

}
