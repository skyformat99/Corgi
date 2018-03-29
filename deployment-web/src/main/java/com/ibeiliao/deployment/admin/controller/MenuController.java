package com.ibeiliao.deployment.admin.controller;

import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AppConstants;
import com.ibeiliao.deployment.admin.service.account.MenuService;
import com.ibeiliao.deployment.admin.utils.resource.MenuItem;
import com.ibeiliao.deployment.admin.utils.resource.SearchResource;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 重新生成菜单的controller
 * 
 * @author linyi 2014年2月28日
 */
@Controller
@RequestMapping("/common/")
public class MenuController {

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	private MenuService menuService;

	/**
	 * 默认的scan路径
	 */
	private static final String[] DEFAULT_APP_SCAN_PATH = new String[] { SearchResource.DEFAULT_RESOURCE_PATH };

    /**
     * 返回改应用系统应该扫描的 package 路径
     * @param appId - 应用系统ID
     * @authur linyi 2014/12/16
     *
     * @return
     */
    private String[] getScanPath(int appId) {
        return DEFAULT_APP_SCAN_PATH;
    }

	/**
	 * 获取当前域名的 appId
	 * @param request
	 * @return 返回相应的appId
	 */
	private int getAppId(HttpServletRequest request) {
		return AppConstants.APP_ID_DEFAULT;
	}

	/**
	 * 重新生成系统菜单
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateMenu.do")
	public RestResult<Object> updateMenu(HttpServletRequest request) {
		int appId = getAppId(request);
		logger.info("将更新菜单, appId=" + appId);

		RestResult<Object> result = null;
		if (appId > 0) {
			String[] scanPath = getScanPath(appId);

			try {
				MenuItem root = SearchResource.scanMenuTree(scanPath);

				menuService.updateMenu(appId, root);
				logger.info("更新菜单成功，菜单: " + JsonUtil.toJSONString(root));
				result = new RestResult<>(ApiCode.SUCCESS, "成功");
			}
			catch (Exception e) {
				logger.error("重新生成系统菜单错误, appId=" + appId, e);
				result = new RestResult<>(ApiCode.FAILURE, e.getMessage());
			}
		}
		else {
			result = new RestResult<>(ApiCode.FAILURE, "appId为空");
		}
		return result;
	}

	/**
	 * 自动清除多余菜单/资源数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("autoClearMenuResource.do")
	public RestResult<Object> autoClearMenuResource(HttpServletRequest request) {
		int appId = getAppId(request);
		logger.info("将自动清除冗余菜单/资源, appId=" + appId);

		RestResult<Object> result = null;
		if (appId > 0) {
			String[] scanPath = getScanPath(appId);

			MenuItem root = SearchResource.scanMenuTree(scanPath);
			menuService.clearRedundantMenu(appId, root);

			result = new RestResult<>(ApiCode.SUCCESS, "执行成功.");
		}
		else {
			result = new RestResult<>(ApiCode.FAILURE, "appId为空");
		}

		return result;
	}

	/**
	 * 获取多余菜单/资源数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getRedundantMenuResource.do")
	public RestResult<Map<String, Object>> getRedundantMenuResource(HttpServletRequest request) {
		int appId = getAppId(request);
		logger.info("将获取冗余菜单/资源, appId=" + appId);

		RestResult<Map<String, Object>> result = null;
		if (appId > 0) {
			String[] scanPath = getScanPath(appId);

			MenuItem root = SearchResource.scanMenuTree(scanPath);
			Map<String, Object> object = menuService.getRedundantMenu(appId, root);

			result = new RestResult<>(ApiCode.SUCCESS, "执行成功.", object);
		}
		else {
			result = new RestResult<>(ApiCode.FAILURE, "appId为空");
		}

		return result;
	}

}
