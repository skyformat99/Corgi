package com.ibeiliao.deployment.admin.controller.deploy;

import com.alibaba.fastjson.JSON;
import com.ibeiliao.deployment.admin.annotation.log.AdminLog;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.deploy.ModuleStatusManagementService;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.base.ApiCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 服务器部署记录
 *
 * @author linyi 2017/1/20
 */
@Menu(name = "发布记录", parent = "项目发布", sequence = 900000)
@Controller
@RequestMapping("/admin/deploy/")
public class DeployHistoryController {

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ModuleStatusManagementService moduleStatusManagementService;

    @Autowired
    private ProjectModuleService projectModuleService;

    @Autowired
    private ProjectService projectService;

    /**
     * 发布记录列表主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     *
     * @return
     */
    @MenuResource("发布记录列表主页")
    @RequestMapping("list.xhtml")
    public String index(HttpServletRequest request) {
        request.setAttribute("envList", projectEnvService.findAllEnv());

        List<Project> projects = projectService.queryAllProjectByAccountId(AdminContext.getAccountId(), true);
        request.setAttribute("projects", projects);

        String projectJson = JSON.toJSONString(projects);
        request.setAttribute("projectJson", projectJson);

        request.setAttribute("accountId", AdminContext.getAccountId());
        return ("/deploy/list_deployment");
    }

    /**
     * 查询发布记录列表，按创建时间倒序
     *
     * @return result.object=List，List不为null
     */
    @RequestMapping("list")
    @ResponseBody
    @MenuResource("发布记录列表")
    public RestResult<List<DeployHistory>> list(@RequestParam(required = false, defaultValue = "0") int envId,
                                                @RequestParam(required = false, defaultValue = "0") int projectId,
                                                @RequestParam(required = false, defaultValue = "0") int moduleId,
                                                int page, int pageSize) {
        long accountId = AdminContext.getAccountId();
        List<DeployHistory> list = deployHistoryService.queryDeployHistory(accountId, envId, projectId,moduleId, page, pageSize);
        return new RestResult<>(list);
    }

    @RequestMapping("getDeployingNum")
    @ResponseBody
    @MenuResource("正在进行发布的数量")
    public RestResult<Integer> getDeployingNum() {
        return new RestResult<>(moduleStatusManagementService.getDeployingNum());
    }

    /**
     * 审核发布记录
     *
     * @return
     */
    @RequestMapping(value = "audit.do", method = RequestMethod.POST)
    @ResponseBody
    @MenuResource("审核发布记录")
    @AdminLog
    public RestResult<Object> audit(int historyId) {
        long accountId = AdminContext.getAccountId();
        deployHistoryService.audit(accountId, historyId);
        return new RestResult<>(null);
    }

    @RequestMapping(value = "redo.do", method = RequestMethod.POST)
    @ResponseBody
    @MenuResource("REDO发布记录")
    @AdminLog
    public RestResult<Object> redo(int historyId) {
        long accountId = AdminContext.getAccountId();
        deployHistoryService.createRollbackOrder(historyId, accountId,false);
        return new RestResult<>(ApiCode.SUCCESS, "创建上线单成功，请审核或发布");
    }

    @RequestMapping(value = "rollBack.do", method = RequestMethod.POST)
    @ResponseBody
    @MenuResource("回滚")
    @AdminLog
    public RestResult<Object> rollBack(int historyId) {
        long accountId = AdminContext.getAccountId();
        deployHistoryService.createRollbackOrder(historyId, accountId, true);
        return new RestResult<>(ApiCode.SUCCESS, "创建回滚单成功，请审核或执行回滚");
    }

    @RequestMapping(value = "cancel.do", method = RequestMethod.POST)
    @ResponseBody
    @MenuResource("取消发布记录")
    @AdminLog
    public RestResult<Object> cancel(int historyId) {
        long accountId = AdminContext.getAccountId();
        deployHistoryService.cancel(accountId, historyId);
        return new RestResult<>(null);
    }

    @RequestMapping(value = "reject.do", method = RequestMethod.POST)
    @ResponseBody
    @MenuResource("拒绝发布记录")
    @AdminLog
    public RestResult<Object> reject(int historyId) {
        long accountId = AdminContext.getAccountId();
        deployHistoryService.reject(accountId, historyId);
        return new RestResult<>(null);
    }
}
