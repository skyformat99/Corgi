/*
package com.ibeiliao.deployment.admin.controller.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibeiliao.deployment.admin.common.PageResult;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.server.ServerService;
import com.ibeiliao.deployment.admin.utils.resource.Menu;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.server.Server;
import com.ibeiliao.deployment.admin.vo.server.ServerGroup;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.platform.util.GsonUtil;

*/
/**
 * @描述:服务器管理
 * @author: liuhaihui
 * @date: 2017年1月13日下午10:10:16
 * @version: 1.0
 * @see:
 *//*

@Menu(name="服务器列表", parent = "服务器管理", sequence = 5111110)
@Controller
@RequestMapping("/admin/server/")
public class ServerController {

	private static Logger logger = LoggerFactory.getLogger(ServerController.class);
	
    @Autowired
    private ServerService serverService;
    
    */
/**
     * http://localhost:8080/admin/server/addServer.do
     * http://localhost:8080/admin/server/addServer.do?ipAddress=192.168.1.253
     * 新增服务器
     * @return
     *//*

    @RequestMapping("addServer")
    @ResponseBody
    public RestResult<List<ServerGroup>> addServer(int moduleId, int groupId, String ipAddress) {
		try {
			Server server = new Server();
			server.setModuleId(25);
			if(moduleId > 0){
				server.setModuleId(moduleId);
			}
			server.setGroupId(1);
			if(groupId > 0){
				server.setGroupId(groupId);
			}
			server.setStatus(Short.valueOf("1"));
			server.setIp("192.168.1.253");
			if(StringUtils.isNotBlank(ipAddress)){
				server.setIp(ipAddress);
			}
			server.setServerName("serverName-Add-" + System.currentTimeMillis());
			
			int serverId = serverService.saveOrUpdate(server);
			return new RestResult<>(ApiCode.SUCCESS, "保存成功！serverId=" + serverId);
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
    }
    */
/**
     * http://localhost:8080/admin/server/updateServer.do?groupId=
     * 修改服务器
     * @return
     *//*

    @RequestMapping("updateServer")
    @ResponseBody
    public RestResult<List<ServerGroup>> updateServer(@RequestParam int serverId) {
    	try {
			Server server = serverService.get(serverId);
			server.setServerName("serverName-Update-" + System.currentTimeMillis());
			int updatedServerId = serverService.saveOrUpdate(server);
			return new RestResult<>(ApiCode.SUCCESS, "更新成功！groupId=" + updatedServerId);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
    }
    
    */
/**
     * http://localhost:8080/admin/server/deleteByIpAndGroupId.do?groupId=1&ip=192.168.1.25
     * 根据ip, groupId删除记录
	 * @return 删除的行数
     *//*

    @RequestMapping("deleteByIpAndGroupId")
    @ResponseBody
    public RestResult<Integer> deleteByIpAndGroupId(String ip, int groupId){
    	try {
			int deleteCount = serverService.deleteByIpAndGroupId(ip, groupId);
			if(deleteCount > 0){
				return new RestResult<>(ApiCode.SUCCESS, "删除成功！");
			}
			return new RestResult<>(ApiCode.SUCCESS, "删除失败！");
		} catch (Exception e) {
			e.printStackTrace();
			return new RestResult<>(ApiCode.FAILURE, e.getMessage());
		}
    }
    
	*/
/**
	 * http://localhost:8080/admin/server/getUnderDeployingServerList.do?historyId=3
     * 获取[发布正在进行中]页面的服务器列表，包含分组
	 * @param historyId
	 * @param historyId
	 * @return
	 *//*

    @RequestMapping("getUnderDeployingServerList")
    @ResponseBody
    public RestResult<Map<String, ArrayList<String>>> getUnderDeployingServerList(@RequestParam int historyId) {
    	logger.info("获取[发布正在进行中]页面的服务器列表，包含分组,historyId={}",historyId);
    	Map<String, ArrayList<String>> underDeployingServerList = serverService.getUnderDeployingServerList(historyId);
        //System.out.println(GsonUtil.toJson(underDeployingServerList));
        return new RestResult<>(underDeployingServerList);
    }
    
    

    */
/**
     * 增加服务器主页，xhtml 仅用于展示页面，ajax 调用 .do 接口获取参数
     * @return
     *//*

    @MenuResource("服务器列表主页")
    @RequestMapping("listServer.xhtml")
    public String index() {
        return ("/account/list_server");
    }

    */
/**
     * http://localhost:8080/admin/server/listServer.do
     * 读取服务器列表
     * @param moduleId 模块id
     * @param page 第几页
     * @param pageSize 每页多少
     * @return
     *//*

    @RequestMapping("listServer")
    @ResponseBody
    @MenuResource("服务器列表")
    public PageResult<List<Server>> listServer(int moduleId,int pageNo,int pageSize,HttpServletRequest request) {
    	
        List<Server> list = serverService.getPageByModuleId(moduleId, pageNo, pageSize);
        int total = 100;//serverService.count(moduleId);

        PageResult<List<Server>> result = new PageResult<>(list);
        result.setCurrentPage(pageNo);
        result.setPageSize(pageSize);
        result.setCount(total);
        return result;
    }

    */
/**
     * http://localhost:8080//admin/server/allServers.do
     * 返回所有的服务器列表
     * @return result.object=List，List不为null
     *//*

    @MenuResource("读取所有服务器列表")
    @RequestMapping("allServers")
    @ResponseBody
    public RestResult<List<Server>> listAllServers() {
        List<Server> list = serverService.getAll();
        return new RestResult<>(list);
    }
}
*/
