package com.ibeiliao.deployment.cfg;


import com.ibeiliao.deployment.common.util.AESUtil;

/**
 * AES实现加解密
 * @author linyi
 *
 */
public class AesPropertiesEncoder implements PropertiesEncoder {
     /**
     * the key
     */
    private static final String KEY = "z$t05ch*pUe%";

    @Override
    public String encode(String str) {
        String s = "";
		try {
			s = AESUtil.encrypt(KEY, str);
		} catch (Exception e) {
			throw new RuntimeException("加密错误", e);
		}
        return s;
    }

    @Override
    public String decode(String str) {
        String s = "";
		try {
			s = AESUtil.decrypt(KEY, str);
		} catch (Exception e) {
			throw new RuntimeException("解密错误: " + str, e);
		}
        return s;
    }

    public static void main(String[] args) {
        AesPropertiesEncoder encoder = new AesPropertiesEncoder();
        System.out.println(encoder.encode(""));
        System.out.println(encoder.encode("root"));
    }

}
