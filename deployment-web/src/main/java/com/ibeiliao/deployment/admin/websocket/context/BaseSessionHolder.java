package com.ibeiliao.deployment.admin.websocket.context;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
public abstract class BaseSessionHolder<T> {

    /**
     * 保存当前链接的session
     */
    private Map<WebSocketSession, T> sessions = new ConcurrentHashMap<>();


    /**
     * 是否存在
     *
     * @param session
     * @return
     */
    public boolean exists(WebSocketSession session) {
        return sessions.containsKey(session);
    }

    /**
     * 保存session
     *
     * @param session
     */
    public void save(WebSocketSession session, T object) {
        sessions.put(session, object);
    }

    /**
     * 获取session的值
     * @param session
     * @param <T>
     * @return
     */
    public <T> T getSessionValue(WebSocketSession session) {
        Object t =  sessions.get(session);
        return (T) t;
    }


    /**
     * 移除session
     *
     * @param session
     */
    public void remove(WebSocketSession session) {
        sessions.remove(session);
    }

    public List<WebSocketSession> getAllSessionKeys() {
        return new ArrayList<>(sessions.keySet());
    }


    public Map<WebSocketSession, T> getSessions(){
        return sessions;
    }


}
