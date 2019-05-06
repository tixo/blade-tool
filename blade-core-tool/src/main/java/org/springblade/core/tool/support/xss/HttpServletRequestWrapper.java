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
package org.springblade.core.tool.support.xss;

import org.springblade.core.tool.utils.Charsets;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * request处理,缓存InputStream
 *
 * @author Chill
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

	/**
	 * 没被包装过的HttpServletRequest（特殊场景,需要自己过滤）
	 */
	private HttpServletRequest orgRequest;

	/**
	 * 缓存报文,支持多次读取流
	 */
	private final byte[] body;

	public HttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		orgRequest = request;
		body = WebUtil.getRequestBytes(request);
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		//为空，直接返回
		if (null == super.getHeader(HttpHeaders.CONTENT_TYPE)) {
			return super.getInputStream();
		}

		//非json类型，直接返回
		if (!super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)
			&& !super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
			return super.getInputStream();
		}

		//为空，直接返回
		String requestStr = WebUtil.getRequestStr(orgRequest, body);
		if (StringUtil.isBlank(requestStr)) {
			return super.getInputStream();
		}

		final ByteArrayInputStream bis = new ByteArrayInputStream(requestStr.getBytes(Charsets.UTF_8));

		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {

			}

			@Override
			public int read() throws IOException {
				return bis.read();
			}
		};

	}

	/**
	 * 获取最原始的request
	 *
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request
	 *
	 * @param request request
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
		if (request instanceof HttpServletRequestWrapper) {
			return ((HttpServletRequestWrapper) request).getOrgRequest();
		}
		return request;
	}

}
