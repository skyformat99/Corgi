package com.ibeiliao.deployment.log.utils;

import com.ibeiliao.deployment.cfg.EncryptionPropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 功能: 日志同步配置
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/24
 */
@Component
public class LogSyncConfig {

    /**
     * 同步shell日志的url
     */
    private String syncShellLogUrl = EncryptionPropertyPlaceholderConfigurer.getConfig("deployment.save.shell.log.url");

    /**
     * 同步shell的后台发送线程数
     */
    private int syncShellLogThreadCount = 2;

    /**
     * 最大同步周期
     */
    private long maxSyncInteval = 2 * 1000L;


    private static LogSyncConfig instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static LogSyncConfig getInstance() {
        return instance;
    }


    public String getSyncShellLogUrl() {
        return syncShellLogUrl;
    }

    public void setSyncShellLogUrl(String syncShellLogUrl) {
        this.syncShellLogUrl = syncShellLogUrl;
    }

    public int getSyncShellLogThreadCount() {
        return syncShellLogThreadCount;
    }

    public void setSyncShellLogThreadCount(int syncShellLogThreadCount) {
        this.syncShellLogThreadCount = syncShellLogThreadCount;
    }

    public long getMaxSyncInteval() {
        return maxSyncInteval;
    }

    public void setMaxSyncInteval(long maxSyncInteval) {
        this.maxSyncInteval = maxSyncInteval;
    }
}
