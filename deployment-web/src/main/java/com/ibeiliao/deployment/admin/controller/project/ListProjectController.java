package com.ibeiliao.deployment.admin.controller.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ibeiliao.deployment.admin.annotation.log.AdminLog;
import com.ibeiliao.deployment.admin.common.PageResult;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 详情 :  项目列表
 *
 * @author liangguanglong
 */
@Menu(name = "项目列表", parent = "项目管理", sequence = 500039)
@Controller
@RequestMapping("admin/project")
public class ListProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ListProjectController.class);

    @Autowired
    private ProjectService projectService;


    /**
     * 项目列表
     */
    @RequestMapping("listProject.xhtml")
    @MenuResource("项目列表")
    public String list(HttpServletRequest request) {

        request.setAttribute("projectManagers", projectService.queryAllManager());

        return "/project/list_project";
    }

    /**
     * 获取所有的环境
     */
    @ResponseBody
    @RequestMapping("list")
    @MenuResource("读取项目列表")
    public PageResult<List<Project>> listEnv(String projectName, String projectLanguage, Long projectManagerId, int page, int pageSize) {
        long accountId = AdminContext.getAccountId();
        List<Project> projects = projectService.queryAdminProjects(accountId, projectLanguage, projectManagerId, projectName, page, pageSize);

        PageResult<List<Project>> pageResult = new PageResult<>(projects);
        pageResult.setCurrentPage(page);
        pageResult.setPageSize(pageSize);
        return pageResult;
    }

    /**
     * 保存项目
     */
    @RequestMapping("save.do")
    @ResponseBody
    @MenuResource("保存项目")
    @AdminLog
    public RestResult<Object> saveProject(@ModelAttribute("project") @Valid Project project, BindingResult bindingResult,
                                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return new RestResult<>(ApiCode.PARAMETER_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        long accountId = AdminContext.getAccountId();
        String projectAccountRelationJson = request.getParameter("projectAccountRelationJson");
        if (StringUtils.isEmpty(projectAccountRelationJson)) {
            return new RestResult<>(ApiCode.PARAMETER_ERROR, "至少要有一个项目成员");
        }
        List<ProjectAccountRelation> relations = JSON.parseObject(projectAccountRelationJson, new TypeReference<List<ProjectAccountRelation>>() {
        });
        project.setProjectAccountRelations(relations);
        projectService.saveProject(accountId, project);

        return new RestResult<>(null);
    }
}
