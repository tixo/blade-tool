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
package org.springblade.core.secure.handler;

import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.constant.PermissionConstant;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 默认授权校验类
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladePermissionHandler implements IPermissionHandler {

	private static final String SCOPE_CACHE_CODE = "apiScope:code:";

	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean permissionAll() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return false;
		}
		String uri = request.getRequestURI();
		List<String> paths = permissionCodes(StringPool.EMPTY);
		if (paths == null || paths.size() == 0) {
			return false;
		}
		return paths.stream().anyMatch(uri::contains);
	}

	@Override
	public boolean hasPermission(String permission) {
		List<String> codes = permissionCodes(permission);
		return codes != null && codes.size() != 0;
	}

	/**
	 * 获取接口权限信息
	 *
	 * @param permission 权限编号
	 * @return permissions
	 */
	private List<String> permissionCodes(String permission) {
		BladeUser user = SecureUtil.getUser();
		if (user == null) {
			return null;
		}
		String permissionPrefix = StringUtil.isBlank(permission) ? StringPool.EMPTY : permission + StringPool.COLON;
		List<String> permissions = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, permissionPrefix + user.getRoleId(), List.class);
		if (permissions == null) {
			List<Object> args = new ArrayList<>();
			if (StringUtil.isNotBlank(permission)) {
				args.add(permission);
			}
			List<Long> roleIds = Func.toLongList(user.getRoleId());
			args.addAll(roleIds);
			String sql = StringUtil.isBlank(permission) ? PermissionConstant.permissionAllStatement(roleIds.size()) : PermissionConstant.permissionStatement(roleIds.size());
			permissions = jdbcTemplate.queryForList(sql, args.toArray(), String.class);
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CODE, user.getRoleId(), permissions);
		}
		return permissions;
	}

}
