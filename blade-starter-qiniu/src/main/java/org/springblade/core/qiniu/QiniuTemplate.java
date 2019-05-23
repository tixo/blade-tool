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
package org.springblade.core.qiniu;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.qiniu.props.QiniuProperties;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * QiniuTemplate
 *
 * @author Chill
 */
@AllArgsConstructor
public class QiniuTemplate {
	private Auth auth;
	private UploadManager uploadManager;
	private BucketManager bucketManager;
	private QiniuProperties qiniuProperties;
	private OssRule qiniuRule;


	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @return String
	 */
	private String getBucketName() {
		return getBucketName(qiniuProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return qiniuRule.bucketName(bucketName);
	}

	/**
	 * 获取存储桶信息
	 *
	 * @return BucketInfo
	 */
	@SneakyThrows
	public BucketInfo getBucket() {
		return bucketManager.getBucketInfo(getBucketName());
	}

	/**
	 * 获取存储桶信息
	 *
	 * @param bucketName 存储桶名
	 * @return BucketInfo
	 */
	@SneakyThrows
	public BucketInfo getBucket(String bucketName) {
		return bucketManager.getBucketInfo(getBucketName(bucketName));
	}

	/**
	 * 创建存储桶
	 *
	 * @param bucketName 存储桶名
	 */
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (!CollectionUtil.contains(bucketManager.buckets(), getBucketName(bucketName))) {
			bucketManager.createBucket(getBucketName(bucketName), Zone.zone0().getRegion());
		}
	}

	/**
	 * 上传文件
	 *
	 * @param file 文件
	 * @return Response
	 */
	@SneakyThrows
	public Response putObject(MultipartFile file) {
		return putObject(file, file.getOriginalFilename(), false);
	}

	/**
	 * 上传文件,自动生成文件名
	 *
	 * @param file 文件
	 * @return Response
	 */
	@SneakyThrows
	public Response putObjectByRule(MultipartFile file) {
		return putObject(file, qiniuRule.fileName(file.getOriginalFilename()), false);
	}

	/**
	 * 上传文件
	 *
	 * @param file 文件
	 * @param key  文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response putObject(MultipartFile file, String key) {
		return putObject(file, key, false);
	}

	/**
	 * 上传文件
	 *
	 * @param file  文件
	 * @param key   文件名
	 * @param cover 是否覆盖
	 * @return Response
	 */
	@SneakyThrows
	public Response putObject(MultipartFile file, String key, boolean cover) {
		return putObject(file.getInputStream(), key, cover);
	}

	/**
	 * 上传文件
	 *
	 * @param stream 文件流
	 * @param key    文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response putObject(InputStream stream, String key) {
		return putObject(stream, key, false);
	}

	/**
	 * 上传文件
	 *
	 * @param stream 文件流
	 * @param key    文件名
	 * @param cover  是否覆盖
	 * @return
	 */
	@SneakyThrows
	public Response putObject(InputStream stream, String key, boolean cover) {
		makeBucket(getBucketName());
		Response response;
		// 覆盖上传
		if (cover) {
			response = uploadManager.put(stream, key, getUploadToken(key), null, null);
		} else {
			response = uploadManager.put(stream, key, getUploadToken(), null, null);
			int retry = 0;
			while (response.needRetry() && retry < qiniuProperties.getRetry()) {
				response = uploadManager.put(stream, key, getUploadToken(), null, null);
				retry++;
			}
		}
		return response;
	}

	/**
	 * 列举空间文件 v2 接口，返回一个 response 对象。v2 接口可以避免由于大量删除导致的列举超时问题，返回的 response 对象中的 body 可以转换为
	 * string stream 来处理。
	 *
	 * @param prefix    文件名前缀
	 * @param marker    上一次获取文件列表时返回的 marker
	 * @param limit     每次迭代的长度限制，推荐值 10000
	 * @param delimiter 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
	 * @return Response 返回一个 okhttp response 对象
	 */
	@SneakyThrows
	public Response listObject(String prefix, String marker, int limit, String delimiter) {
		return bucketManager.listV2(getBucketName(), prefix, marker, limit, delimiter);
	}

	/**
	 * 列举空间文件 v2 接口，返回一个 response 对象。v2 接口可以避免由于大量删除导致的列举超时问题，返回的 response 对象中的 body 可以转换为
	 * string stream 来处理。
	 *
	 * @param bucketName 空间名
	 * @param prefix     文件名前缀
	 * @param marker     上一次获取文件列表时返回的 marker
	 * @param limit      每次迭代的长度限制，推荐值 10000
	 * @param delimiter  指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
	 * @return Response 返回一个 okhttp response 对象
	 */
	@SneakyThrows
	public Response listObject(String bucketName, String prefix, String marker, int limit, String delimiter) {
		return bucketManager.listV2(getBucketName(bucketName), prefix, marker, limit, delimiter);
	}


	/**
	 * 删除文件
	 *
	 * @param key 文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response delete(String key) {
		return bucketManager.delete(getBucketName(), key);
	}

	/**
	 * 查看文件信息
	 *
	 * @param fileKey 文件名
	 * @return FileInfo
	 */
	@SneakyThrows
	public FileInfo stat(String fileKey) {
		return bucketManager.stat(getBucketName(), fileKey);
	}

	/**
	 * 获取上传凭证，普通上传
	 */
	public String getUploadToken() {
		return auth.uploadToken(getBucketName());
	}

	/**
	 * 获取上传凭证，覆盖上传
	 */
	private String getUploadToken(String key) {
		return auth.uploadToken(getBucketName(), key);
	}

}
