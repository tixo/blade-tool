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
package org.springblade.core.datascope.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springblade.core.datascope.annotation.DataAuth;
import org.springblade.core.datascope.model.DataScope;
import org.springblade.core.datascope.rule.DataScopeRule;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;


/**
 * mybatis 数据权限拦截器
 *
 * @author lengleng, L.cm, Chill
 */
@Slf4j
@AllArgsConstructor
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataScopeInterceptor extends AbstractSqlParserHandler implements Interceptor {

	private DataScopeRule dataScopeRule;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
		MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
		this.sqlParser(metaObject);

		// 非SELECT操作放行
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
			|| StatementType.CALLABLE == mappedStatement.getStatementType()) {
			return invocation.proceed();
		}

		BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
		String originalSql = boundSql.getSql();
		Object parameterObject = boundSql.getParameterObject();

		//查找参数中包含DataScope类型的参数
		DataScope dataScope = findDataScopeObject(parameterObject);
		//查找注解中包含DataAuth类型的参数
		DataAuth dataAuth = findDataAuthAnnotation(mappedStatement);
		//都为空则放行
		if (dataScope == null && dataAuth == null) {
			return invocation.proceed();
		}
		//若注解不为空,优先注解配置
		if (dataAuth != null) {
			dataScope = new DataScope();
			dataScope.setResourceCode(dataAuth.resourceCode());
			dataScope.setScopeColumn(dataAuth.scopeColumn());
			dataScope.setScopeType(dataAuth.scopeType());
			dataScope.setScopeValue(dataAuth.scopeValue());
		}

		//获取数据权限规则对应的筛选Sql
		String whereSql = dataScopeRule.whereSql(invocation, dataScope, SecureUtil.getUser());
		if (StringUtil.isBlank(whereSql)) {
			return invocation.proceed();
		} else {
			String newOriginalSql = "select * from (" + originalSql + ") scope " + whereSql;
			metaObject.setValue("delegate.boundSql.sql", newOriginalSql);
			return invocation.proceed();
		}
	}

	/**
	 * 生成拦截对象的代理
	 *
	 * @param target 目标对象
	 * @return 代理对象
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	/**
	 * mybatis配置的属性
	 *
	 * @param properties mybatis配置的属性
	 */
	@Override
	public void setProperties(Properties properties) {

	}

	/**
	 * 查找参数是否包括DataScope对象
	 *
	 * @param parameterObj 参数列表
	 * @return DataScope
	 */
	private DataScope findDataScopeObject(Object parameterObj) {
		if (parameterObj instanceof DataScope) {
			return (DataScope) parameterObj;
		} else if (parameterObj instanceof Map) {
			for (Object val : ((Map<?, ?>) parameterObj).values()) {
				if (val instanceof DataScope) {
					return (DataScope) val;
				}
			}
		}
		return null;
	}

	/**
	 * 获取数据权限注解信息
	 *
	 * @param mappedStatement mappedStatement
	 * @return DataAuth
	 */
	private DataAuth findDataAuthAnnotation(MappedStatement mappedStatement) {
		DataAuth dataAuth = null;
		try {
			String id = mappedStatement.getId();
			String className = id.substring(0, id.lastIndexOf(StringPool.DOT));
			String methodName = id.substring(id.lastIndexOf(StringPool.DOT) + 1);
			final Class<?> cls = Class.forName(className);
			final Method[] method = cls.getMethods();
			for (Method me : method) {
				if (me.getName().equals(methodName) && me.isAnnotationPresent(DataAuth.class)) {
					dataAuth = me.getAnnotation(DataAuth.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataAuth;
	}

}
