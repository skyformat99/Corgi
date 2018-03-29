package com.ibeiliao.deployment.admin.service.project;

import com.ibeiliao.deployment.admin.vo.project.ProjectModule;

import java.util.List;
import java.util.Map;

/**
 * 详情 :  项目模块的服务
 *
 * @author liangguanglong
 */
public interface ProjectModuleService {

    /**
     * 保存或者更新项目模块
     * @param accountId     管理员ID
     * @param projectModule 模块信息，包括服务器组、服务器的信息
     */
    void saveProjectModule(long accountId, ProjectModule projectModule);

    /**
     * 根据项目id 查找模块
     *
     * @param projectId
     * @return
     */
    List<ProjectModule> getByProjectId(int projectId);

    /**
     * 根据moduleid 查询
     *
     * @param moduleId
     * @return
     */
    ProjectModule getByModuleId(int moduleId);

    /**
     *
     */
    void deleteByModuleId(int moduleId);

    /**
     * resin 配置是否已经生成过
     * @param moduleId
     * @return
     */
    boolean isResinConfCreated(int moduleId);

    /**
     * 设置 resin 配置已经生成过
     * @param moduleId
     */
    void setResinConfCreated(int moduleId);

    /**
     * 检测模块的resin接口是否被占用
     * @param projectModule
     * @return
     */
    Map<String, String> checkResinPortOccupy(ProjectModule projectModule);

    /**
     * 根据账号id查找所有模块信息
     * @param accountId 账号id
     * @return
     */
    List<ProjectModule> getProjectModuleByAccountId(long accountId);

    /**
     * 根据项目id 查找模块
     *  注意：返回的模块信息，只有id 和 模块名
     * @param projectIds
     * @return
     */
    List<ProjectModule> getSimpleInfoByProjectIds(List<Integer> projectIds);
}
