package com.ibeiliao.deployment.admin.service.account.impl;

import com.ibeiliao.deployment.admin.annotation.authority.Authorizable;
import com.ibeiliao.deployment.admin.context.AppConstants;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.admin.vo.account.UrlResource;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.service.account.MenuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 权限验证 service
 * 
 * @author ten 2015/10/19.
 */
@Service
public class AuthorizationServiceImpl implements Authorizable {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

	@Autowired
	private AdminAccountService adminAccountService;

	@Autowired
	private MenuService menuService;

	@Override
	public boolean hasAuthorization(long accountId, String uri) {
		logger.info("权限判断 | accountId: {}, uri: {}", accountId, uri);
		// 注：javax.ws.rs.Path 里可能有正则表达式
		AdminAccount adminAccount = adminAccountService.getById(accountId);
		boolean pass = true;
		if (adminAccount == null) {
			logger.error("找不到管理员 | accountId: {}", accountId);
			pass = false;
		} else if (adminAccountService.isSuperAdmin(accountId)) {
			logger.info("超级管理员 | accountId: {}", accountId);
		} else {
			pass = matchUri(accountId, AppConstants.APP_ID_DEFAULT, uri);
		}
		logger.info("权限判断结果 | accountId: {}, uri:{}, pass: {}", accountId, uri, pass);
		return pass;
	}

	/**
	 * 返回所有的用户有权限的链接列表
	 * 
	 * @param accountId
	 * @param appId
	 * @param regxSet
	 *            使用了正则匹配的url
	 * @return
	 */
	private Set<String> getUrlResourceSet(long accountId, int appId, Set<String> regxSet) {
		List<UrlResource> list = menuService.listMenuResourceByAccount(accountId, appId);
		Set<String> set = new HashSet<>(list.size() * 4 / 3);
		for (UrlResource urlResource : list) {
			set.add(urlResource.getUri());
			if (urlResource.getUri().indexOf("{") >= 0) {
				regxSet.add(urlResource.getUri());
			}
		}
		return set;
	}

	/**
	 * 判断是否有权限访问 uri
	 * 
	 * @param accountId
	 * @param appId
	 * @param uri
	 * @return
	 */
	private boolean matchUri(long accountId, int appId, String uri) {
		Set<String> regxSet = new HashSet<>();
		Set<String> urlSet = getUrlResourceSet(accountId, appId, regxSet);
		if (urlSet.contains(uri)) {
			return true;
		}
		if (regxSet.size() > 0) {
			// 判断正则
			for (String s : regxSet) {
				if (Pattern.matches(s, uri)) {
					return true;
				}
			}
		}
		return false;
	}
}
