package com.ibeiliao.deployment.admin.vo.project;

import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;

import java.util.List;

/**
 * 详情 : 模块详情
 *
 * @author liangguanglong
 */
public class ModuleDetailInfo {

    /**
     * 模块内容
     */
    private ProjectModule projectModule;

    /**
     * 模块对应的项目
     */
    private Project project;

    /**
     * 服务器组
     */
    private List<ServerGroup> serverGroups;

    /**
     * 所有的环境 (仅作为页面初始化渲染 环境所用)
     */
    private List<ProjectEnv> envs;

    /**
     * 模块的jvm参数 (仅用于 添加的时候初始化各个环境对应的jvm参数)
     */
    private List<ModuleJvm> moduleJvms;

    public List<ModuleJvm> getModuleJvms() {
        return moduleJvms;
    }

    public void setModuleJvms(List<ModuleJvm> moduleJvms) {
        this.moduleJvms = moduleJvms;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectModule getProjectModule() {
        return projectModule;
    }

    public void setProjectModule(ProjectModule projectModule) {
        this.projectModule = projectModule;
    }

    public List<ServerGroup> getServerGroups() {
        return serverGroups;
    }

    public void setServerGroups(List<ServerGroup> serverGroups) {
        this.serverGroups = serverGroups;
    }

     public List<ProjectEnv> getEnvs() {
        return envs;
    }

    public void setEnvs(List<ProjectEnv> envs) {
        this.envs = envs;
    }

}
