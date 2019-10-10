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

package org.springblade.core.log.publisher;

import org.springblade.core.log.annotation.ApiLog;

/**
 * API日志信息事件发送
 *
 * @author Chill
 */
public class ApiLogPublisher {

	public static void publishEvent(String methodName, String methodClass, ApiLog apiLog, long time) {
//		HttpServletRequest request = WebUtil.getRequest();
//		LogApi logApi = new LogApi();
//		logApi.setType(BladeConstant.LOG_NORMAL_TYPE);
//		logApi.setTitle(apiLog.value());
//		logApi.setTime(String.valueOf(time));
//		logApi.setMethodClass(methodClass);
//		logApi.setMethodName(methodName);
//		Map<String, Object> event = new HashMap<>(16);
//		event.put(EventConstant.EVENT_LOG, logApi);
//		event.put(EventConstant.EVENT_REQUEST, request);
//		SpringUtil.publishEvent(new ApiLogEvent(event));
	}

}
