package com.ibeiliao.deployment.common.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 功能：测试 JvmArgUtil
 * 详细：
 *
 * @author linyi, 2017/3/17.
 */
public class JvmArgUtilTest {

    @Test
    public void testEnhanceArg() {
        Map<String, String> params = new HashMap<>();
        String gcLog = JvmArgUtil.GC_LOG + "/data/logs/a.log";
        params.put(JvmArgUtil.GC_LOG, gcLog);
        String arg = JvmArgUtil.enhanceArg("", params);
        System.out.println(arg);
        assertTrue(arg.contains(gcLog));
    }
}