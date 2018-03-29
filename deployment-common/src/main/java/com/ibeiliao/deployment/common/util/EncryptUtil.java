package com.ibeiliao.deployment.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密类
 * 
 */
public class EncryptUtil {
	public static String getMD5(String str) {
		return encode(str, "MD5");
	}

	private static String encode(String str, String type) {
		try {
			return encode(str.getBytes("UTF-8"), type);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Not supported encoding: UTF-8", e);
		}
	}

	private static String encode(byte[] bytes, String type) {
		try {
			MessageDigest alga = MessageDigest.getInstance(type);
			alga.update(bytes);
			byte[] digest = alga.digest();
			return byte2hex(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Not supported: " + type, e);
		}
	}

	public static String byte2hex(byte[] b) {
        StringBuilder buf = new StringBuilder(32);
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				buf.append("0").append(stmp);
			else
				buf.append(stmp);
		}
		return buf.toString().toLowerCase();
	}

}
