package com.ibeiliao.deployment.admin.websocket.context;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

/**
 * 功能: 获取日志的链接session
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
public class LogSessionHolder extends BaseSessionHolder<Set<Integer>> {

    private static LogSessionHolder holder = new LogSessionHolder();

    private LogSessionHolder() {

    }

    /**
     * 获取单实例
     *
     * @return
     */
    public static LogSessionHolder getInstance() {
        return holder;
    }

    @Override
    public void save(WebSocketSession session, Set<Integer> object) {
        super.save(session, object);
        for (Integer id : object) {
            LogIdToSessionHolder.getInstance().add(id, session);
        }
    }

    @Override
    public void remove(WebSocketSession session) {
        if (super.exists(session)) {
            Set<Integer> idSet = super.getSessionValue(session);
            super.remove(session);
            for (Integer id : idSet) {
                LogIdToSessionHolder.getInstance().remove(id);
            }
        }

    }
}
