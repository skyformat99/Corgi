package com.ibeiliao.deployment.admin.service.project.impl;

import com.ibeiliao.deployment.admin.dao.project.ProjectAccountRelationDao;
import com.ibeiliao.deployment.admin.po.project.ProjectAccountRelationPO;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 详情 :
 *
 * @author liangguanglong
 */
@Service
public class ProjectAccountRelationServiceImpl implements ProjectAccountRelationService {

    @Autowired
    private ProjectAccountRelationDao projectAccountRelationDao;

    @Autowired
    private AdminAccountService adminAccountService;

    @Override
    public void batchAddRelations(List<ProjectAccountRelation> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        projectAccountRelationDao.batchInsertRelation(relations);
    }

    @Override
    public void deleteRelationByProjectId(int projectId) {
        Assert.isTrue(projectId > 0, "项目id小于或等于0");
        projectAccountRelationDao.deleteByProjectId(projectId);
    }

    @Override
    public List<ProjectAccountRelation> getByProjectId(int projectId) {
        List<ProjectAccountRelationPO> accountRelationPOs = projectAccountRelationDao.getByProjectId(projectId);
        if (CollectionUtils.isEmpty(accountRelationPOs)) {
            return Collections.emptyList();
        }
        List<ProjectAccountRelation> accountRelation = VOUtil.fromList(accountRelationPOs, ProjectAccountRelation.class);
        return accountRelation;
    }

    @Override
    public List<ProjectAccountRelation> getByProjectIds(List<Integer> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(projectAccountRelationDao.getByProjectIds(projectIds), ProjectAccountRelation.class);
    }

    @Override
    public boolean hasRelation(long accountId, int projectId) {
        ProjectAccountRelationPO relation = projectAccountRelationDao.get(accountId, projectId);
        return (relation != null);
    }

    @Override
    public boolean isAdmin(long accountId, int projectId) {
        ProjectAccountRelationPO relation = projectAccountRelationDao.get(accountId, projectId);
        return (relation != null && relation.getIsAdmin() == Constants.TRUE);
    }

    @Override
    public boolean canModify(long accountId, int projectId) {
        return isAdmin(accountId, projectId) || adminAccountService.isSuperAdmin(accountId);
    }

    @Override
    public List<Long> getAllManagerId() {
        List<Long> allAdminId = projectAccountRelationDao.getAllAdminId();
        return allAdminId;
    }
}
