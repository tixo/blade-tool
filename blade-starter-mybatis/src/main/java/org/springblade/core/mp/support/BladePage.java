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

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页模型
 *
 * @author Chill
 */
@Data
public class BladePage<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 查询数据列表
	 */
	private List<T> records = Collections.emptyList();

	/**
	 * 总数
	 */
	private long total = 0;
	/**
	 * 每页显示条数，默认 10
	 */
	private long size = 10;

	/**
	 * 当前页
	 */
	private long current = 1;

	/**
	 * mybatis-plus分页模型转化
	 *
	 * @param page 分页实体类
	 * @return BladePage<T>
	 */
	public static <T> BladePage<T> of(IPage<T> page) {
		BladePage<T> bladePage = new BladePage<>();
		bladePage.setRecords(page.getRecords());
		bladePage.setTotal(page.getTotal());
		bladePage.setSize(page.getSize());
		bladePage.setCurrent(page.getCurrent());
		return bladePage;
	}

}
