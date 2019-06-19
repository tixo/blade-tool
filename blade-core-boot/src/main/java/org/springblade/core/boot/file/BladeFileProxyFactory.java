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
package org.springblade.core.boot.file;

import org.springblade.core.tool.constant.SystemConstant;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.ImageUtil;
import org.springblade.core.tool.utils.StringPool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 文件代理类
 *
 * @author Chill
 */
public class BladeFileProxyFactory implements IFileProxy {

	@Override
	public File rename(File f, String path) {
		File dest = new File(path);
		f.renameTo(dest);
		return dest;
	}

	@Override
	public String[] path(File f, String dir) {
		//避免网络延迟导致时间不同步
		long time = System.nanoTime();

		StringBuilder uploadPath = new StringBuilder()
			.append(getFileDir(dir, SystemConstant.me().getUploadRealPath()))
			.append(time)
			.append(getFileExt(f.getName()));

		StringBuilder virtualPath = new StringBuilder()
			.append(getFileDir(dir, SystemConstant.me().getUploadCtxPath()))
			.append(time)
			.append(getFileExt(f.getName()));

		return new String[]{BladeFileUtil.formatUrl(uploadPath.toString()), BladeFileUtil.formatUrl(virtualPath.toString())};
	}

	/**
	 * 获取文件后缀
	 *
	 * @param fileName 文件名
	 * @return 文件后缀
	 */
	public static String getFileExt(String fileName) {
		if (!fileName.contains(StringPool.DOT)) {
			return ".jpg";
		} else {
			return fileName.substring(fileName.lastIndexOf(StringPool.DOT));
		}
	}

	/**
	 * 获取文件保存地址
	 *
	 * @param dir     目录
	 * @param saveDir 保存目录
	 * @return 地址
	 */
	public static String getFileDir(String dir, String saveDir) {
		StringBuilder newFileDir = new StringBuilder();
		newFileDir.append(saveDir)
			.append(File.separator).append(dir).append(File.separator).append(DateUtil.format(DateUtil.now(), "yyyyMMdd"))
			.append(File.separator);
		return newFileDir.toString();
	}


	/**
	 * 图片压缩
	 *
	 * @param path 文件地址
	 */
	@Override
	public void compress(String path) {
		try {
			ImageUtil.zoomScale(ImageUtil.readImage(path), new FileOutputStream(new File(path)), null, SystemConstant.me().getCompressScale(), SystemConstant.me().isCompressFlag());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
