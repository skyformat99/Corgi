package com.ibeiliao.deployment.admin.controller.stat;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.global.ProjectEnvService;
import com.ibeiliao.deployment.admin.service.stat.StatService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.stat.StatProject;
import com.ibeiliao.deployment.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 功能：查询各项目的统计情况
 * 详细：
 *
 * @author linyi, 2017/2/28.
 */
@Controller
@RequestMapping("/admin/stat")
@Menu(name = "按项目统计", parent = "统计", sequence = 800000)
public class StatProjectController {

    @Autowired
    private StatService statService;

    @Autowired
    private ProjectEnvService projectEnvService;

    /**
     * 按项目统计主页
     */
    @RequestMapping("statProject.xhtml")
    @MenuResource("按项目统计主页")
    public String index(HttpServletRequest request) {
        request.setAttribute("envList", projectEnvService.findAllEnv());
        return ("/stat/stat_project");
    }

    @RequestMapping("queryStatProject")
    @MenuResource("查询各项目的发布情况")
    @ResponseBody
    public RestResult<List<StatProject>> query(int envId, @DateTimeFormat(pattern= Constants.DATE_FORMAT) Date start,
                                               @DateTimeFormat(pattern=Constants.DATE_FORMAT) Date end) {
        return new RestResult<>(statService.queryStatProject(envId, start, end));
    }
}
