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
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.datascope.enums.DataScopeTypeEnum;
import org.springblade.core.datascope.exception.DataScopeException;
import org.springblade.core.datascope.model.DataScope;
import org.springblade.core.mp.db.Db;
import org.springblade.core.mp.db.Record;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.PlaceholderUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 默认数据权限规则
 *
 * @author Chill
 */
public class BladeDataScopeRule implements DataScopeRule {

	private static final String SCOPE_CACHE_CODE = "scope:code:";
	private static final String DEPT_CACHE_ID = "dept:ancestors:id:";

	@Override
	public String whereSql(Invocation invocation, DataScope dataScope, BladeUser bladeUser) {
		//判断用户数据是否存在
		if (bladeUser == null) {
			throw new DataScopeException("failed to obtain user information, data Scope settings failed.");
		}

		//开启数据库链接
		Connection connection = (Connection) invocation.getArgs()[0];
		Db db = Db.create(connection);

		//判断是否需要从数据库获取
		if (StringUtil.isNotBlank(dataScope.getResourceCode())) {
			dataScope = getDataScope(db, dataScope);
			//未从数据库找到对应数据则放行
			if (dataScope == null) {
				return null;
			}
		}

		//判断数据权限类型并组装对应Sql
		Integer scopeRule = Objects.requireNonNull(dataScope).getScopeType();
		DataScopeTypeEnum scopeTypeEnum = DataScopeTypeEnum.of(scopeRule);
		List<Long> ids = new ArrayList<>();
		if (DataScopeTypeEnum.ALL == scopeTypeEnum) {
			return null;
		} else if (DataScopeTypeEnum.CUSTOM == scopeTypeEnum) {
			return PlaceholderUtil.getDefaultResolver().resolveByMap(dataScope.getScopeValue(), BeanUtil.toMap(bladeUser));
		} else if (DataScopeTypeEnum.OWN_LEVEL == scopeTypeEnum) {
			ids.add(bladeUser.getUserId());
		} else if (DataScopeTypeEnum.OWN_DEPT_LEVEL == scopeTypeEnum) {
			ids.addAll(Func.toLongList(bladeUser.getDeptId()));
		} else if (DataScopeTypeEnum.OWN_DEPT_CHILD_LEVEL == scopeTypeEnum) {
			List<Long> deptIds = Func.toLongList(bladeUser.getDeptId());
			ids.addAll(deptIds);
			deptIds.forEach(deptId -> {
				List<Long> deptIdList = getDeptAncestors(db, deptId);
				ids.addAll(deptIdList);
			});
		}
		return StringUtil.format(" where scope.{} in ({}) ", dataScope.getScopeColumn(), StringUtil.join(ids));
	}


	/**
	 * 获取数据权限
	 *
	 * @param db        db工具栏
	 * @param dataScope 数据权限实体类
	 * @return DataScope
	 */
	private DataScope getDataScope(Db db, DataScope dataScope) {
		return CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, dataScope.getResourceCode(), () -> {
			Record record = db.selectOne("select * from blade_data_scope where resource_code = ?", dataScope.getResourceCode());
			dataScope.setScopeColumn(record.getStr("scope_column"));
			dataScope.setScopeType(record.getInt("scope_type"));
			dataScope.setScopeValue(record.getStr("scope_value"));
			return dataScope;
		});
	}

	/**
	 * 获取部门子级
	 *
	 * @param db     db工具类
	 * @param deptId 部门id
	 * @return deptIds
	 */
	private List<Long> getDeptAncestors(Db db, Long deptId) {
		return CacheUtil.get(SYS_CACHE, DEPT_CACHE_ID, deptId, () -> {
			try {
				return db.select("select id from blade_dept where ancestors like concat('%', ?, '%')", deptId).stream()
					.map(record -> record.getLong("id"))
					.collect(Collectors.toList());
			} catch (SQLException e) {
				return new ArrayList<>();
			}
		});
	}

}
