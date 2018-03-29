package com.ibeiliao.deployment.admin.controller;

import com.ibeiliao.deployment.admin.annotation.authority.AllowAnonymous;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.context.AdminLoginUser;
import com.ibeiliao.deployment.admin.context.AppConstants;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.utils.MD5Util;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.common.util.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * 欢迎界面
 * @author ten 2015/10/17.
 */
@Controller
@RequestMapping("/admin/")
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    @Autowired
    AdminAccountService adminAccountService;


    /**
     * 登录界面
     */
    @RequestMapping("login")
    @MenuResource("登录界面")
    @AllowAnonymous
    public String toLogin() {
        return "login";
    }


    /**
     * 增加管理员主页，xhtml 仅用于展示页面
     * @return
     */
    @RequestMapping("welcome.xhtml")
    @MenuResource("欢迎界面")
    @AllowAnonymous
    public String index() {
        return "redirect:/admin/deploy/list.xhtml";
    }
    
    /**
     * 登录接口,sso登录时回调
     * @param request
     * @param response
     * @param account
     * @param password
     * @return
     */
    @RequestMapping("/login.do")
    @MenuResource("授权登录")
    @AllowAnonymous
    @ResponseBody
    public RestResult login(HttpServletRequest request, HttpServletResponse response, String account, String password){

        ParameterUtil.assertNotBlank(account, "账户不能为空");
        ParameterUtil.assertNotBlank(password, "密码不能为空");

        AdminAccount adminAccount = adminAccountService.getByAccount(account);
        if (adminAccount == null){
            return new RestResult(ApiCode.FAILURE, "账号不存在");
        }

        if (!Objects.equals(MD5Util.md5(password), adminAccount.getPassword())) {
            return new RestResult(ApiCode.FAILURE, "账号密码不正确");
        }

        AdminLoginUser loginUser = new AdminLoginUser();
        loginUser.setAccountId(adminAccount.getUid());
        loginUser.setAppId(AppConstants.APP_ID_DEFAULT);

        AdminContext.saveToCookie(response, loginUser);
        logger.info("登录成功 | uid: {}", adminAccount.getUid());

        return new RestResult(ApiCode.SUCCESS, "");
    }

    /**
     * 退出登录接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/logout.do")
    @MenuResource("退出登录")
    @AllowAnonymous
    public String logout(HttpServletRequest request, HttpServletResponse response){
        AdminContext.clearCookie(response);
        return "redirect:" + AppConstants.SSO_LOGIN_URL;
    }


    @RequestMapping("/error.xhtml")
    @AllowAnonymous
    public String errorMsg(HttpServletRequest request, HttpServletResponse response){
        String message = ServletRequestUtils.getStringParameter(request, "message", "");
        try {
            request.setAttribute("message", URLDecoder.decode(message, "UTF-8"));
        }catch (Exception e){
            logger.error("错误页面发生错误 | msg:{}", message, e);
        }
        return "exception";
    }


}
