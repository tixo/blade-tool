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
import org.springblade.core.minio.MinIoTemplate;
import org.springblade.core.minio.model.MinIoItem;
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
@ConditionalOnProperty(name = "minio.endpoint.enable", havingValue = "true")
public class MinIoEndpoint {

	private MinIoTemplate template;

	/**
	 * 获取存储桶
	 *
	 * @param bucketName 存储桶名称
	 * @return Bucket
	 */
	@SneakyThrows
	@GetMapping("/bucket/{bucketName}")
	public R<Bucket> bucket(@PathVariable String bucketName) {
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
	@PostMapping("/make-bucket/{bucketName}")
	public R<Bucket> makeBucket(@PathVariable String bucketName) {
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
	@PostMapping("/remove-bucket/{bucketName}")
	public R removeBucket(@PathVariable String bucketName) {
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
	@GetMapping("/bucket-life-cycle/{bucketName}")
	public R<String> getBucketLifeCycle(@PathVariable String bucketName) {
		return R.data(template.getBucketLifeCycle(bucketName));
	}

	/**
	 * 删除 存储桶 生命周期
	 *
	 * @param bucketName 存储桶名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/delete-bucket-life-cycle/{bucketName}")
	public R deleteBucketLifeCycle(@PathVariable String bucketName) {
		template.deleteBucketLifeCycle(bucketName);
		return R.success("删除成功");
	}

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param objectName     存储桶对象名称
	 * @param destBucketName 目标存储桶名称
	 * @return R
	 */
	@SneakyThrows
	@PostMapping("/copy-object/{bucketName}/{objectName}/{destBucketName}")
	public R copyObject(@PathVariable String bucketName, @PathVariable String objectName, @PathVariable String destBucketName) {
		template.copyObject(bucketName, objectName, destBucketName);
		return R.success("操作成功");
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
	@PostMapping("/copy-object/{bucketName}/{objectName}/{destBucketName}/{destObjectName}")
	public R copyObject(@PathVariable String bucketName, @PathVariable String objectName, @PathVariable String destBucketName, @PathVariable String destObjectName) {
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
	@GetMapping("/stat-object/{bucketName}/{objectName}")
	public R<ObjectStat> statObject(@PathVariable String bucketName, @PathVariable String objectName) {
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
	@GetMapping("/get-object/{bucketName}/{objectName}")
	public InputStream getObject(@PathVariable String bucketName, @PathVariable String objectName) {
		return template.getObject(bucketName, objectName);
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @return List<MinIoItem>
	 */
	@SneakyThrows
	@GetMapping("/list-objects/{bucketName}")
	public R<List<MinIoItem>> listObjects(@PathVariable String bucketName) {
		return R.data(template.listObjects(bucketName));
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @param prefix     对象名前缀
	 * @return List<MinIoItem>
	 */
	@SneakyThrows
	@GetMapping("/list-objects/{bucketName}/{prefix}")
	public R<List<MinIoItem>> listObjects(@PathVariable String bucketName, @PathVariable String prefix) {
		return R.data(template.listObjects(bucketName, prefix));
	}

	/**
	 * 获取存储桶下的对象集合
	 *
	 * @param bucketName 存储桶名称
	 * @param prefix     对象名前缀
	 * @param recursive  是否递归
	 * @return List<MinIoItem>
	 */
	@SneakyThrows
	@GetMapping("/list-objects/{bucketName}/{prefix}/{recursive}")
	public R<List<MinIoItem>> listObjects(@PathVariable String bucketName, @PathVariable String prefix, @PathVariable boolean recursive) {
		return R.data(template.listObjects(bucketName, prefix, recursive));
	}

	/**
	 * 获取文件外链
	 *
	 * @param bucketName 存储桶名称
	 * @param objectName 存储桶对象名称
	 * @return String
	 */
	@SneakyThrows
	@GetMapping("/object-url/{bucketName}/{objectName}")
	public R<String> getObjectUrl(@PathVariable String bucketName, @PathVariable String objectName) {
		return R.data(template.getObjectUrl(bucketName, objectName));
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
	@GetMapping("/object-url/{bucketName}/{objectName}/{expires}")
	public R<String> getObjectUrl(@PathVariable String bucketName, @PathVariable String objectName, @PathVariable Integer expires) {
		return R.data(template.getObjectUrl(bucketName, objectName, expires));
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
	@PostMapping("/put-object/{bucketName}/{objectName}")
	public R<ObjectStat> putObject(@PathVariable String bucketName, @PathVariable String objectName, @RequestParam MultipartFile file) {
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
	@PostMapping("/remove-object/{bucketName}/{objectName}")
	public R removeObject(@PathVariable String bucketName, @PathVariable String objectName) {
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
	@PostMapping("/remove-object/{bucketName}/{objectNames}")
	public R removeObjects(@PathVariable String bucketName, @PathVariable String objectNames) {
		template.removeObjects(bucketName, Func.toStrList(objectNames));
		return R.success("操作成功");
	}

}
