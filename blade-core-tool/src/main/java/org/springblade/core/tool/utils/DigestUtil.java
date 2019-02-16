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

package org.springblade.core.tool.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密相关工具类直接使用Spring util封装，减少jar依赖
 *
 * @author L.cm
 */
@UtilityClass
public class DigestUtil extends org.springframework.util.DigestUtils {
	private static final char[] HEX_CODE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(final String data) {
		return DigestUtils.md5DigestAsHex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * Return a hexadecimal string representation of the MD5 digest of the given bytes.
	 *
	 * @param bytes the bytes to calculate the digest over
	 * @return a hexadecimal digest string
	 */
	public static String md5Hex(final byte[] bytes) {
		return DigestUtils.md5DigestAsHex(bytes);
	}

	/**
	 * sha1Hex
	 *
	 * @param data Data to digest
	 * @return digest as a hex string
	 */
	public static String sha1Hex(String data) {
		return DigestUtil.sha1Hex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * sha1Hex
	 *
	 * @param bytes Data to digest
	 * @return digest as a hex string
	 */
	public static String sha1Hex(final byte[] bytes) {
		return DigestUtil.digestHex("SHA-1", bytes);
	}

	/**
	 * SHA224Hex
	 *
	 * @param data Data to digest
	 * @return digest as a hex string
	 */
	public static String sha224Hex(String data) {
		return DigestUtil.sha224Hex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * SHA224Hex
	 *
	 * @param bytes Data to digest
	 * @return digest as a hex string
	 */
	public static String sha224Hex(final byte[] bytes) {
		return DigestUtil.digestHex("SHA-224", bytes);
	}

	/**
	 * sha256Hex
	 *
	 * @param data Data to digest
	 * @return digest as a hex string
	 */
	public static String sha256Hex(String data) {
		return DigestUtil.sha256Hex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * sha256Hex
	 *
	 * @param bytes Data to digest
	 * @return digest as a hex string
	 */
	public static String sha256Hex(final byte[] bytes) {
		return DigestUtil.digestHex("SHA-256", bytes);
	}

	/**
	 * sha384Hex
	 *
	 * @param data Data to digest
	 * @return digest as a hex string
	 */
	public static String sha384Hex(String data) {
		return DigestUtil.sha384Hex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * sha384Hex
	 *
	 * @param bytes Data to digest
	 * @return digest as a hex string
	 */
	public static String sha384Hex(final byte[] bytes) {
		return DigestUtil.digestHex("SHA-384", bytes);
	}

	/**
	 * sha512Hex
	 *
	 * @param data Data to digest
	 * @return digest as a hex string
	 */
	public static String sha512Hex(String data) {
		return DigestUtil.sha512Hex(data.getBytes(Charsets.UTF_8));
	}

	/**
	 * sha512Hex
	 *
	 * @param bytes Data to digest
	 * @return digest as a hex string
	 */
	public static String sha512Hex(final byte[] bytes) {
		return DigestUtil.digestHex("SHA-512", bytes);
	}

	/**
	 * digest Hex
	 *
	 * @param algorithm 算法
	 * @param bytes     Data to digest
	 * @return digest as a hex string
	 */
	public static String digestHex(String algorithm, byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			return encodeHex(md.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * hmacMd5 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacMd5Hex(String data, String key) {
		return DigestUtil.hmacMd5Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacMd5 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacMd5Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacMD5", bytes, key);
	}

	/**
	 * hmacSha1 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha1Hex(String data, String key) {
		return DigestUtil.hmacSha1Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha1 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha1Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacSHA1", bytes, key);
	}

	/**
	 * hmacSha224 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha224Hex(String data, String key) {
		return DigestUtil.hmacSha224Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha224 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha224Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacSHA224", bytes, key);
	}

	/**
	 * hmacSha256 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha256Hex(String data, String key) {
		return DigestUtil.hmacSha256Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha256 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha256Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacSHA256", bytes, key);
	}

	/**
	 * hmacSha384 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha384Hex(String data, String key) {
		return DigestUtil.hmacSha384Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha384 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha384Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacSHA384", bytes, key);
	}

	/**
	 * hmacSha512 Hex
	 *
	 * @param data Data to digest
	 * @param key  key
	 * @return digest as a hex string
	 */
	public static String hmacSha512Hex(String data, String key) {
		return DigestUtil.hmacSha512Hex(data.getBytes(Charsets.UTF_8), key);
	}

	/**
	 * hmacSha512 Hex
	 *
	 * @param bytes Data to digest
	 * @param key   key
	 * @return digest as a hex string
	 */
	public static String hmacSha512Hex(final byte[] bytes, String key) {
		return DigestUtil.digestHMacHex("HmacSHA512", bytes, key);
	}

	/**
	 * digest HMac Hex
	 *
	 * @param algorithm 算法
	 * @param bytes     Data to digest
	 * @return digest as a hex string
	 */
	public static String digestHMacHex(String algorithm, final byte[] bytes, String key) {
		SecretKey secretKey = new SecretKeySpec(key.getBytes(Charsets.UTF_8), algorithm);
		try {
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			return DigestUtil.encodeHex(mac.doFinal(bytes));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * encode Hex
	 *
	 * @param bytes Data to Hex
	 * @return bytes as a hex string
	 */
	public static String encodeHex(byte[] bytes) {
		StringBuilder r = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			r.append(HEX_CODE[(b >> 4) & 0xF]);
			r.append(HEX_CODE[(b & 0xF)]);
		}
		return r.toString();
	}

	/**
	 * decode Hex
	 *
	 * @param hexStr Hex string
	 * @return decode hex to bytes
	 */
	public static byte[] decodeHex(final String hexStr) {
		return DatatypeConverter.parseHexBinary(hexStr);
	}

	/**
	 * 比较字符串，避免字符串因为过长，产生耗时
	 *
	 * @param a String
	 * @param b String
	 * @return 是否相同
	 */
	public static boolean slowEquals(@Nullable String a, @Nullable String b) {
		if (a == null || b == null) {
			return false;
		}
		return DigestUtil.slowEquals(a.getBytes(Charsets.UTF_8), b.getBytes(Charsets.UTF_8));
	}

	/**
	 * 比较 byte 数组，避免字符串因为过长，产生耗时
	 *
	 * @param a byte array
	 * @param b byte array
	 * @return 是否相同
	 */
	public static boolean slowEquals(@Nullable byte[] a, @Nullable byte[] b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * 自定义加密 先MD5再SHA1
	 *
	 * @param data 数据
	 * @return String
	 */
	public static String encrypt(String data) {
		return sha1Hex(md5Hex(data));
	}

}
