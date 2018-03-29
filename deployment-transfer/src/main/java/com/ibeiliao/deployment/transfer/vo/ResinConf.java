package com.ibeiliao.deployment.transfer.vo;

import com.ibeiliao.deployment.common.Constants;

import java.io.Serializable;

/**
 * 功能：resin 配置
 * 详细：
 *
 * @author linyi, 2017/3/14.
 */
public class ResinConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
    private int moduleId;

    /**
     * 配置类型：resin
     */
    private int confType = Constants.CONF_TYPE_RESIN;

    /**
     * 域名，不包含 http/https
     */
    private String domain;

    /**
     * 域名，不包含 http/https
     */
    private String aliasDomain;

    /**
     * http 端口
     */
    private int httpPort;

    /**
     * 服务器端口
     */
    private int serverPort;

    /**
     * watchdog 端口
     */
    private int watchdogPort;

    /**
     * 最大线程数，0 为不配置
     */
    private int threadMax;

    /**
     * keepalive 最大数量，0 为不配置
     */
    private int keepaliveMax;

    /**
     * keepaliveTimeout，0 为不配置
     */
    private int keepaliveTimeout;

    /**
     * socketTimeout，0 为不配置
     */
    private int socketTimeout;

    /**
     * 是否每次发布都重新生成 resin.xml
     */
    private boolean createEveryTime;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getWatchdogPort() {
        return watchdogPort;
    }

    public void setWatchdogPort(int watchdogPort) {
        this.watchdogPort = watchdogPort;
    }

    public int getThreadMax() {
        return threadMax;
    }

    public void setThreadMax(int threadMax) {
        this.threadMax = threadMax;
    }

    public int getKeepaliveMax() {
        return keepaliveMax;
    }

    public void setKeepaliveMax(int keepaliveMax) {
        this.keepaliveMax = keepaliveMax;
    }

    public int getKeepaliveTimeout() {
        return keepaliveTimeout;
    }

    public void setKeepaliveTimeout(int keepaliveTimeout) {
        this.keepaliveTimeout = keepaliveTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getConfType() {
        return confType;
    }

    public void setConfType(int confType) {
        this.confType = confType;
    }

    public boolean isCreateEveryTime() {
        return createEveryTime;
    }

    public String getAliasDomain() {
        return aliasDomain;
    }

    public void setAliasDomain(String aliasDomain) {
        this.aliasDomain = aliasDomain;
    }

    public void setCreateEveryTime(boolean createEveryTime) {
        this.createEveryTime = createEveryTime;
    }

}
