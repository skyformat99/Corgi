package com.ibeiliao.deployment.admin.websocket.request;

/**
 * 功能: 发送cookie的请求
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/4
 */
public class CookieRequestMessage extends WebSocketRequestMessage{

    /**
     * cookie
     */
    private String cookies;


    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }
}
