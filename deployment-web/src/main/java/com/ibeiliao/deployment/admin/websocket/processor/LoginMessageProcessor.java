package com.ibeiliao.deployment.admin.websocket.processor;

import com.alibaba.fastjson.JSONObject;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.context.AdminLoginUser;
import com.ibeiliao.deployment.admin.websocket.context.UserIdentitySessionHolder;
import com.ibeiliao.deployment.admin.websocket.request.CookieRequestMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：客户端请求登录
 * 详细：处理客户端发送的 document.cookie，实现 websocket 的登录
 *
 * @author linyi, 2017/2/9.
 */
public class LoginMessageProcessor extends MessageProcessor {

    @Override
    public void process(WebSocketSession webSocketSession, String message) {
        if (!UserIdentitySessionHolder.getInstance().exists(webSocketSession)) {

            logger.info("用户未登录");
            CookieRequestMessage cookieMessage = JSONObject.parseObject(message, CookieRequestMessage.class);

            if (cookieMessage != null) {

                logger.info("cookie: " + cookieMessage.getCookies());
                if (StringUtils.isNotEmpty(cookieMessage.getCookies())) {
                    Map<String, String> cookieMap = new HashMap<>();

                    for (String str : StringUtils.split(cookieMessage.getCookies(), " ")) {
                        if (StringUtils.isNotEmpty(str)) {
                            String s = str.contains(";") ? str.substring(0, str.lastIndexOf(";")) : str.trim();
                            int pos = s.indexOf("=");  // 不可用s.split(), 因为可能有多个等号
                            if (pos > 0) {
                                String name = s.substring(0, pos);
                                String value = s.substring(pos + 1);
                                cookieMap.put(name.trim(), value.trim());
                            }
                        }
                    }
                    AdminLoginUser loginUser = AdminContext.loadFromCookieMap(cookieMap);
                    if (loginUser == null) {
                        logger.error("用户不存在 ... " + cookieMessage.getCookies());
                    } else {
                        logger.info("用户登录成功 | accountId: " + loginUser.getAccountId());
                        UserIdentitySessionHolder.getInstance().save(webSocketSession, loginUser);

                        sendAckMessage(webSocketSession);
                    }
                } else {
                    logger.error("cookie 错误，关闭客户端 ... " + webSocketSession.getId());
                    IOUtils.closeQuietly(webSocketSession);
                }

            }
        }
    }

    private void sendAckMessage(WebSocketSession webSocketSession) {
        WebSocketRequestMessage ackMessage = new WebSocketRequestMessage(WebSocketRequestType.USER_IDENTITY_SUCCESS.getName());
        TextMessage textMessage = new TextMessage(JSONObject.toJSONString(ackMessage));
        // 发送ack消息，最多重试 3 次
        for (int i=0; i < 3; i++) {
            try {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(textMessage);
                } else {
                    logger.warn("websocket已经关闭");
                }
                break;
            } catch (IOException e) {
                logger.error("发送登录成功ack消息出错", e);
            }

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
