package com.ibeiliao.deployment.common.util;

/**
 * 功能: redis 日志key
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/9
 */
public class RedisLogKey {

    /**
     * 获取 存储服务器发布日志记录的key
     * @return
     */
    public static String getServerDeploymentKey(){
        return "deployment:server_deploy_log";
    }

    /**
     *  每台服务器的启动过程的日志
     */
    public static String getDeployServerLogKey(int historyId) {
       return "deployment:history:" + historyId + ":server:log";
    }

    /**
     * 获取模块的编译日志key
     * @param historyId
     * @return
     */
    public static String getModuleCompileKey(int historyId){
        return "deployment:module_compile_log:" + historyId;
    }

}
