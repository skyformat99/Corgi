package com.ibeiliao.deployment.common.vo;

import java.io.Serializable;

/**
 * 功能: 服务器执行脚本的log
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/24
 */
public class ServerCollectLog implements Serializable {

    /**
     * 服务器发布id / 项目模块ID
     */
    private int id;

    /**
     * 日志类型
     */
    private short logType;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 服务器ip
     */
    private String serverIp;

    public ServerCollectLog() {

    }

    public ServerCollectLog(int id, short logType, String content) {
        this.id = id;
        this.content = content;
        this.logType = logType;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public short getLogType() {
        return logType;
    }

    public void setLogType(short logType) {
        this.logType = logType;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

}
