package com.ibeiliao.deployment.admin.websocket.processor;

import org.springframework.web.socket.WebSocketSession;

/**
 * 功能：处理客户端发来的心跳包
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public class HeartbeatMessageProcessor extends MessageProcessor {

    @Override
    public void process(WebSocketSession webSocketSession, String message) {
//        logger.info("心跳包 ... sessionId: " + webSocketSession.getId());
    }
}
