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
package org.springblade.core.tenant;

import org.springblade.core.tool.utils.RandomType;
import org.springblade.core.tool.utils.StringUtil;

/**
 * blade租户id生成器
 *
 * @author Chill
 */
public class BladeTenantId implements TenantId {
	@Override
	public String generate() {
		return StringUtil.random(6, RandomType.INT);
	}
}
