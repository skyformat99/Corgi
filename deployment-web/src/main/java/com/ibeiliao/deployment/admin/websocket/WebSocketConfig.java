package com.ibeiliao.deployment.admin.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 功能: websocket 注册配置器
 * <p>
 * （1）客户端在 onopen() 处理连接成功事件
 * （2）连接成功后，客户端要发送登录信息给服务端，type=USER_IDENTITY, cookies=document.cookie
 * （3）连接成功后，客户端要发 type=HEARTBEAT 保持连接
 * （4）登录成功后，服务端返回消息 type=USER_IDENTITY_SUCCESS 给客户端，
 *     客户端要发一次 type=DEPLOY_SHELL_LOG 抓取一次日志，
 *     后面是服务端主动推送消息给客户端。
 *
 * </p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private BaseTextWebSocketHandler baseTextWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        logger.info("注册registerWebSocketHandlers ...");

        webSocketHandlerRegistry.addHandler(baseTextWebSocketHandler, "/admin/websocket/log").setAllowedOrigins("*");

    }
}
