package com.ibeiliao.deployment.admin.websocket.request;

import java.io.Serializable;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/3
 */
public class WebSocketResponseMessage implements Serializable{


    /**
     * 返回代码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 请求/返回类型
     */
    private String type;

    public WebSocketResponseMessage(){
    }

    public WebSocketResponseMessage(int code, String message, WebSocketRequestType requestType) {
        this.code = code;
        this.message = message;
        this.type = requestType.getName();
    }



    public WebSocketResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
