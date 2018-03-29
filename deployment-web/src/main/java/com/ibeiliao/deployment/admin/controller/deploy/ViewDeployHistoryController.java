package com.ibeiliao.deployment.admin.controller.deploy;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.server.DeployLogService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.base.ApiCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 功能：显示发布详情
 * 详细：
 *
 * @author linyi, 2017/1/22.
 */
@Menu(name="发布详情", parent = "项目发布", sequence = 800000)
@Controller
@RequestMapping("/admin/deploy/")
public class ViewDeployHistoryController {

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private ProjectAccountRelationService projectAccountRelationService;

    @Autowired
    private DeployLogService deployLogService;

    /**
     * 发布详情主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     * @return
     */
    @MenuResource("发布详情主页")
    @RequestMapping("view.xhtml")
    public String index() {
        return ("/deploy/view_deployment");
    }

    @RequestMapping("getDeployHistory")
    @ResponseBody
    @MenuResource("发布详情")
    public RestResult<DeployHistory> getDeployHistory(int historyId) {
        long accountId = AdminContext.getAccountId();
        DeployHistory deployHistory = deployHistoryService.getByHistoryId(historyId);
        if (deployHistory == null) {
            return new RestResult<>(ApiCode.FAILURE, "找不到发布记录");
        }
        return new RestResult<>(deployHistory);
    }

    @RequestMapping("getServerDeployLog")
    @ResponseBody
    @MenuResource("读取服务器发布日志")
    public RestResult<List<ServerDeployLog>> getDeployHistoryLog(int serverDeployId) {
        return new RestResult<>(deployLogService.getServerDeployLog(serverDeployId));
    }
}
