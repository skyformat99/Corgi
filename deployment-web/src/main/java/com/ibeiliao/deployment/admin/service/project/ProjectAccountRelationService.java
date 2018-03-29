package com.ibeiliao.deployment.admin.service.project;

import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;

import java.util.List;

/**
 * 详情 :  账户与项目的关系 service
 *
 * @author liangguanglong
 */
public interface ProjectAccountRelationService {

    /**
     * 保存关系
     * @param relations
     */
    void batchAddRelations(List<ProjectAccountRelation> relations);

    /**
     * 根据projectId删除关系
     * @param projectId
     * @return
     */
    void deleteRelationByProjectId(int projectId);

    /**
     * 根据projectid查询 关系
     * @param projectId
     * @return
     */
    List<ProjectAccountRelation> getByProjectId(int projectId);

    /**
     * 根据projectid查询 关系
     * @param projectIds
     * @return
     */
    List<ProjectAccountRelation> getByProjectIds(List<Integer> projectIds);

    /**
     * 判断帐号是否有 project 的发布权限
     * @param accountId 账户id
     * @param projectId 项目id
     * @return
     */
    boolean hasRelation(long accountId, int projectId);

    /**
     * 判断帐号是否不是项目的管理员
     * @param accountId 帐号ID
     * @param projectId 项目id
     * @return
     */
    boolean isAdmin(long accountId, int projectId);

    /**
     * 判断帐号是否有项目的修改、审核权限
     * @param accountId  帐号ID
     * @param projectId  项目ID
     * @return
     */
    boolean canModify(long accountId, int projectId);

    /**
     * 获取所有的负责人id
     * @return
     */
    List<Long> getAllManagerId();
}
