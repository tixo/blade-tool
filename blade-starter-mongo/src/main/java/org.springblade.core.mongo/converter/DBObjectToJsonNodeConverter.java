package org.springblade.core.mongo.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.bson.BasicBSONObject;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;

/**
 * mongo DBObject 转 jsonNode
 *
 * @author L.cm
 */
@ReadingConverter
public enum DBObjectToJsonNodeConverter implements Converter<BasicBSONObject, JsonNode> {
	/**
	 * 实例
	 */
	INSTANCE;

	@Override
	public JsonNode convert(@Nullable BasicBSONObject source) {
		if (source == null) {
			return null;
		}
		return JsonUtil.getInstance().valueToTree(source);
	}
}

