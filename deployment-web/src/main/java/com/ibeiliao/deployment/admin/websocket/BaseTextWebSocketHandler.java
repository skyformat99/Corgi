package com.ibeiliao.deployment.admin.websocket;

import com.alibaba.fastjson.JSONObject;
import com.ibeiliao.deployment.admin.websocket.context.LogSessionHolder;
import com.ibeiliao.deployment.admin.websocket.context.UserIdentitySessionHolder;
import com.ibeiliao.deployment.admin.websocket.processor.MessageProcessor;
import com.ibeiliao.deployment.admin.websocket.processor.ProcessorFactory;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketResponseMessage;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

/**
 * 功能: websocket 消息处理
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/3
 */
@Component
public class BaseTextWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseTextWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("afterConnectionEstablished ...");
        // 要求客户端发送先关的cookie信息
        WebSocketResponseMessage cookieReq = new WebSocketResponseMessage(ApiCode.SUCCESS, "请发送相关的用户新信息", WebSocketRequestType.USER_IDENTITY);
        webSocketSession.sendMessage(new TextMessage(JSONObject.toJSONString(cookieReq)));

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        TextMessage message = (TextMessage) webSocketMessage;
//        logger.info("获取到客户端请求的信息 | message:{}", message.getPayload());

        String content = message.getPayload();
        if (StringUtils.isEmpty(content)) {
            logger.error("消息内容为 empty");
        }
        else {
            try {
                WebSocketRequestMessage request = JSONObject.parseObject(content, WebSocketRequestMessage.class);
                if (request == null) {
                    logger.error("消息内容格式错误: " + content);
                } else {
                    WebSocketRequestType requestType = WebSocketRequestType.from(request.getType());
                    if (requestType == null) {
                        logger.error("消息类型不存在: " + request.getType() + ", content: " + content);
                    } else {
                        MessageProcessor messageProcessor = ProcessorFactory.create(requestType);
                        messageProcessor.process(webSocketSession, content);
                    }
                }
            } catch (Exception e) {
                logger.error("消息处理出错: " + content, e);
            }
        }

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error("服务器异常, message: {}", throwable.getMessage(), throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("afterConnectionClosed ...");
        LogSessionHolder.getInstance().remove(webSocketSession);
        UserIdentitySessionHolder.getInstance().remove(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    protected TextMessage constructMessage(int code, String message) {
        return new TextMessage(
                JSONObject.toJSONString(new WebSocketResponseMessage(code, message))
        );
    }


}
