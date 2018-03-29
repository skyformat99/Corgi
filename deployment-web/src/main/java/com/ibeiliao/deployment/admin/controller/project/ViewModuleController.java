package com.ibeiliao.deployment.admin.controller.project;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.admin.vo.project.*;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 功能：编辑模块
 * 详细：
 *
 * @author liangguanglong, 迁移by linyi, 2017/2/4.
 */
@Menu(name = "模块详情", parent = "项目管理", sequence = 500037)
@Controller
@RequestMapping("admin/project")
public class ViewModuleController {

    private static final Logger logger = LoggerFactory.getLogger(ViewModuleController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ServerGroupService serverGroupService;

    /**
     * 编辑或者添加项目模块
     */
    @RequestMapping("editModule.xhtml")
    @MenuResource("编辑模块")
    public String editProjectModule() {
        return "/project/edit_module";
    }

    /**
     * 添加模块 初始化的项目和服务器组数据
     */
    @RequestMapping("moduleBaseInfo")
    @ResponseBody
    @MenuResource("加载初始化的项目和服务器组数据")
    public RestResult loadModuleBaseInfo(int projectId) {
        Project project = projectService.getProject(projectId);
        ModuleDetailInfo moduleDetailInfo = new ModuleDetailInfo();
        moduleDetailInfo.setProject(project);

        List<ProjectEnv> allEnv = projectEnvService.findAllEnv();
        moduleDetailInfo.setEnvs(allEnv);

        List<ModuleJvm> moduleJvmArgses = buildModuleJvms(allEnv);
        moduleDetailInfo.setModuleJvms(moduleJvmArgses);

        return new RestResult<>(moduleDetailInfo);
    }

    private List<ModuleJvm> buildModuleJvms(List<ProjectEnv> allEnv) {
        List<ModuleJvm> moduleJvmArgses = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(allEnv)) {
            for (ProjectEnv env : allEnv) {
                ModuleJvm moduleJvm = new ModuleJvm();
                moduleJvm.setEnvId(env.getEnvId());
                moduleJvm.setEnvName(env.getEnvName());
                moduleJvmArgses.add(moduleJvm);
            }
        }
        return moduleJvmArgses;
    }

    /**
     * 获取模块
     */
    @RequestMapping("getModule")
    @ResponseBody
    @MenuResource("获取模块数据")
    public RestResult getModule(int moduleId) {

        ModuleDetailInfo moduleDetailInfo = new ModuleDetailInfo();
        ProjectModule projectModule = projectModuleService.getByModuleId(moduleId);
        Project project = projectService.getProject(projectModule.getProjectId());
        List<ServerGroup> serverGroups = serverGroupService.getByModuleIds(Lists.newArrayList(moduleId), true);

        projectModule.setServerGroups(serverGroups);
        moduleDetailInfo.setProject(project);
        moduleDetailInfo.setProjectModule(projectModule);
        moduleDetailInfo.setEnvs(projectEnvService.findAllEnv());

        return new RestResult<>(moduleDetailInfo);
    }

    /**
     * 保存模块详情
     *
     * @param projectModule 模块内容,包含模块的详情以及服务器的关联关系
     * @return
     */
    @RequestMapping("saveModule.do")
    @ResponseBody
    @MenuResource("保存模块详情")
    public RestResult saveModule(@RequestBody ProjectModule projectModule) {
        long accountId = AdminContext.getAccountId();
        try {
            projectModuleService.saveProjectModule(accountId, projectModule);
            return new RestResult<>(ApiCode.SUCCESS);
        } catch (Exception e) {
            logger.error("保存项目模块报错", e);
            return new RestResult<>(ApiCode.FAILURE, "保存出错: " + e.getMessage());
        }
    }

    /**
     * 检测新服务器是否ping通
     * @return
     */
    @RequestMapping("ping")
    @ResponseBody
    @MenuResource("ping服务器")
    public RestResult pingServerIp(String ip) {
        AnsibleCommandResult result = CommandUtil.ansiblePing(Lists.newArrayList(ip));
        if (result.isSuccess()) {
            return new RestResult<>(ApiCode.SUCCESS);
        }
        return new RestResult<>(ApiCode.FAILURE, "ping不通该服务器");
    }

    /**
     * 检测配置的resin端口有没有被相同ip的机器的其他模块占用
     * @param projectModule
     */
    @RequestMapping("checkResinPort")
    @ResponseBody
    public RestResult checkResinPort(@RequestBody ProjectModule projectModule) {

        Map<String, String> port2OccupyInfoMap = projectModuleService.checkResinPortOccupy(projectModule);
        if (port2OccupyInfoMap.isEmpty()) {
            return new RestResult<>(ApiCode.SUCCESS);
        }

        StringBuilder occupyInfo = new StringBuilder();
        for (Map.Entry<String, String> entry : port2OccupyInfoMap.entrySet()) {
            occupyInfo.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }

        return new RestResult<>(ApiCode.FAILURE, occupyInfo.toString());
    }

    /**
     * 这里读取阿里云的所有服务器，如果没有阿里云服务器，返回 new RestResult<>(null)
     * @return
     */
    @RequestMapping("getAllServers")
    @ResponseBody
    public RestResult<List<AliyunEcs>> getAllServers() {
        return new RestResult<>();
    }
}
