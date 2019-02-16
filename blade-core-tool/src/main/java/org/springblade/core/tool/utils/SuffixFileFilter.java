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

package org.springblade.core.tool.utils;

import org.springframework.util.Assert;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * 文件后缀过滤器
 *
 * @author L.cm
 */
public class SuffixFileFilter implements FileFilter, Serializable {

	private static final long serialVersionUID = -3389157631240246157L;

	private final String[] suffixes;

	public SuffixFileFilter(final String suffix) {
		Assert.notNull(suffix, "The suffix must not be null");
		this.suffixes = new String[]{suffix};
	}

	public SuffixFileFilter(final String[] suffixes) {
		Assert.notNull(suffixes, "The suffix must not be null");
		this.suffixes = new String[suffixes.length];
		System.arraycopy(suffixes, 0, this.suffixes, 0, suffixes.length);
	}

	@Override
	public boolean accept(File pathname) {
		final String name = pathname.getName();
		for (final String suffix : this.suffixes) {
			if (checkEndsWith(name, suffix)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkEndsWith(final String str, final String end) {
		final int endLen = end.length();
		return str.regionMatches(true, str.length() - endLen, end, 0, endLen);
	}
}
