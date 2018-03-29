package com.ibeiliao.deployment.admin.vo.global;

import java.util.List;

/**
 * 详情 :
 *
 * @author liangguanglong
 */
public class SettingEnvironment {

    /**
     * 环境
     */
    private List<ProjectEnv> environments;
    /**
     * 全局基础配置
     */
    private GlobalSetting globalSetting;

    public GlobalSetting getGlobalSetting() {
        return globalSetting;
    }

    public void setGlobalSetting(GlobalSetting globalSetting) {
        this.globalSetting = globalSetting;
    }

    public List<ProjectEnv> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<ProjectEnv> environments) {
        this.environments = environments;
    }

}
