package com.ibeiliao.deployment.admin.websocket.request;

/**
 * 功能:  websocket 请求类型
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/3
 */
public enum WebSocketRequestType {

    USER_IDENTITY("USER_IDENTITY", "用户登录信息"),
    /**
     * 登录成功通知，发给客户端
     */
    USER_IDENTITY_SUCCESS("USER_IDENTITY_SUCCESS", "用户登录信息成功"),
    DEPLOY_SHELL_LOG("DEPLOY_SHELL_LOG", "部署shell日志"),
    DEPLOY_STEP_LOG("DEPLOY_STEP_LOG", "部署步骤日志"),
    DEPLOY_SERVER_LOG("DEPLOY_SERVER_LOG", "每台服务器部署步骤日志"),
    HEARTBEAT("HEARTBEAT", "心跳包"),
    CLOSE_CONNECTION("CLOSE_CONNECTION", "关闭连接");


    /**
     * 名称
     */
    private String name;

    /**
     * 信息备注
     */
    private String msg;

    private WebSocketRequestType(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static WebSocketRequestType from(String name){
        for(WebSocketRequestType type : WebSocketRequestType.values()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }


}
