package com.ibeiliao.deployment.admin.service.project.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibeiliao.deployment.admin.dao.project.ProjectDao;
import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;
import com.ibeiliao.deployment.admin.vo.project.ProjectModule;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 详情 : 项目 service
 *
 * @author liangguanglong
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectAccountRelationService relationService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Override
    @Transactional
    public Project saveProject(long accountId, Project project) {
        Assert.notNull(project, "项目为空!");
        Assert.isTrue(StringUtils.isNotBlank(project.getProjectName()), "项目名称为空!");
        Assert.isTrue(StringUtils.isNotBlank(project.getProjectNo()), "项目编号为空!");
        checkProjectAccountRelations(project.getProjectAccountRelations());

        ProjectPO projectPO = VOUtil.from(project, ProjectPO.class);
        if (project.getProjectId() > 0) {
            ProjectPO current = projectDao.get(project.getProjectId());
            Assert.notNull(current, "项目不存在");
            projectPO.setProjectNo(current.getProjectNo()); // 禁止修改
            projectDao.update(projectPO);
        } else {
            ProjectPO current = projectDao.getByProjectNo(project.getProjectNo());
            if (current != null) {
                throw new IllegalArgumentException("项目[" + project.getProjectNo() + "]已经存在");
            }
            projectPO.setCreateTime(new Date());
            projectDao.insert(projectPO);
            project.setProjectId(projectPO.getProjectId());
        }

        saveProjectRelations(project);
        return VOUtil.from(projectPO, Project.class);
    }

    @Override
    public Project getProject(int projectId) {
        Assert.isTrue(projectId > 0, "项目id小于或等于0!");

        ProjectPO projectPO = projectDao.get(projectId);
        return VOUtil.from(projectPO, Project.class);
    }

    @Override
    public List<Project> queryAdminProjects(long accountId, String projectLanguage, Long projectManagerId, String projectName, int page, int pageSize) {
        if (adminAccountService.isSuperAdmin(accountId)) {
            return queryProject(projectName, projectLanguage, projectManagerId, page, pageSize);
        }
        int startIndex = (page - 1) * pageSize;
        List<Project> projectList = VOUtil.fromList(projectDao.queryAdminProjects(accountId, processLike(projectName), projectLanguage, projectManagerId, startIndex, pageSize), Project.class);
        fillJoinerNamesIntoProject(projectList);
        return projectList;
    }

    @Override
    public List<AdminAccount> queryAllManager() {
        List<Long> managerIds = relationService.getAllManagerId();
        if (CollectionUtils.isEmpty(managerIds)) {
            return Collections.emptyList();
        }
        return adminAccountService.findByAccountIds(managerIds);
    }

    @Override
    public List<Project> queryAllProjectByAccountId(long accountId, boolean needModuleInfo) {
        List<ProjectPO> projectPOS = projectDao.queryByAccountId(accountId);
        if (CollectionUtils.isEmpty(projectPOS)) {
            return Collections.emptyList();
        }

        List<Project> projects = VOUtil.fromList(projectPOS, Project.class);
        if (needModuleInfo) {
            fillModuleInfo(projects);
        }
        return projects;
    }

    private void fillModuleInfo(List<Project> projects) {
        List<Integer> projectIds = Lists.newArrayList();
        for (Project project : projects) {
            projectIds.add(project.getProjectId());
        }
        List<ProjectModule> projectModules = projectModuleService.getSimpleInfoByProjectIds(projectIds);
        if (CollectionUtils.isEmpty(projectModules)) {
            return;
        }
        ListMultimap<Integer, ProjectModule> projectId2ModulesMap = ArrayListMultimap.create();
        for (ProjectModule module : projectModules) {
            projectId2ModulesMap.put(module.getProjectId(), module);
        }
        for (Project project : projects) {
            project.setProjectModules(projectId2ModulesMap.get(project.getProjectId()));
        }
    }

    private String processLike(String projectName) {
        projectName = StringUtils.defaultString(projectName);
        if (StringUtils.isNotEmpty(projectName)) {
            projectName = "%" + projectName + "%";
        }
        return projectName;
    }

    private List<Project> queryProject(String projectName, String projectLanguage, Long projectManagerId, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;

        List<ProjectPO> projectPOs;
        if (projectManagerId == 0) {
            projectPOs = projectDao.queryProject(processLike(projectName), projectLanguage, projectManagerId, startIndex, pageSize);
        } else {
            projectPOs = projectDao.queryProjectWithManager(processLike(projectName), projectLanguage, projectManagerId, startIndex, pageSize);
        }
        if (CollectionUtils.isEmpty(projectPOs)) {
            return Collections.emptyList();
        }
        List<Project> projectList = VOUtil.fromList(projectPOs, Project.class);
        fillJoinerNamesIntoProject(projectList);
        return projectList;
    }

    private void saveProjectRelations(Project project) {
        List<ProjectAccountRelation> projectAccountRelations = project.getProjectAccountRelations();
        for (ProjectAccountRelation relation : projectAccountRelations) {
            relation.setProjectId(project.getProjectId());
        }
        relationService.deleteRelationByProjectId(project.getProjectId());
        relationService.batchAddRelations(filterRedundantRelation(projectAccountRelations));
    }

    private List<ProjectAccountRelation> filterRedundantRelation(List<ProjectAccountRelation> relations) {
        HashMap<Long, ProjectAccountRelation> accountId2RelationMap = Maps.newHashMap();
        for (ProjectAccountRelation relation : relations) {
            if (accountId2RelationMap.get(relation.getAccountId()) == null) {
                accountId2RelationMap.put(relation.getAccountId(), relation);
            } else {
                ProjectAccountRelation accountRelation = accountId2RelationMap.get(relation.getAccountId());
                if (accountRelation.getIsAdmin() == Constants.FALSE) {
                    accountId2RelationMap.put(relation.getAccountId(), relation);
                }
            }
        }
        return Lists.newArrayList(accountId2RelationMap.values());
    }

    private void checkProjectAccountRelations(List<ProjectAccountRelation> list) {
        Assert.isTrue(!CollectionUtils.isEmpty(list), "项目至少要有一个成员");

        int adminNum = 0;
        for (ProjectAccountRelation relation : list) {
            if (relation.getIsAdmin() == Constants.TRUE) {
                adminNum++;
            }
        }
        if (adminNum == 0) {
            throw new IllegalArgumentException("项目至少要有一个管理员");
        }
        if (adminNum > 3) {
            throw new IllegalArgumentException("项目最多只能有三个管理员");
        }

    }

    private void fillJoinerNamesIntoProject(List<Project> projects) {
        if (CollectionUtils.isEmpty(projects)) {
            return;
        }
        // 创建项目和账户关系
        ArrayList<Integer> projectIds = Lists.newArrayList();
        for (Project project : projects) {
            projectIds.add(project.getProjectId());
        }
        List<ProjectAccountRelation> relations = relationService.getByProjectIds(projectIds);
        Map<String, ProjectAccountRelation> acntProId2RelationMap = buildAccount2RelationMap(relations);

        ArrayList<Long> accountIds = Lists.newArrayList();
        for (ProjectAccountRelation relation : relations) {
            accountIds.add(relation.getAccountId());
        }
        ArrayListMultimap<Integer, Long> projectId2RelationPOsMap = ArrayListMultimap.create();
        for (ProjectAccountRelation relation : relations) {
            projectId2RelationPOsMap.put(relation.getProjectId(), relation.getAccountId());
        }
        // 账户和名字map
        List<AdminAccount> accountList = adminAccountService.findByAccountIds(accountIds);
        HashMap<Long, String> accountId2RealNameMap = Maps.newHashMap();
        for (AdminAccount account : accountList) {
            accountId2RealNameMap.put(account.getUid(), account.getRealname());
        }

        for (Project project : projects) {
            List<Long> joinerAccountIds = projectId2RelationPOsMap.get(project.getProjectId());
            List<String> projectJoiners = Lists.newArrayList();
            List<String> projectManagers = Lists.newArrayList();
            for (Long joinerAccountId : joinerAccountIds) {
                if (acntProId2RelationMap.get(joinerAccountId + "-" + project.getProjectId()).getIsAdmin() == Constants.TRUE) {
                    projectManagers.add(accountId2RealNameMap.get(joinerAccountId));
                } else {
                    projectJoiners.add(accountId2RealNameMap.get(joinerAccountId));
                }
            }
            project.setJoinerNames(StringUtils.join(projectJoiners, ","));
            project.setManagers(StringUtils.join(projectManagers, ","));
        }
    }

    private Map<String, ProjectAccountRelation> buildAccount2RelationMap(List<ProjectAccountRelation> relations) {
        Map<String, ProjectAccountRelation> accountId2RelationMap = Maps.newHashMap();
        for (ProjectAccountRelation relation : relations) {
            accountId2RelationMap.put(relation.getAccountId() + "-" + relation.getProjectId(), relation);
        }
        return accountId2RelationMap;
    }

}
