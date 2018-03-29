package com.ibeiliao.deployment.admin.websocket.processor;

import org.springframework.web.socket.WebSocketSession;

/**
 * 功能：默认的消息处理
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public class DefaultMessageProcessor extends MessageProcessor {

    @Override
    public void process(WebSocketSession webSocketSession, String message) {
        logger.warn("默认消息处理，丢弃消息 ... message: " + message);
    }
}
