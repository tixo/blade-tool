/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */
package org.springblade.core.datascope.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 *
 * @author lengleng, Chill
 */
@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum {
	/**
	 * 查询全部数据
	 */
	ALL(1, "全部"),

	/**
	 * 本人可见
	 */
	OWN_LEVEL(2, "本人可见"),

	/**
	 * 所在机构可见
	 */
	OWN_DEPT_LEVEL(3, "所在机构可见"),

	/**
	 * 所在机构及子级可见
	 */
	OWN_DEPT_CHILD_LEVEL(4, "所在机构及子级可见"),

	/**
	 * 自定义
	 */
	CUSTOM(5, "自定义");

	/**
	 * 类型
	 */
	private final int type;
	/**
	 * 描述
	 */
	private final String description;

	public static DataScopeTypeEnum of(Integer dataScopeType) {
		if (dataScopeType == null) {
			return null;
		}
		DataScopeTypeEnum[] values = DataScopeTypeEnum.values();
		for (DataScopeTypeEnum scopeTypeEnum : values) {
			if (scopeTypeEnum.type == dataScopeType) {
				return scopeTypeEnum;
			}
		}
		return null;
	}
}
