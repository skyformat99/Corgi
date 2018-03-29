package com.ibeiliao.deployment.common.util;


/**
 * 对系统所有涉及密码的地方加密。
 * 用固定的key加密
 * @author tendy
 * 2014/2/17
 */
public class StaticKeyHelper {

    /**
     * the key
     */
    private static final String KEY = "lkDooG33820-cj_-=#328%f-++32-UW,NM32savcz3,@*($)";


    /**
     * 加密
     * @param key
     * @return
     */
	public static String encryptKey(String key) {
		String s = "";
		try {
			s = DES3.encrypt(key, KEY);
		} catch (Exception e) {
			throw new RuntimeException("加密错误", e);
		}
        return s;
	}

	/**
	 * 解密
	 * @param data
	 * @return
	 */
	public static String descryptKey(String data) {
		String s = "";
		try {
			s = DES3.decrypt(data, KEY);
		} catch (Exception e) {
			throw new RuntimeException("解密错误", e);
		}
        return s;
	}

	public static void main(String[] args) {
		System.out.println(encryptKey("18928810408"));
	}
}