package com.ibeiliao.deployment.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 功能：jvm 参数增强
 * 详细：
 *
 * @author linyi, 2017/3/16.
 */
public class JvmArgUtil {

    public static final String GC_LOG = "-Xloggc:";

    public static final String PROFILE_FLAG = "-Dprofile=";

    private static final String[] ENHANCE_ARGS_NAME = {
            "-XX:-OmitStackTraceInFastThrow",
            "-Dnetworkaddress.cache.ttl",
            "-server",
            "-d64",
            "-verbose:gc",
            "-XX:+PrintGCDetails",
            "-XX:+PrintGCDateStamps",
            GC_LOG
    };

    private static final String[] ENHANCE_ARGS_VALUE = {
            "-XX:-OmitStackTraceInFastThrow",
            "-Dnetworkaddress.cache.ttl=600",
            "-server",
            "-d64",
            "-verbose:gc",
            "-XX:+PrintGCDetails",
            "-XX:+PrintGCDateStamps",
            GC_LOG
    };

    /**
     * 加强 JVM 参数
     * @param jvmArgs 原始的 JVM 参数
     * @param params  JVM参数的变量，比如 GCLOG 的目录，需要传入，完整的参数值，比如：-Xloggc:/data/logs/x.log
     * @return 返回加强后的JVM参数
     */
    public static String enhanceArg(String jvmArgs, Map<String, String> params) {
        StringBuilder buf = new StringBuilder(jvmArgs);
        for (int i=0; i < ENHANCE_ARGS_NAME.length; i++) {
            if (buf.indexOf(ENHANCE_ARGS_NAME[i]) < 0) {
                if (params.containsKey(ENHANCE_ARGS_NAME[i])) {
                    buf.append(" ").append(params.get(ENHANCE_ARGS_NAME[i]));
                } else {
                    buf.append(" ").append(ENHANCE_ARGS_VALUE[i]);
                }
            }
        }
        return buf.toString().trim();
    }

    /**
     * 添加 -Dprofile 环境参数，用于标识发布环境
     * @param jvmArgs jvm参数
     * @param envName 环境名称
     * @return 添加了环境参数的jvm 参数
     */
    public static String addProfileArgs(String jvmArgs, String envName) {
        String profileArg = PROFILE_FLAG + envName;
        return jvmArgs + " " + profileArg;
    }

    public static String getDefaultArgs() {
        return StringUtils.join(ENHANCE_ARGS_VALUE, " ");
    }
}
