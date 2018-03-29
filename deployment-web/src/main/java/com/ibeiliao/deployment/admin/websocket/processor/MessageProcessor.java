package com.ibeiliao.deployment.admin.websocket.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

/**
 * 功能：消息处理
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public abstract class MessageProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

    /**
     * 处理消息
     * @param webSocketSession
     * @param message
     */
    public abstract void process(WebSocketSession webSocketSession, String message);
}
