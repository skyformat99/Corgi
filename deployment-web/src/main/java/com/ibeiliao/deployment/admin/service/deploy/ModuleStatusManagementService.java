package com.ibeiliao.deployment.admin.service.deploy;

import com.ibeiliao.deployment.common.util.redis.Redis;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 功能：管理模块发布的状态，避免同一个模块同时被多个人发布
 * 详细：
 *
 * @author linyi, 2017/2/8.
 */
@Service
public class ModuleStatusManagementService {

    @Autowired
    private Redis redis;

    /**
     * 模块最大的部署时间，设置为 5 分钟
     */
    private static final int MAX_MODULE_DEPLOY_TIME  = 300;

    private static final String FIELD_SEPARATOR = "_";

    /**
     * 判断模块是否在部署中
     * @param moduleId
     * @param envId 环境ID
     * @return
     */
    public boolean isModuleDeploying(int moduleId, int envId) {
        String key = getRedisKey(moduleId, envId);
        String value = redis.get(key);
        return StringUtils.isNotEmpty(value);
    }

    /**
     * 开始模块部署，成功返回 true.
     * 只有并发操作的时候才可能会返回false.
     * @param moduleId   模块ID
     * @param envId      环境ID
     * @param historyId  发布记录ID
     */
    public boolean startModuleDeploy(int moduleId, int envId, int historyId) {
        String key = getRedisKey(moduleId, envId);
        Long n = redis.setnx(key, "1");
        if (n == null || n == 0L) {
            return false;
        }
        redis.expire(key, MAX_MODULE_DEPLOY_TIME);
        redis.incr(getCounterKey());
        redis.hset(getModuleMapKey(), getFieldName(moduleId, envId), "" + historyId);
        return true;
    }

    /**
     * 结束模块的部署
     * @param moduleId  模块ID
     * @param envId     环境ID
     */
    public void endModuleDeploy(int moduleId, int envId) {
        String key = getRedisKey(moduleId, envId);
        redis.del(key);
        redis.decr(getCounterKey());
    }

    /**
     * 返回正在发布的模块的数量
     * @return
     */
    public int getDeployingNum() {
        String value = redis.get(getCounterKey());
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        return NumberUtils.toInt(value, 0);
    }

    /**
     * 清除模块发布日志的标记，由日志推送线程负责调用
     * @param moduleId  模块ID
     * @param envId     环境ID
     */
    public void clearModuleDeployLog(int moduleId, int envId) {
        redis.hdel(getModuleMapKey(), getFieldName(moduleId, envId));
    }

    /**
     * 返回所有正在部署的 module
     * @return key=field, value=historyId
     */
    public Map<String, String> getAllDeployingModules() {
        return redis.hgetAll(getModuleMapKey());
    }

    public int getHistoryIdFromFieldValue(String value) {
        return NumberUtils.toInt(value, 0);
    }

    public int getModuleIdFromField(String field) {
        String[] ids = field.split(FIELD_SEPARATOR);
        if (ids.length == 2) {
            return NumberUtils.toInt(ids[0], 0);
        }
        return 0;
    }

    private String getFieldName(int moduleId, int envId) {
        return moduleId + FIELD_SEPARATOR + envId;
    }

    private String getRedisKey(int moduleId, int envId) {
        return "deployment_module_status_" + moduleId + "_" + envId;
    }

    private String getCounterKey() {
        return "deployment_deploying_counter";
    }

    private String getModuleMapKey() {
        return "deployment_deploying_map";
    }
}
