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
package org.springblade.core.datascope.rule;

import org.apache.ibatis.plugin.Invocation;
import org.springblade.core.datascope.model.DataScope;
import org.springblade.core.secure.BladeUser;

/**
 * 数据权限规则
 *
 * @author Chill
 */
public interface DataScopeRule {

	/**
	 * 获取过滤sql
	 *
	 * @param invocation 过滤器信息
	 * @param mapperId   数据查询类
	 * @param dataScope  数据权限类
	 * @param bladeUser  当前用户信息
	 * @return sql
	 */
	String whereSql(Invocation invocation, String mapperId, DataScope dataScope, BladeUser bladeUser);

}
