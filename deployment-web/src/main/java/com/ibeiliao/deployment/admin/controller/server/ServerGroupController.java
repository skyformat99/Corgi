/*
package com.ibeiliao.deployment.admin.controller.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.server.ServerGroupService;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.base.ApiCode;

*/
/**
 * @描述:
 * @author: liuhaihui
 * @date: 2017年1月16日下午1:27:41
 * @version: 1.0
 * @see:
 *//*

@Menu(name="服务器组列表", parent = "服务器组管理", sequence = 5111110)
@Controller
@RequestMapping("/admin/server/group/")
public class ServerGroupController {

    @Autowired
    private ServerGroupService serverGroupService;
    
    @Autowired
    private ServerService serverService;
    
    
    
    */
/**
     * http://localhost:8080/admin/server/group/addServerGroup.do
     * http://localhost:8080/admin/server/group/addServerGroup.do?groupName=NEW-SERVER-GROUP-BY-MODIFY1484569812319
     * 新增服务器组
     * @return
     *//*

    @RequestMapping("addServerGroup")
    @ResponseBody
    public RestResult<List<ServerGroup>> addServerGroup(String groupName) {
		try {
			ServerGroup serverGroup = new ServerGroup();
			serverGroup.setStatus(Short.valueOf("1"));
			serverGroup.setGroupName("NEW-SERVER-GROUP-BY-CODE" + System.currentTimeMillis());
			if(StringUtils.isNoneBlank(groupName)){
				serverGroup.setGroupName(groupName);
			}
			int groupId = serverGroupService.saveOrUpdate(serverGroup);
			return new RestResult<>(ApiCode.SUCCESS, "保存成功！groupId=" + groupId);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
    }
    */
/**
     * http://localhost:8080/admin/server/group/updateServerGroup.do?groupId=
     * 修改服务器组
     * @return
     *//*

    @RequestMapping("updateServerGroup")
    @ResponseBody
    public RestResult<List<ServerGroup>> updateServerGroup(@RequestParam int groupId) {
    	try {
			ServerGroup serverGroup = serverGroupService.get(groupId);
			serverGroup.setGroupName("NEW-SERVER-GROUP-BY-MODIFY" + System.currentTimeMillis());
			int updatedGroupId = serverGroupService.saveOrUpdate(serverGroup);
			return new RestResult<>(ApiCode.SUCCESS, "更新成功！groupId=" + updatedGroupId);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
    }
    
    */
/**
     * 增加服务器主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     * @return
     *//*

    @MenuResource("服务器组列表主页")
    @RequestMapping("list.xhtml")
    public String index() {
        return ("/account/list");
    }


    */
/**
     * http://localhost:8080/admin/server/group/listAll.do
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("listAll")
    @ResponseBody
    public RestResult<List<ServerGroup>> listAll() {
        List<ServerGroup> list = serverGroupService.getAll();
        return new RestResult<>(list);
    }
    
    */
/**
     * http://localhost:8080/admin/server/group/get.do?historyId=2
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("get")
    @ResponseBody
    public RestResult<ServerGroup> get(@RequestParam int groupId) {
    	ServerGroup serverGroup = serverGroupService.get(groupId);
        return new RestResult<>(serverGroup);
    }
    
    
    */
/**
     * http://localhost:8080/admin/server/group/listAllServers.do
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("listAllServers")
    @ResponseBody
    public RestResult<List<Server>> listAllServers(@RequestParam int groupId) {
    	List<Server> serverList = serverService.getByGroupId(groupId);
        return new RestResult<>(serverList);
    }
    
    
    
    */
/**
     * http://localhost:8080/admin/server/group/add.do
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("add")
    @ResponseBody
    public RestResult<String> add(@RequestParam ServerGroup serverGroup) {
        try {
			serverGroupService.saveOrUpdate(serverGroup);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
        return new RestResult<>(null);
    }
    
    */
/**
     * http://localhost:8080/admin/server/group/modify.do
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("modify")
    @ResponseBody
    public RestResult<ServerGroup> modify(@RequestParam ServerGroup serverGroup) {
    	try {
			serverGroupService.saveOrUpdate(serverGroup);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
        return new RestResult<>(null);
    }
    

    */
/**
     * http://localhost:8080/admin/server/group/delete.do?serverGroupId=1
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @RequestMapping("delete")
    @ResponseBody
    public RestResult<List<ServerGroup>> delete(@RequestParam int groupId) {
    	try {
			serverGroupService.deleteByGroupId(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
        return new RestResult<>(null);
    }
    
    
}
*/
