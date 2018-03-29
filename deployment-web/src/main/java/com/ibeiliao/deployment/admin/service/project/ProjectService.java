package com.ibeiliao.deployment.admin.service.project;

import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.project.Project;

import java.util.List;

/**
 * 详情 :  项目service
 *
 * @author liangguanglong
 */
public interface ProjectService {

    /**
     * 保存项目及成员关系
     * @param accountId  管理员ID，更新项目的时候用来判断权限
     * @param project    要保存的内容，包括成员列表
     */
    Project saveProject(long accountId, Project project);

    /**
     * 根据id查找项目
     *
     * @return
     */
    Project getProject(int projectId);

    /**
     * 查询管理员能发布的所有项目列表
     * @param accountId   管理员ID，必传
     * @param projectName 项目名 (非必填)
     * @param page
     * @param pageSize
     * @return 返回可以访问的项目列表，如果没有数据，返回size=0的List
     * @author linyi 2017/1/20
     */
    List<Project> queryAdminProjects(long accountId, String projectLanguage, Long projectManagerId, String projectName, int page, int pageSize);

    /**
     * 查询所有管理员
     * @return
     */
    List<AdminAccount> queryAllManager();

    /**
     * 查找用户参与的所有项目
     * @param accountId
     * @param needModuleInfo 是否需要填充模块信息
     * @return
     */
    List<Project> queryAllProjectByAccountId(long accountId, boolean needModuleInfo);
}
