package com.ibeiliao.deployment.admin.service.global.impl;

import com.ibeiliao.deployment.admin.dao.global.ProjectEnvDao;
import com.ibeiliao.deployment.admin.po.global.ProjectEnvPO;
import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.common.Constants;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 详情 : 环境service
 *
 * @author liangguanglong
 */
@Service
public class ProjectEnvServiceImpl implements ProjectEnvService {

    @Autowired
    private ProjectEnvDao projectEnvDao;

    private static final String ENV_REGX = "^[A-Za-z0-9_-]+$";

    @Override
    public List<ProjectEnv> findEnvList(String envName, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        List<ProjectPO> projectPOs = projectEnvDao.listEnv(buildLike(envName), startIndex, pageSize);
        if (CollectionUtils.isEmpty(projectPOs)) {
            return Collections.emptyList();
        }
        List<ProjectEnv> projectEnvList = VOUtil.fromList(projectPOs, ProjectEnv.class);
        return projectEnvList;
    }

    private String buildLike(String envName) {
        envName = StringUtils.defaultString(envName);
        if (StringUtils.isNotEmpty(envName)) {
            envName = "%" + envName + "%";
        }
        return envName;
    }

    @Override
    public int findEnvTotalCount(String envName) {
        int totalCount = projectEnvDao.findTotalCount(buildLike(envName));
        return totalCount;
    }

    @Override
    public void saveEnv(ProjectEnv projectEnv) {
        Assert.notNull(projectEnv, "参数不能为null");
        Assert.notNull(projectEnv.getEnvName(), "envName不能为null");
        Assert.isTrue(projectEnv.getEnvName().matches(ENV_REGX), "环境名称必须是字母、数字、_、-的组合");

        ProjectEnvPO projectEnvPO = VOUtil.from(projectEnv, ProjectEnvPO.class);
        if (projectEnv.getEnvId() > 0) {
            projectEnvDao.update(projectEnvPO);
        } else {
            projectEnvDao.insert(projectEnvPO);
        }
    }

    @Override
    public int deleteEnv(int projectEnvId) {
        return projectEnvDao.delete(projectEnvId);
    }

    @Override
    public List<ProjectEnv> findAllEnv() {
        List<ProjectPO> allEnv = projectEnvDao.findAllEnv();
        List<ProjectEnv> projectEnvList = VOUtil.fromList(allEnv, ProjectEnv.class);
        return projectEnvList;
    }

    @Override
    public ProjectEnv getById(int envId) {
        ProjectEnvPO projectEnvPO = projectEnvDao.get(envId);
        return VOUtil.from(projectEnvPO, ProjectEnv.class);
    }

    @Override
    public boolean isOnline(int envId) {
        ProjectEnvPO po = projectEnvDao.get(envId);
        return (po != null && po.getOnlineFlag() == Constants.TRUE);
    }
}
