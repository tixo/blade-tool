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
package org.springblade.core.mp.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * 业务封装基础类
 *
 * @param <M> mapper
 * @param <T> model
 * @author Chill
 */
@Validated
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

	private Class<T> modelClass;

	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {
		Type type = this.getClass().getGenericSuperclass();
		this.modelClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[1];
	}

	@Override
	public boolean save(T entity) {
		BladeUser user = SecureUtil.getUser();
		assert user != null;
		Date now = DateUtil.now();
		entity.setCreateUser(user.getUserId());
		entity.setCreateDept(Func.toLongList(user.getDeptId()).iterator().next());
		entity.setCreateTime(now);
		entity.setUpdateUser(user.getUserId());
		entity.setUpdateTime(now);
		if (entity.getStatus() == null) {
			entity.setStatus(BladeConstant.DB_STATUS_NORMAL);
		}
		entity.setIsDeleted(BladeConstant.DB_NOT_DELETED);
		return super.save(entity);
	}

	@Override
	public boolean updateById(T entity) {
		BladeUser user = SecureUtil.getUser();
		assert user != null;
		entity.setUpdateUser(user.getUserId());
		entity.setUpdateTime(DateUtil.now());
		return super.updateById(entity);
	}

	@Override
	public boolean deleteLogic(@NotEmpty List<Long> ids) {
		BladeUser user = SecureUtil.getUser();
		assert user != null;
		T entity = BeanUtil.newInstance(modelClass);
		entity.setUpdateUser(user.getUserId());
		entity.setUpdateTime(DateUtil.now());
		return super.update(entity, Wrappers.<T>update().lambda().in(T::getId, ids)) && super.removeByIds(ids);
	}

}
