package com.ibeiliao.deployment.admin.controller.account;

import com.ibeiliao.deployment.admin.common.PageResult;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.account.RoleService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.account.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 角色管理
 * @author linyi 2015/6/10.
 */
@Menu(name="角色列表", parent = "帐号管理", sequence = 500000)
@Controller
@RequestMapping("/admin/account/")
public class ListRoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 增加管理员主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     * @return
     */
    @MenuResource("角色列表主页")
    @RequestMapping("listRole.xhtml")
    public String index() {
        return ("/account/list_role");
    }

    /**
     * 读取管理员列表
     * @param keyword 搜索的关键字
     * @param page 第几页
     * @param pageSize 每页多少
     * @return
     */
    @RequestMapping("queryRole")
    @ResponseBody
    @MenuResource("角色列表")
    public PageResult<List<Role>> listRole(
            String keyword,
            int page,
            int pageSize,
            HttpServletRequest request) {
        List<Role> list = roleService.listRoles(keyword, page, pageSize);
        int total = roleService.statRole(keyword);

        PageResult<List<Role>> result = new PageResult<>(list);
        result.setCurrentPage(page);
        result.setPageSize(pageSize);
        result.setCount(total);
        return result;
    }

    /**
     * 返回所有的角色列表
     * @return result.object=List，List不为null
     */
    @MenuResource("读取所有角色列表")
    @RequestMapping("allRoles")
    @ResponseBody
    public RestResult<List<Role>> listAllRoles() {
        List<Role> list = roleService.listAll();
        return new RestResult<>(list);
    }
}
