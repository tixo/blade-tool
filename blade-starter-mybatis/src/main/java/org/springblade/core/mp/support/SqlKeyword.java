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
package org.springblade.core.mp.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.util.Map;

/**
 * 定义常用的 sql关键字
 *
 * @author Chill
 */
public class SqlKeyword {
	private final static String SQL_REGEX = "'|%|--|insert|delete|update|select|count|group|union|drop|truncate|alter|grant|execute|exec|xp_cmdshell|call|declare|sql";

	private static final String EQUAL = "_equal";
	private static final String NOT_EQUAL = "_notequal";
	private static final String LIKE = "_like";
	private static final String NOT_LIKE = "_notlike";
	private static final String GE = "_ge";
	private static final String LE = "_le";
	private static final String GT = "_gt";
	private static final String LT = "_lt";
	private static final String DATE_GE = "_datege";
	private static final String DATE_GT = "_dategt";
	private static final String DATE_EQUAL = "_dateequal";
	private static final String DATE_LT = "_datelt";
	private static final String DATE_LE = "_datele";
	private static final String IS_NULL = "_null";
	private static final String NOT_NULL = "_notnull";
	private static final String IGNORE = "_ignore";

	/**
	 * 条件构造器
	 *
	 * @param query 查询字段
	 * @param qw    查询包装类
	 */
	public static void buildCondition(Map<String, Object> query, QueryWrapper<?> qw) {
		if (Func.isEmpty(query)) {
			return;
		}
		query.forEach((k, v) -> {
			if (Func.hasEmpty(k, v) || k.endsWith(IGNORE)) {
				return;
			}
			if (k.endsWith(EQUAL)) {
				qw.eq(getColumn(k, EQUAL), v);
			} else if (k.endsWith(NOT_EQUAL)) {
				qw.ne(getColumn(k, NOT_EQUAL), v);
			} else if (k.endsWith(NOT_LIKE)) {
				qw.notLike(getColumn(k, NOT_LIKE), v);
			} else if (k.endsWith(GE)) {
				qw.ge(getColumn(k, GE), v);
			} else if (k.endsWith(LE)) {
				qw.le(getColumn(k, LE), v);
			} else if (k.endsWith(GT)) {
				qw.gt(getColumn(k, GT), v);
			} else if (k.endsWith(LT)) {
				qw.lt(getColumn(k, LT), v);
			}  else if (k.endsWith(DATE_GE)) {
				qw.ge(getColumn(k, DATE_GE), v);
			} else if (k.endsWith(DATE_GT)) {
				qw.gt(getColumn(k, DATE_GT), v);
			} else if (k.endsWith(DATE_EQUAL)) {
				qw.eq(getColumn(k, DATE_EQUAL), v);
			} else if (k.endsWith(DATE_LE)) {
				qw.le(getColumn(k, DATE_LE), v);
			} else if (k.endsWith(DATE_LT)) {
				qw.lt(getColumn(k, DATE_LT), v);
			} else if (k.endsWith(IS_NULL)) {
				qw.isNull(getColumn(k, IS_NULL));
			} else if (k.endsWith(NOT_NULL)) {
				qw.isNotNull(getColumn(k, NOT_NULL));
			} else {
				qw.like(getColumn(k, LIKE), v);
			}
		});
	}

	/**
	 * 获取数据库字段
	 *
	 * @param column  字段名
	 * @param keyword 关键字
	 * @return
	 */
	private static String getColumn(String column, String keyword) {
		return StringUtil.humpToUnderline(StringUtil.removeSuffix(column, keyword));
	}

	/**
	 * 把SQL关键字替换为空字符串
	 *
	 * @param param 关键字
	 * @return string
	 */
	public static String filter(String param) {
		if (param == null) {
			return null;
		}
		return param.replaceAll("(?i)" + SQL_REGEX, StringPool.EMPTY);
	}

}
