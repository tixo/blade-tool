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
package org.springblade.core.log.event;


/**
 * 异步监听错误日志事件
 *
 * @author Chill
 */
//@Slf4j
//@AllArgsConstructor
public class ErrorLogListener {

//	private final ILogClient logService;
//	private final ServerInfo serverInfo;
//	private final BladeProperties bladeProperties;

	//	@Async
//	@Order
//	@EventListener(ErrorLogEvent.class)
	public void saveErrorLog(ErrorLogEvent event) {
//		Map<String, Object> source = (Map<String, Object>) event.getSource();
//		LogError logError = (LogError) source.get(EventConstant.EVENT_LOG);
//		HttpServletRequest request = (HttpServletRequest) source.get(EventConstant.EVENT_REQUEST);
//		logError.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
//		logError.setMethod(request.getMethod());
//		logError.setParams(WebUtil.getRequestParamString(request));
//		logError.setServiceId(bladeProperties.getName());
//		logError.setServerHost(serverInfo.getHostName());
//		logError.setServerIp(serverInfo.getIpWithPort());
//		logError.setEnv(bladeProperties.getEnv());
//		logError.setCreateBy(SecureUtil.getUserAccount(request));
//		logError.setCreateTime(DateUtil.now());
//		logService.saveErrorLog(logError);
	}

}
