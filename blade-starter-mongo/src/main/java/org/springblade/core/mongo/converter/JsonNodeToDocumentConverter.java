package org.springblade.core.mongo.converter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.Nullable;

/**
 * JsonNode 转 mongo Document
 *
 * @author L.cm
 */
@WritingConverter
public enum JsonNodeToDocumentConverter implements Converter<ObjectNode, Document> {
	/**
	 * 实例
	 */
	INSTANCE;

	@Override
	public Document convert(@Nullable ObjectNode source) {
		return source == null ? null : Document.parse(source.toString());
	}
}
