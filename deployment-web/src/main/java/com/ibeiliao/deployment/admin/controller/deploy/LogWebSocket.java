package com.ibeiliao.deployment.admin.controller.deploy;

//import com.alibaba.fastjson.JSONObject;
//import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestMessage;
//import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
//import com.ibeiliao.deployment.admin.websocket.request.WebSocketResponseMessage;
//import com.ibeiliao.deployment.base.ApiCode;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//
///**
// * 功能：实现日志的 websocket 功能
// * 详细：
// *
// * @author linyi, 2017/2/8.
// */
////@ServerEndpoint(value = "/admin/websocket/log")
//public class LogWebSocket {
//
//    private static final Logger logger = LoggerFactory.getLogger(LogWebSocket.class);
//
//    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//    private static volatile int onlineCount = 0;
//
//    /**
//     * 连接建立成功调用的方法
//     *
//     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
//     */
//    @OnOpen
//    public void onOpen(Session session, EndpointConfig config) {
//        addOnlineCount();
//        logger.info("有新连接加入，当前连接数为 " + getOnlineCount());
//        WebSocketResponseMessage response = new WebSocketResponseMessage(ApiCode.SUCCESS, "请发送相关的用户新信息", WebSocketRequestType.USER_IDENTITY);
//        sendMessage(session, JSONObject.toJSONString(response));
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        subOnlineCount();           //在线数减1
//        logger.info("有一连接关闭，当前连接数为 " + getOnlineCount() + ", closeCode: " + closeReason.getCloseCode());
//    }
//
//    /**
//     * 收到客户端消息后调用的方法
//     *
//     * @param message 客户端发送过来的消息
//     * @param session 可选的参数
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        logger.info("来自客户端的消息:" + message);
//        // request json 格式判断
//        WebSocketRequestMessage requestMessage = JSONObject.parseObject(message, WebSocketRequestMessage.class);
//        if (requestMessage == null) {
//            logger.error("请求内容格式错误: " + message);
//        }
//        else {
//            // 请求类型判断
//            WebSocketRequestType requestType = WebSocketRequestType.from(requestMessage.getType());
//            if (requestType == null) {
//                logger.error("请求类型不存在: " + requestMessage.getType());
//            }
//            else {
//                handle(session, requestMessage, requestType);
//            }
//        }
//    }
//
//    /**
//     * 发生错误时调用
//     *
//     * @param session
//     * @param error
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        logger.info("发生错误", error);
//    }
//
//    /**
//     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
//     *
//     * @param message
//     * @throws IOException
//     */
//    public void sendMessage(Session session, String message) {
//        try {
//            session.getBasicRemote().sendText(message);
//        } catch (IOException e) {
//            logger.error("发送消息失败", e);
//        }
//        //this.session.getAsyncRemote().sendText(message);
//    }
//
//    public static synchronized int getOnlineCount() {
//        return LogWebSocket.onlineCount;
//    }
//
//    public static synchronized void addOnlineCount() {
//        LogWebSocket.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        LogWebSocket.onlineCount--;
//    }
//
//    private void handle(Session session, WebSocketRequestMessage requestMessage, WebSocketRequestType requestType) {
//        if (requestType == WebSocketRequestType.HEARTBEAT) {
//            logger.info("心跳包，跳过 ... session: " + session.getId());
//        }
//        else {
//
//        }
//    }
//}
