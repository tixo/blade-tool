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
package org.springblade.core.secure.constant;

/**
 * PreAuth权限表达式
 *
 * @author Chill
 */
public interface AuthConstant {

	/**
	 * 超管别名
	 */
	String ADMINISTRATOR = "administrator";

	/**
	 * 是有超管角色
	 */
	String HAS_ROLE_ADMINISTRATOR = "hasRole('" + ADMINISTRATOR + "')";

	/**
	 * 管理员别名
	 */
	String ADMIN = "admin";

	/**
	 * 是否有管理员角色
	 */
	String HAS_ROLE_ADMIN = "hasAnyRole('" + ADMINISTRATOR + "', '" + ADMIN + "')";

	/**
	 * 用户别名
	 */
	String USER = "user";

	/**
	 * 是否有用户角色
	 */
	String HAS_ROLE_USER = "hasRole('" + USER + "')";

	/**
	 * 测试别名
	 */
	String TEST = "test";

	/**
	 * 是否有测试角色
	 */
	String HAS_ROLE_TEST = "hasRole('" + TEST + "')";

	/**
	 * 只有超管才能访问
	 */
	String DENY_ALL = "denyAll()";

	/**
	 * 对所有请求进行接口权限校验
	 */
	String PERMISSION_ALL = "permissionAll()";

}
