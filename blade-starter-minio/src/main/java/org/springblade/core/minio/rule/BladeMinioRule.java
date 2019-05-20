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
package org.springblade.core.minio.rule;

import lombok.AllArgsConstructor;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.FileUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 默认存储桶生成规则
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeMinioRule implements IMinioRule {

	/**
	 * 租户模式
	 */
	private Boolean tenantMode;

	@Override
	public String bucketName(String bucketName) {
		String prefix = (tenantMode) ? SecureUtil.getTenantCode().concat(StringPool.DASH) : StringPool.EMPTY;
		return prefix + bucketName;
	}

	@Override
	public String fileName(MultipartFile file) {
		return StringUtil.randomUUID() + StringPool.DOT + FileUtil.getFileExtension(file.getOriginalFilename());
	}

}
