package com.ibeiliao.deployment.admin.websocket.request;

import java.io.Serializable;
import java.util.List;

/**
 * 功能: 部署系统的shell log 日志返回
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/4
 */
public class ShellLogResponseMessage extends WebSocketResponseMessage {

    /**
     * 步骤日志
     */
    private List<String> stepLogs;

    /**
     * 服务器启动步骤日志
     */
    private List<String> serverDeployLogs;

    /**
     * 服务器日志
     */
    private List<ServerShellLog> serverLogs;


    /**
     * 服务器日志
     */
    public static class ServerShellLog implements Serializable{

        /**
         * 服务器发布id
         */
        private int serverDeployId;

        /**
         * 日志
         */
        private String log;

        public int getServerDeployId() {
            return serverDeployId;
        }

        public void setServerDeployId(int serverDeployId) {
            this.serverDeployId = serverDeployId;
        }

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }
    }

    public List<String> getStepLogs() {
        return stepLogs;
    }

    public void setStepLogs(List<String> stepLogs) {
        this.stepLogs = stepLogs;
    }

    public List<ServerShellLog> getServerLogs() {
        return serverLogs;
    }

    public void setServerLogs(List<ServerShellLog> serverLogs) {
        this.serverLogs = serverLogs;
    }
    public List<String> getServerDeployLogs() {
        return serverDeployLogs;
    }

    public void setServerDeployLogs(List<String> serverDeployLogs) {
        this.serverDeployLogs = serverDeployLogs;
    }
}
