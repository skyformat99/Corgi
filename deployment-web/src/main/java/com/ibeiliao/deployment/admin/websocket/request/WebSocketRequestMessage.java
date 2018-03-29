package com.ibeiliao.deployment.admin.websocket.request;

import java.io.Serializable;

/**
 * 功能: websocket 请求
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/3
 */
public class WebSocketRequestMessage implements Serializable {

    /**
     * 请求/返回类型
     */
    private String type;



    public WebSocketRequestMessage(String type) {
        this.type = type;
    }

    public WebSocketRequestMessage() {

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
