package com.ibeiliao.deployment.admin.controller.setting;

import com.ibeiliao.deployment.admin.common.PageResult;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.global.ProjectEnv;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 详情 :  环境的控制器
 *
 * @author liangguanglong
 */
@Menu(name="环境列表", parent = "全局配置管理", sequence = 500036)
@Controller
@RequestMapping("admin/env")
public class ListEnvironmentController {


    @Autowired
    private ProjectEnvService projectEnvService;

    private static final Logger logger = LoggerFactory.getLogger(ListEnvironmentController.class);

    /**
     * 查看环境
     */
    @RequestMapping("listEnv.xhtml")
    @MenuResource("环境列表")
    public String index() {
        return ("/setting/list_env");
    }

    /**
     * 获取所有的环境
     */
    @ResponseBody
    @RequestMapping("list")
    @MenuResource("读取环境列表")
    public PageResult<List<ProjectEnv>> listEnv(String envName, int page, int pageSize) {
        List<ProjectEnv> allEnv = projectEnvService.findEnvList(envName, page, pageSize);

        PageResult<List<ProjectEnv>> pageResult = new PageResult<>(allEnv);
        pageResult.setCurrentPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setCount(projectEnvService.findEnvTotalCount(envName));
        return pageResult;
    }

    /**
     * save
     */
    @ResponseBody
    @RequestMapping("save.do")
    @MenuResource("保存环境")
    public RestResult<Object> delete(int envId, String envName, int onlineFlag) {
        if (StringUtils.isBlank(envName)) {
            return new RestResult<>(ApiCode.PARAMETER_ERROR, "环境名称不能为空");
        }
        ProjectEnv projectEnv = new ProjectEnv();
        projectEnv.setEnvId(envId);
        projectEnv.setEnvName(envName.trim());
        projectEnv.setOnlineFlag(onlineFlag);
        projectEnvService.saveEnv(projectEnv);
        return new RestResult<>(null);
    }
}
