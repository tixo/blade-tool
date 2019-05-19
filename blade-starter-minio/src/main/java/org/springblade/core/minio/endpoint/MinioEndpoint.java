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
package org.springblade.core.minio.endpoint;

import io.minio.ObjectStat;
import io.minio.messages.Bucket;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.minio.MinioTemplate;
import org.springblade.core.minio.model.MinioItem;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * MinIO端点
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/minio/endpoint")
@ConditionalOnProperty(name = "minio.enable", havingValue = "true")
public class MinioEndpoint {

	private MinioTemplate template;

	/**
	 * 获取存储桶
	 *
	 * @param bucketName 存储桶名称
	 * @return Bucket
	 */
	@SneakyThrows
	@GetMapping("/bucket")
	public R<Bucket> bucket(@RequestParam String bucketName) {
		return R.data(template.getBucket(bucketName));
	}

	/**
	 * 获取存储桶集合
	 *
	 * @return List<Bucket>
	 */
	@SneakyThrows
	@GetMapping("/buckets")
	public R<List<Bucket>> buckets() {
		return R.data(template.listBuckets());
	}

	/**
	 * 创建存储桶
	 *
	 * @param bucketName 存储桶名称
	 * @return Bucket
	 */
	@SneakyThrows
	@PostMapping("/make-bucket")
	public R<Bucket> makeBucket(@RequestParam String bucketName) {
		template.makeBucket(bucketName);
		return R.data(template.getBucket(bucketName));
	}

	/**
	 * 创建存储桶
	 *
	 * @param bucketName 存储桶名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/remove-bucket")
	public R removeBucket(@RequestParam String bucketName) {
		template.removeBucket(bucketName);
		return R.success("删除成功");
	}

	/**
	 * 获取 存储桶 生命周期
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	@SneakyThrows
	@GetMapping("/bucket-life-cycle")
	public R<String> getBucketLifeCycle(@RequestParam String bucketName) {
		return R.data(template.getBucketLifeCycle(bucketName));
	}

	/**
	 * 删除 存储桶 生命周期
	 *
	 * @param bucketName 存储桶名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/delete-bucket-life-cycle")
	public R deleteBucketLifeCycle(@RequestParam String bucketName) {
		template.deleteBucketLifeCycle(bucketName);
		return R.success("删除成功");
	}

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param objectName     存储桶对象名称
	 * @param destBucketName 目标存储桶名称
	 * @param destObjectName 目标存储桶对象名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/copy-object")
	public R copyObject(@RequestParam String bucketName, @RequestParam String objectName, @RequestParam String destBucketName, String destObjectName) {
		template.copyObject(bucketName, objectName, destBucketName, destObjectName);
		return R.success("操作成功");
	}

	/**
	 * 获取文件信息
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return InputStream
	 */
	@SneakyThrows
	@GetMapping("/stat-object")
	public R<ObjectStat> statObject(@RequestParam String bucketName, @RequestParam String objectName) {
		return R.data(template.statObject(bucketName, objectName));
	}

	/**
	 * 获取文件流
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return InputStream
	 */
	@SneakyThrows
	@GetMapping("/get-object")
	public InputStream getObject(@RequestParam String bucketName, @RequestParam String objectName) {
		return template.getObject(bucketName, objectName);
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @return List<MinioItem>
	 */
	@SneakyThrows
	@GetMapping("/list-objects")
	public R<List<MinioItem>> listObjects(@RequestParam String bucketName) {
		return R.data(template.listObjects(bucketName));
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @param prefix     对象名前缀
	 * @return List<MinioItem>
	 */
	@SneakyThrows
	@GetMapping("/list-objects-by-prefix")
	public R<List<MinioItem>> listObjects(@RequestParam String bucketName, @RequestParam String prefix) {
		return R.data(template.listObjects(bucketName, prefix));
	}

	/**
	 * 获取文件外链
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return String
	 */
	@SneakyThrows
	@GetMapping("/object-url")
	public R<String> getObjectUrl(@RequestParam String bucketName, @RequestParam String objectName) {
		return R.data(template.getObjectUrl(bucketName, objectName));
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @param file       文件
	 * @return ObjectStat
	 */
	@SneakyThrows
	@PostMapping("/put-object")
	public R<ObjectStat> putObject(@RequestParam String bucketName, @RequestParam String objectName, @RequestParam MultipartFile file) {
		template.putObject(bucketName, objectName, file.getInputStream());
		return R.data(template.statObject(bucketName, objectName));
	}

	/**
	 * 删除文件
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/remove-object")
	public R removeObject(@RequestParam String bucketName, @RequestParam String objectName) {
		template.removeObject(bucketName, objectName);
		return R.success("操作成功");
	}

	/**
	 * 批量删除文件
	 *
	 * @param bucketName  存储桶名称
	 * @param objectNames 存储桶对象名称集合
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/remove-objects")
	public R removeObjects(@RequestParam String bucketName, @RequestParam String objectNames) {
		template.removeObjects(bucketName, Func.toStrList(objectNames));
		return R.success("操作成功");
	}

}
