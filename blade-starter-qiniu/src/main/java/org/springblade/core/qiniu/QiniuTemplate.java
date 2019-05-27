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
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.model.OssFile;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * QiniuTemplate
 *
 * @author Chill
 */
@AllArgsConstructor
public class QiniuTemplate implements OssTemplate {
	private Auth auth;
	private UploadManager uploadManager;
	private BucketManager bucketManager;
	private OssProperties ossProperties;
	private OssRule qiniuRule;

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
		return qiniuRule.bucketName(bucketName);
	}

	/**
	 * 获取上传凭证，普通上传
	 */
	public String getUploadToken(String bucketName) {
		return auth.uploadToken(getBucketName(bucketName));
	}

	/**
	 * 获取上传凭证，覆盖上传
	 */
	private String getUploadToken(String bucketName, String key) {
		return auth.uploadToken(getBucketName(bucketName), key);
	}

	@Override
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (!CollectionUtil.contains(bucketManager.buckets(), getBucketName(bucketName))) {
			bucketManager.createBucket(getBucketName(bucketName), Zone.zone0().getRegion());
		}
	}

	@Override
	@SneakyThrows
	public void removeBucket(String bucketName) {
		bucketManager.deleteBucket(getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return CollectionUtil.contains(bucketManager.buckets(), getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String bucketName, String fileName) {
		FileInfo stat = bucketManager.stat(getBucketName(bucketName), fileName);
		OssFile ossFile = new OssFile();
		ossFile.setName(stat.key);
		ossFile.setName(Func.isEmpty(stat.key) ? fileName : stat.key);
		ossFile.setLink(fileLink(ossFile.getName()));
		ossFile.setHash(stat.hash);
		ossFile.setLength(stat.fsize);
		ossFile.setPutTime(new Date(stat.putTime / 10000));
		ossFile.setContentType(stat.mimeType);
		return ossFile;
	}

	@Override
	@SneakyThrows
	public String filePath(String fileName) {
		return getBucketName().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String filePath(String bucketName, String fileName) {
		return getBucketName(bucketName).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String bucketName, String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public void putFile(MultipartFile file) {
		putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@Override
	@SneakyThrows
	public void putFile(String fileName, MultipartFile file) {
		putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	@SneakyThrows
	public void putFile(String bucketName, String fileName, MultipartFile file) {
		putFile(bucketName, fileName, file);
	}

	@Override
	@SneakyThrows
	public void putFile(String fileName, InputStream stream) {
		putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	@SneakyThrows
	public void putFile(String bucketName, String fileName, InputStream stream) {
		put(bucketName, stream, fileName, false);
	}

	@SneakyThrows
	public Response put(String bucketName, InputStream stream, String key, boolean cover) {
		makeBucket(bucketName);
		Response response;
		// 覆盖上传
		if (cover) {
			response = uploadManager.put(stream, key, getUploadToken(bucketName, key), null, null);
		} else {
			response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
			int retry = 0;
			int retryCount = 5;
			while (response.needRetry() && retry < retryCount) {
				response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
				retry++;
			}
		}
		return response;
	}

	@Override
	@SneakyThrows
	public void removeFile(String fileName) {
		bucketManager.delete(getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFile(String bucketName, String fileName) {
		bucketManager.delete(getBucketName(bucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFiles(List<String> fileNames) {
		fileNames.forEach(this::removeFile);
	}

	@Override
	@SneakyThrows
	public void removeFiles(String bucketName, List<String> fileNames) {
		fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
	}
}
