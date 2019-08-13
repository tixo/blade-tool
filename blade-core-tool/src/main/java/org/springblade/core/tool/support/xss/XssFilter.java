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

import lombok.AllArgsConstructor;
import org.springblade.core.tool.utils.StringPool;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * XSS过滤
 *
 * @author Chill
 */
@AllArgsConstructor
public class XssFilter implements Filter {

	private XssProperties xssProperties;
	private XssUrlProperties xssUrlProperties;

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getServletPath();
		if (isSkip(path)) {
			chain.doFilter(request, response);
		} else {
			XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
			chain.doFilter(xssRequest, response);
		}
	}

	private boolean isSkip(String path) {
		return (xssUrlProperties.getExcludePatterns().stream().anyMatch(path::startsWith))
			|| (xssProperties.getSkipUrl().stream().map(url -> url.replace("/**", StringPool.EMPTY)).anyMatch(path::startsWith));
	}

	@Override
	public void destroy() {

	}

}
