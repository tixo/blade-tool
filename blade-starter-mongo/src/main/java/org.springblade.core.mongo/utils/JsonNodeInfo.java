package org.springblade.core.mongo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * json tree 节点信息
 *
 * @author L.cm
 */
public class JsonNodeInfo {
	/**
	 * mongo keys: class1.class2.item
	 */
	private volatile String nodeKeys;
	/**
	 * jsonPath语法：/class1/class2/item
	 */
	private volatile String nodePath;
	/**
	 * 节点关系
	 */
	@Getter
	private final LinkedList<String> elements;
	/**
	 * tree 的 叶子节点，此处为引用
	 */
	@Getter
	private final JsonNode leafNode;

	public JsonNodeInfo(LinkedList<String> elements, JsonNode leafNode) {
		Assert.notNull(elements, "elements can not be null.");
		this.nodeKeys = null;
		this.nodePath = null;
		this.elements = elements;
		this.leafNode = leafNode;
	}

	/**
	 * 获取 mongo db的 key 语法
	 * @return mongo db的 key 语法
	 */
	public String getNodeKeys() {
		if (nodeKeys == null) {
			synchronized (this) {
				if (nodeKeys == null) {
					StringJoiner nodeKeysJoiner = new StringJoiner(".");
					elements.forEach(nodeKeysJoiner::add);
					nodeKeys = nodeKeysJoiner.toString();
				}
			}
		}
		return nodeKeys;
	}

	/**
	 * 获取 json path 语法路径
	 * @return jsonPath 路径
	 */
	public String getNodePath() {
		if (nodePath == null) {
			synchronized (this) {
				if (nodePath == null) {
					StringJoiner nodePathJoiner = new StringJoiner("/", "/", "");
					elements.forEach(nodePathJoiner::add);
					nodePath = nodePathJoiner.toString();
				}
			}
		}
		return nodePath;
	}

	/**
	 * 获取第一个元素
	 * @return element
	 */
	public String getFirst() {
		return elements.getFirst();
	}

}
