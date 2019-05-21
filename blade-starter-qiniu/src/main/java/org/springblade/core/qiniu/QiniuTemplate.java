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

import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.qiniu.props.QiniuProperties;

import java.io.File;
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

	/**
	 * 上传文件
	 *
	 * @param file 文件
	 * @param key  文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response put(File file, String key) {
		return put(file, key, false);
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
	public Response put(File file, String key, boolean cover) {
		Response response;
		// 覆盖上传
		if (cover) {
			response = uploadManager.put(file, key, getUploadToken(key));
		} else {
			response = uploadManager.put(file, key, getUploadToken());
			int retry = 0;
			while (response.needRetry() && retry < qiniuProperties.getRetry()) {
				response = uploadManager.put(file, key, getUploadToken());
				retry++;
			}
		}
		return response;
	}

	/**
	 * 上传文件
	 *
	 * @param stream 文件流
	 * @param key    文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response put(InputStream stream, String key) {
		return put(stream, key, false);
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
	public Response put(InputStream stream, String key, boolean cover) {
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
	 * 删除文件
	 *
	 * @param key 文件名
	 * @return Response
	 */
	@SneakyThrows
	public Response delete(String key) {
		return bucketManager.delete(qiniuProperties.getBucketName(), key);
	}

	/**
	 * 查看文件信息
	 *
	 * @param fileKey 文件名
	 * @return FileInfo
	 */
	@SneakyThrows
	public FileInfo stat(String fileKey) {
		return bucketManager.stat(qiniuProperties.getBucketName(), fileKey);
	}

	/**
	 * 获取上传凭证，普通上传
	 */
	public String getUploadToken() {
		return auth.uploadToken(qiniuProperties.getBucketName());
	}

	/**
	 * 获取上传凭证，覆盖上传
	 */
	private String getUploadToken(String key) {
		return auth.uploadToken(qiniuProperties.getBucketName(), key);
	}

}
