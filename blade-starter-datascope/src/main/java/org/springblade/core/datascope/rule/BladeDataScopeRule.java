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

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.plugin.Invocation;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.datascope.constant.DataScopeConstant;
import org.springblade.core.datascope.enums.DataScopeEnum;
import org.springblade.core.datascope.model.DataScope;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 默认数据权限规则
 *
 * @author Chill
 */
@RequiredArgsConstructor
public class BladeDataScopeRule implements DataScopeRule {

	private static final String SCOPE_CACHE_CODE = "scope:code:";
	private static final String SCOPE_CACHE_CLASS = "scope:class:";
	private static final String DEPT_CACHE_ANCESTORS = "dept:ancestors:";

	private final JdbcTemplate jdbcTemplate;

	@Override
	public String whereSql(Invocation invocation, String mapperId, DataScope dataScope, BladeUser bladeUser) {

		//数据权限资源编号
		String code = dataScope.getCode();

		//根据mapperId从数据库中获取对应模型
		dataScope = getDataScopeByMapper(mapperId, bladeUser.getRoleId());

		//判断是否需要从数据库根据资源编号获取
		if (dataScope == null && StringUtil.isNotBlank(code)) {
			dataScope = getDataScopeByCode(code);
		}

		//未从数据库找到对应数据则放行
		if (dataScope == null) {
			return null;
		}

		//判断数据权限类型并组装对应Sql
		Integer scopeRule = Objects.requireNonNull(dataScope).getType();
		DataScopeEnum scopeTypeEnum = DataScopeEnum.of(scopeRule);
		List<Long> ids = new ArrayList<>();
		if (DataScopeEnum.ALL == scopeTypeEnum) {
			return null;
		} else if (DataScopeEnum.CUSTOM == scopeTypeEnum) {
			return PlaceholderUtil.getDefaultResolver().resolveByMap(dataScope.getValue(), BeanUtil.toMap(bladeUser));
		} else if (DataScopeEnum.OWN == scopeTypeEnum) {
			ids.add(bladeUser.getUserId());
		} else if (DataScopeEnum.OWN_DEPT == scopeTypeEnum) {
			ids.addAll(Func.toLongList(bladeUser.getDeptId()));
		} else if (DataScopeEnum.OWN_DEPT_CHILD == scopeTypeEnum) {
			List<Long> deptIds = Func.toLongList(bladeUser.getDeptId());
			ids.addAll(deptIds);
			deptIds.forEach(deptId -> {
				List<Long> deptIdList = getDeptAncestors(deptId);
				ids.addAll(deptIdList);
			});
		}
		return StringUtil.format(" where scope.{} in ({}) ", dataScope.getColumn(), StringUtil.join(ids));
	}

	/**
	 * 获取数据权限
	 *
	 * @param mapperId 数据权限mapperId
	 * @param roleId   用户角色集合
	 * @return DataScope
	 */
	private DataScope getDataScopeByMapper(String mapperId, String roleId) {
		List<Object> args = new ArrayList<>(Collections.singletonList(mapperId));
		List<Long> roleIds = Func.toLongList(roleId);
		args.addAll(roleIds);
		DataScope dataScope = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CLASS, mapperId + StringPool.COLON + roleId, DataScope.class);
		if (dataScope == null) {
			List<DataScope> list = jdbcTemplate.query(DataScopeConstant.dataByMapper(roleIds.size()), args.toArray(), new BeanPropertyRowMapper<>(DataScope.class));
			if (CollectionUtil.isNotEmpty(list)) {
				dataScope = list.iterator().next();
			}
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CLASS, mapperId + StringPool.COLON + roleId, dataScope);
		}
		return dataScope;
	}

	/**
	 * 获取数据权限
	 *
	 * @param code 数据权限资源编号
	 * @return DataScope
	 */
	private DataScope getDataScopeByCode(String code) {
		DataScope dataScope = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, code, DataScope.class);
		if (dataScope == null) {
			List<DataScope> list = jdbcTemplate.query(DataScopeConstant.DATA_BY_CODE, new Object[]{code}, new BeanPropertyRowMapper<>(DataScope.class));
			if (CollectionUtil.isNotEmpty(list)) {
				dataScope = list.iterator().next();
			}
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CODE, code, dataScope);
		}
		return dataScope;
	}

	/**
	 * 获取部门子级
	 *
	 * @param deptId 部门id
	 * @return deptIds
	 */
	private List<Long> getDeptAncestors(Long deptId) {
		List ancestors = CacheUtil.get(SYS_CACHE, DEPT_CACHE_ANCESTORS, deptId, List.class);
		if (CollectionUtil.isEmpty(ancestors)) {
			ancestors = jdbcTemplate.queryForList(DataScopeConstant.DATA_BY_DEPT, new Object[]{deptId}, Long.class);
			CacheUtil.put(SYS_CACHE, DEPT_CACHE_ANCESTORS, deptId, ancestors);
		}
		return ancestors;
	}

}
