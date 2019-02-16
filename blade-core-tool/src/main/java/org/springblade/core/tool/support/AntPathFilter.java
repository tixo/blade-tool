package org.springblade.core.tool.support;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * 文件过滤
 *
 * @author Chill
 */
public class AntPathFilter implements FileFilter, Serializable {
	private static final long serialVersionUID = 812598009067554612L;

	private static PathMatcher pathMatcher = new AntPathMatcher();

	private final String pattern;

	public AntPathFilter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(File pathname) {
		String filePath = pathname.getAbsolutePath();
		return pathMatcher.match(pattern, filePath);
	}
}
