/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
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
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */
package org.springblade.core.mp.db;

import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * db 封装，方便拦截器中获取数据
 *
 * @author L.cm
 */
public class Db {
	private final SqlRunner sqlRunner;

	private Db(Connection connection) {
		this.sqlRunner = new SqlRunner(connection);
	}

	public static Db create(Connection connection) {
		return new Db(connection);
	}

	public List<Record> select(String sql, Object... paras) throws SQLException {
		List<Map<String, Object>> mapList = sqlRunner.selectAll(sql, paras);
		return mapList.stream()
			.map(Record::create)
			.collect(Collectors.toList());
	}

	public Record selectOne(String sql, Object... paras) throws SQLException {
		Map<String, Object> dataMap = sqlRunner.selectOne(sql, paras);
		if (dataMap == null) {
			return null;
		}
		return Record.create(dataMap);
	}
}
