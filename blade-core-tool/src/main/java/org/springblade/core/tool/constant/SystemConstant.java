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
package org.springblade.core.tool.constant;


import lombok.Data;

/**
 * Blade系统配置类
 *
 * @author Chill
 */
@Data
public class SystemConstant {

	/**
	 * 开发模式
	 */
	private boolean devMode = false;

	/**
	 * 远程上传模式
	 */
	private boolean remoteMode = false;

	/**
	 * 外网地址
	 */
	private String domain = "http://localhost:8888";

	/**
	 * 上传下载路径(物理路径)
	 */
	private String remotePath = System.getProperty("user.dir") + "/target/blade";

	/**
	 * 上传路径(相对路径)
	 */
	private String uploadPath = "/upload";

	/**
	 * 下载路径
	 */
	private String downloadPath = "/download";

	/**
	 * 图片压缩
	 */
	private boolean compress = false;

	/**
	 * 图片压缩比例
	 */
	private Double compressScale = 2.00;

	/**
	 * 图片缩放选择:true放大;false缩小
	 */
	private boolean compressFlag = false;

	/**
	 * 项目物理路径
	 */
	private String realPath = System.getProperty("user.dir");

	/**
	 * 项目相对路径
	 */
	private String contextPath = "/";

	private static final SystemConstant ME = new SystemConstant();

	private SystemConstant() {

	}

	public static SystemConstant me() {
		return ME;
	}

	public String getUploadRealPath() {
		return (remoteMode ? remotePath : realPath) + uploadPath;
	}

	public String getUploadCtxPath() {
		return contextPath + uploadPath;
	}

}
