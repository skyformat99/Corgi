package com.ibeiliao.deployment.admin.websocket.request;

import java.util.List;

/**
 * 功能: 获取部署时 shell log 日志的请求
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/4
 */
public class ShellLogRequestMessage extends WebSocketRequestMessage {



    /**
     * 发布的服务器id
     */
    private List<Integer> serverDeployIdList;

    public List<Integer> getServerDeployIdList() {
        return serverDeployIdList;
    }

    public void setServerDeployIdList(List<Integer> serverDeployIdList) {
        this.serverDeployIdList = serverDeployIdList;
    }
}
