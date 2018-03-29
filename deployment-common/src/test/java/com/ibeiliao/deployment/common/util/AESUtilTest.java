package com.ibeiliao.deployment.common.util;

import com.ibeiliao.deployment.cfg.AesPropertiesEncoder;
import org.junit.Test;

import java.util.Objects;

/**
 * 详情 : aes 加密解密工具测试
 * <p>
 * 详细 :
 *
 * @author liangguanglong 2018/2/6
 */
public class AESUtilTest {

    /**
     * 测试对称加密解密
     */
    @Test
    public void test() {
        AesPropertiesEncoder aes = new AesPropertiesEncoder();
        String password = "123456";
        String encode = aes.encode(password);

        String decode = aes.decode(encode);
        assert Objects.equals(password, decode);
    }
}
