/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
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
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */
package org.springblade.core.mp.db;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * record
 *
 * @author dream.lu
 */
public class Record implements Serializable {
	private Map<String, Object> columns = new LinkedCaseInsensitiveMap<>();

	private Record(Map<String, Object> dataMap) {
		this.columns.putAll(dataMap);
	}

	public static Record create(Map<String, Object> dataMap) {
		return new Record(dataMap);
	}

	public Map<String, Object> getColumns() {
		return this.columns;
	}

	/**
	 * Set columns value with Record.
	 *
	 * @param record the Record object
	 */
	public Record setColumns(Record record) {
		getColumns().putAll(record.getColumns());
		return this;
	}

	/**
	 * Remove attribute of this record.
	 *
	 * @param column the column name of the record
	 */
	public Record remove(String column) {
		getColumns().remove(column);
		return this;
	}

	/**
	 * Remove columns of this record.
	 *
	 * @param columns the column names of the record
	 */
	public Record remove(String... columns) {
		if (columns != null) {
			for (String c : columns) {
				this.getColumns().remove(c);
			}
		}
		return this;
	}

	/**
	 * Remove columns if it is null.
	 */
	public Record removeNullValueColumns() {
		getColumns().entrySet().removeIf(e -> e.getValue() == null);
		return this;
	}

	/**
	 * Remove all columns of this record.
	 */
	public Record clear() {
		getColumns().clear();
		return this;
	}

	/**
	 * Set column to record.
	 *
	 * @param column the column name
	 * @param value  the value of the column
	 */
	public Record set(String column, Object value) {
		getColumns().put(column, value);
		return this;
	}

	/**
	 * Get column of any mysql type
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String column) {
		return (T) getColumns().get(column);
	}

	/**
	 * Get column of any mysql type. Returns defaultValue if null.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String column, Object defaultValue) {
		Object result = getColumns().get(column);
		return (T) (result != null ? result : defaultValue);
	}

	public Object getObject(String column) {
		return getColumns().get(column);
	}

	public Object getObject(String column, Object defaultValue) {
		Object result = getColumns().get(column);
		return result != null ? result : defaultValue;
	}

	/**
	 * Get column of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
	 */
	public String getStr(String column) {
		Object s = getColumns().get(column);
		return s != null ? s.toString() : null;
	}

	/**
	 * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
	 */
	public Integer getInt(String column) {
		Number n = getNumber(column);
		return n != null ? n.intValue() : null;
	}

	/**
	 * Get column of mysql type: bigint
	 */
	public Long getLong(String column) {
		Number n = getNumber(column);
		return n != null ? n.longValue() : null;
	}

	/**
	 * Get column of mysql type: unsigned bigint
	 */
	public java.math.BigInteger getBigInteger(String column) {
		return (java.math.BigInteger) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: date, year
	 */
	public java.util.Date getDate(String column) {
		return (java.util.Date) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: time
	 */
	public java.sql.Time getTime(String column) {
		return (java.sql.Time) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: timestamp, datetime
	 */
	public java.sql.Timestamp getTimestamp(String column) {
		return (java.sql.Timestamp) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: real, double
	 */
	public Double getDouble(String column) {
		Number n = getNumber(column);
		return n != null ? n.doubleValue() : null;
	}

	/**
	 * Get column of mysql type: float
	 */
	public Float getFloat(String column) {
		Number n = getNumber(column);
		return n != null ? n.floatValue() : null;
	}

	public Short getShort(String column) {
		Number n = getNumber(column);
		return n != null ? n.shortValue() : null;
	}

	public Byte getByte(String column) {
		Number n = getNumber(column);
		return n != null ? n.byteValue() : null;
	}

	/**
	 * Get column of mysql type: bit, tinyint(1)
	 */
	public Boolean getBoolean(String column) {
		return (Boolean) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: decimal, numeric
	 */
	public java.math.BigDecimal getBigDecimal(String column) {
		return (java.math.BigDecimal) getColumns().get(column);
	}

	/**
	 * Get column of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
	 * I have not finished the test.
	 */
	public byte[] getBytes(String column) {
		return (byte[]) getColumns().get(column);
	}

	/**
	 * Get column of any type that extends from Number
	 */
	public Number getNumber(String column) {
		return (Number) getColumns().get(column);
	}

	@Override
	public String toString() {
		if (columns == null) {
			return "{}";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		boolean first = true;
		for (Map.Entry<String, Object> e : getColumns().entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			Object value = e.getValue();
			if (value != null) {
				value = value.toString();
			}
			sb.append(e.getKey()).append(':').append(value);
		}
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Record)) {
			return false;
		}
		if (o == this) {
			return true;
		}
		return getColumns().equals(((Record) o).getColumns());
	}

	@Override
	public int hashCode() {
		return getColumns().hashCode();
	}

	/**
	 * Return column names of this record.
	 */
	public String[] getColumnNames() {
		Set<String> attrNameSet = getColumns().keySet();
		return attrNameSet.toArray(new String[0]);
	}

	/**
	 * Return column values of this record.
	 */
	public Object[] getColumnValues() {
		java.util.Collection<Object> attrValueCollection = getColumns().values();
		return attrValueCollection.toArray(new Object[0]);
	}
}
