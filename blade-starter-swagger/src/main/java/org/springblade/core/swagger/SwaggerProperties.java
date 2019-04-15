/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */
package org.springblade.core.swagger;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * SwaggerProperties
 *
 * @author Chill
 */
@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {
	/**
	 * swagger会解析的包路径
	 **/
	private String basePackage = "org.springblade";
	/**
	 * swagger会解析的url规则
	 **/
	private List<String> basePath = new ArrayList<>();
	/**
	 * 在basePath基础上需要排除的url规则
	 **/
	private List<String> excludePath = new ArrayList<>();
	/**
	 * 标题
	 **/
	private String title = "BladeX 接口文档系统";
	/**
	 * 描述
	 **/
	private String description = "BladeX 接口文档系统";
	/**
	 * 版本
	 **/
	private String version = "2.0.0";
	/**
	 * 许可证
	 **/
	private String license = "Powered By BladeX";
	/**
	 * 许可证URL
	 **/
	private String licenseUrl = "https://bladex.vip";
	/**
	 * 服务条款URL
	 **/
	private String termsOfServiceUrl = "https://bladex.vip";

	/**
	 * host信息
	 **/
	private String host = "";
	/**
	 * 联系人信息
	 */
	private Contact contact = new Contact();
	/**
	 * 全局统一鉴权配置
	 **/
	private Authorization authorization = new Authorization();

	@Data
	@NoArgsConstructor
	public static class Contact {

		/**
		 * 联系人
		 **/
		private String name = "chillzhuang";
		/**
		 * 联系人url
		 **/
		private String url = "https://gitee.com/smallc";
		/**
		 * 联系人email
		 **/
		private String email = "smallchill@163.com";

	}

	@Data
	@NoArgsConstructor
	public static class Authorization {

		/**
		 * 鉴权策略ID，需要和SecurityReferences ID保持一致
		 */
		private String name = "";

		/**
		 * 需要开启鉴权URL的正则
		 */
		private String authRegex = "^.*$";

		/**
		 * 鉴权作用域列表
		 */
		private List<AuthorizationScope> authorizationScopeList = new ArrayList<>();

		private List<String> tokenUrlList = new ArrayList<>();
	}

	@Data
	@NoArgsConstructor
	public static class AuthorizationScope {

		/**
		 * 作用域名称
		 */
		private String scope = "";

		/**
		 * 作用域描述
		 */
		private String description = "";

	}
}
