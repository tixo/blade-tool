package org.springblade.core.tool.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

/**
 * @version 1.0
 * @author：xy
 * @createTime：2019-09-25
 */

public class BigNumberModule extends SimpleModule {

	public BigNumberModule() {
		super();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

	}

}
