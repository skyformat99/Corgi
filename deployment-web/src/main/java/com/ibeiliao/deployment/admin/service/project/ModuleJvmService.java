package com.ibeiliao.deployment.admin.service.project;

import com.ibeiliao.deployment.admin.vo.project.ModuleJvm;

import java.util.List;

/**
 * 详情 : 模块的jvm参数
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/24
 */
public interface ModuleJvmService {

    /**
     * 批量添加模块jvm参数
     *
     * @param moduleJvmList
     */
    void saveModuleJvm(List<ModuleJvm> moduleJvmList);

    /**
     * 更新jvm参数
     * @param moduleJvmList
     */
    void  updateModuleJvm(List<ModuleJvm> moduleJvmList);

    /**
     * 根据moduleId 查询
     * @param moduleId
     */
    List<ModuleJvm> queryByModuleId(int moduleId);

    /**
     * 根据moduleId 做删除
     * @param moduleId
     */
    void deleteByModuleId(int moduleId);

    List<ModuleJvm> queryByModuleIds(List<Integer> moduleIds);
}
