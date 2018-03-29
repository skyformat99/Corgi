package com.ibeiliao.deployment.admin.controller.deploy;

import com.ibeiliao.deployment.admin.utils.resource.Menu;
import org.springframework.stereotype.Controller;

/**
 * 发布管理,仅用于菜单
 * 
 * @author 林毅 2017/1/16.
 */
@Controller
@Menu(name = "项目发布", parent = "", sequence = 950000)
public class DeploymentManagementController {
}
