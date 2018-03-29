package com.ibeiliao.deployment.admin.test.utils.table;

import com.ibeiliao.deployment.admin.utils.MD5Util;
import org.junit.Test;

import java.util.Date;

/**
 * 详情 :
 * <p>
 * 详细 :
 *
 * @author liangguanglong 2018/3/26
 */
public class MD5Test {

    @Test
    public void test() {
        //e10adc3949ba59abbe56e057f20f883e
        System.out.println(MD5Util.md5("123456"));

        Date date = new Date();
        System.out.println(date.getTime() + " 1522053294000");
        System.out.println(date.getTime() > 1522053294000L);
    }
}
