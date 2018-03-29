package com.ibeiliao.deployment.common;

/**
 * 功能：公共变量
 * 详细：
 *
 * @author linyi, 2017/2/23.
 */
public interface Constants {

    /**
     * 系统ID
     */
    long SYSTEM_ACCOUNT_ID = -1L;

    /**
     * 发布任务超时的时间，单位：秒
     */
    int DEPLOY_TASK_TIMEOUT = 3600 * 16;

    /**
     * bool值
     */
    short TRUE = 1;

    /**
     * bool值
     */
    short FALSE = 0;

    /**
     * 发布次数大于等于这个数值的为质量差的模块
     */
    int LOW_QUALITY_DEPLOY_TIMES = 2;

    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * web 容器 resin
     */
    int CONF_TYPE_RESIN = 1;

    /**
     * web 容器 tomcat
     */
    int CONF_TYPE_TOMCAT = 2;

    /**
     * 标识 resin 配置是否生成过
     */
    int CONF_TYPE_RESIN_GEN_FLAG = 10;

    /**
     * 默认的 keepalive timeout, 15s
     */
    int DEFAULT_KEEPALIVE_TIMEOUT = 15;

    /**
     * 默认的 socket timeout, 30s
     */
    int DEFAULT_SOCKET_TIMEOUT = 30;

    /**
     * 域名的正则表达式
     */
    String DOMAIN_REGX = "[a-zA-Z0-9\u4e00-\u9fa5._-]+";
}
