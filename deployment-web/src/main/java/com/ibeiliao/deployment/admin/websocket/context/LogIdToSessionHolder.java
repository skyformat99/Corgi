package com.ibeiliao.deployment.admin.websocket.context;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 功能：log id 对应的 session
 * 详细：
 *
 * @author linyi, 2017/2/17.
 */
public class LogIdToSessionHolder {

    /**
     * 保存 serverDeployId -> session 的关系
     */
    private Map<Integer, Set<WebSocketSession>> idToSessionMap = new ConcurrentHashMap<>();

    private static LogIdToSessionHolder logIdToSessionHolder = new LogIdToSessionHolder();

    private LogIdToSessionHolder() {}

    public static LogIdToSessionHolder getInstance() {
        return logIdToSessionHolder;
    }

    public Set<WebSocketSession> get(int serverDeployId) {
        return idToSessionMap.get(serverDeployId);
    }

    public void add(int serverDeployId, WebSocketSession session) {
        Set<WebSocketSession> set = idToSessionMap.get(serverDeployId);
        if (set == null) {
            set = new HashSet<>();
            idToSessionMap.put(serverDeployId, set);
        }
        if (!set.contains(session)) {
            set.add(session);
        }
    }

    public void remove(int serverDeployId) {
        idToSessionMap.remove(serverDeployId);
    }
}
