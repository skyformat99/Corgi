package com.ibeiliao.deployment.admin.websocket.processor;

import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;

/**
 * 功能：工厂类
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public class ProcessorFactory {

    public static MessageProcessor create(WebSocketRequestType type) {
        switch (type) {
            case HEARTBEAT:
                return new HeartbeatMessageProcessor();
            case USER_IDENTITY:
                return new LoginMessageProcessor();
            case DEPLOY_SHELL_LOG:
                return new LogMessageProcessor();
            default:
                break;
        }
        return new DefaultMessageProcessor();
    }
}
