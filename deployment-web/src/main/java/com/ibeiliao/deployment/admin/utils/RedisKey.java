package com.ibeiliao.deployment.admin.utils;

/**
 * 功能: redis key
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/7
 */
public class RedisKey {

    /**
     * 获取发布过程中发布记录已完成的发布步骤
     * @param historyId
     * @return
     */
    public static String getProjectHistoryLogKey(int historyId){
        return "deployment:history:" + historyId + ":step:log";
    }

    /**
     * 获取发布的推送channel
     * @return
     */
    public static String getDeploySubscribeChannelKey(){
        return "deployment:subscribe:channel";
    }


}
