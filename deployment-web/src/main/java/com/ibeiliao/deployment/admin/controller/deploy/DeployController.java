package com.ibeiliao.deployment.admin.controller.deploy;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.base.ApiCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：显示发布中的内容和日志
 * 详细：
 *
 * @author linyi, 2017/2/6.
 */
@Menu(name="发布", parent = "项目发布", sequence = 700000)
@Controller
@RequestMapping("/admin/deploy/")
public class DeployController {

    @Autowired
    private DeployHistoryService deployHistoryService;

    /**
     * 发布中主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     * @return
     */
    @MenuResource("发布主页")
    @RequestMapping("deploy.xhtml")
    public String index(HttpServletRequest request) throws Exception {
        Integer historyId = ServletRequestUtils.getIntParameter(request, "historyId");
        if (historyId != null) {
            DeployHistory history = deployHistoryService.getByHistoryId(historyId);
            request.setAttribute("history", history);
            request.setAttribute("canDeploy", AdminContext.getAccountId() == history.getAccountId()
                && history.getDeployStatus() == DeployStatus.WAITING_FOR_DEPLOYMENT.getValue());
        }
        return ("/deploy/deploy");
    }

    /**
     * 开始发布
     * @return
     */
    @MenuResource("开始发布")
    @RequestMapping("startDeploy.do")
    @ResponseBody
    public RestResult<Object> startDeploy(int historyId) {
        try {
            deployHistoryService.startDeploy(historyId, AdminContext.getAccountId());
        } catch (RuntimeException e) {
            return new RestResult<>(ApiCode.PARAMETER_ERROR, e.getMessage());
        }
        return new RestResult<>(null);
    }
}
