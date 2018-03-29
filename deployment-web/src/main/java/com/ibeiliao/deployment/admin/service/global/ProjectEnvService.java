package com.ibeiliao.deployment.admin.service.global;

import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;

import java.util.List;

/**
 * 详情 :  环境的服务
 *
 * @author liangguanglong
 */
public interface ProjectEnvService {

    /**
     * 分页查询
     * @param envName 环境名称(非必填)
     * @param page
     * @param pageSize
     * @return
     */
    List<ProjectEnv> findEnvList(String envName, int page, int pageSize);

    /**
     * 根据查询条件查询环境的数量
     * @param envName 环境名称
     * @return
     */
    int findEnvTotalCount(String envName);

    /**
     * 新增
     */
    void saveEnv(ProjectEnv projectEnv);

    /**
     * 删除环境
     * @param projectEnvId
     */
    int deleteEnv(int projectEnvId);

    /**
     * 获取所有的环境
     * @return
     */
    List<ProjectEnv> findAllEnv();

    /**
     * 根据id查找
     * @param envId
     * @return
     */
    ProjectEnv getById(int envId);

    /**
     * 判断环境是否为生产环境
     * @param envId
     * @return
     */
    boolean isOnline(int envId);
}
