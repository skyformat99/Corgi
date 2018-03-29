package com.ibeiliao.deployment.admin.context;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.utils.SpringContextUtil;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.common.util.HMacSHA1;
import com.ibeiliao.deployment.common.util.StaticKeyHelper;
import com.ibeiliao.deployment.common.util.WebCookie;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 后台管理员上下文信息
 * @author linyi 2015/6/6.
 */
public class AdminContext {

    private static final Logger logger = LoggerFactory.getLogger(AdminContext.class);

    private static ThreadLocal<AdminLoginUser> loginUserThreadLocal = new ThreadLocal<>();

    /** 各种ID保存在cookie里的名称 */
    public static final String COOKIE_ID_NAME = "bladmin_id";

    /** 一个签名，用于验证数据的合法性 */
    public static final String COOKIE_SIGN = "bladmin_sign";

    public static final String DOMAIN = "";

    private static final String SIGN_KEY = "@uu2t[]2oisa&*93292)(---KJEIJi3u2";

    //admin账号缓存     key为 accountId
    private static final Cache<Long, AdminAccount> adminCache = CacheBuilder.newBuilder().expireAfterAccess(1800, TimeUnit.SECONDS).maximumSize(256).build();



    /**
     * 返回已经登录的管理员ID
     * @return
     */
    public static long getAccountId() {
        AdminLoginUser u = loginUserThreadLocal.get();
        return (u == null ? 0 : u.getAccountId());
    }


    /**
     * 返回当前登录的应用系统ID
     *
     * @return
     */
    public static int getAppId() {
        AdminLoginUser u = loginUserThreadLocal.get();
        return (u == null ? 0 : u.getAppId());
    }

    /**
     * 返回管理员名称, 使用缓存
     * @return
     */
    public static String getName() {
        final AdminLoginUser u = loginUserThreadLocal.get();
        if (u == null){
            return "[NOT LOGIN]";
        } else {
            try {
                AdminAccount account =  adminCache.get(u.getAccountId(), new Callable<AdminAccount>() {
                    @Override
                    public AdminAccount call() throws Exception {
                        AdminAccount vo = SpringContextUtil.getBean(AdminAccountService.class).getById(u.getAccountId());
                        if (vo == null){
                            throw new RuntimeException("找不到用户" + u.getAccountId());
                        }
                        return vo;
                    }
                });
                return account.getRealname();
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }

        }

        return "ADMIN";
    }

    /**
     * 清除数据
     */
    public static void clear() {
        loginUserThreadLocal.remove();
    }

    /**
     * 清除cookie
     */
    public static void clearCookie(HttpServletResponse response) {
        WebCookie mc = new WebCookie(DOMAIN, true);
        mc.setCookie(response, COOKIE_ID_NAME, "", 0);
        mc.setCookie(response, COOKIE_SIGN, "", 0);
    }

    /**
     * 保存到 cookie
     * @param response Http Servlet Reponse
     * @param user - 用户信息
     */
    public static void saveToCookie(HttpServletResponse response, AdminLoginUser user) {
        String context = user.toString();
        String contextEncrypted = StaticKeyHelper.encryptKey(context);
        String sign = HMacSHA1.getSignature(context, SIGN_KEY);
        WebCookie mc = new WebCookie(DOMAIN, false);
        mc.setHttpOnly(false);
        mc.setCookie(response, COOKIE_ID_NAME, contextEncrypted);
        mc.setCookie(response, COOKIE_SIGN, sign);
        loginUserThreadLocal.set(user);
    }

    /**
     * 从 Cookie 读取管理员登录信息
     */
    public static void loadFromCookie(HttpServletRequest request) {
        WebCookie mc = new WebCookie(DOMAIN, false);
        mc.setHttpOnly(false);
        String contextEncrypted = mc.getCookie(request, COOKIE_ID_NAME);
        String sign = mc.getCookie(request, COOKIE_SIGN);
        if (StringUtils.isNotBlank(contextEncrypted) && StringUtils.isNotBlank(sign)) {
            try {
                String context = StaticKeyHelper.descryptKey(contextEncrypted);
                String tmpSign = HMacSHA1.getSignature(context, SIGN_KEY);
                if (tmpSign.equals(sign)) {
                    AdminLoginUser user = AdminLoginUser.parse(context);
                    loginUserThreadLocal.set(user);
                }
            } catch (Exception e) {
                logger.error("加载cookie错误 | contextEncrypted: "
                        + contextEncrypted + ", sign: " + sign, e);
            }
        }
    }

    /**
     * 从 cookie map 读取管理员的登录信息
     * @param cookieMap
     * @return 登录信息
     */
    public static AdminLoginUser loadFromCookieMap(Map<String,String> cookieMap){

        if(cookieMap != null && !cookieMap.isEmpty()){
            String sign = cookieMap.get(COOKIE_SIGN);
            String contextEncrypted = cookieMap.get(COOKIE_ID_NAME);
            if (StringUtils.isNotBlank(contextEncrypted) && StringUtils.isNotBlank(sign)) {
                try {
                    String context = StaticKeyHelper.descryptKey(contextEncrypted);
                    String tmpSign = HMacSHA1.getSignature(context, SIGN_KEY);
                    if (tmpSign.equals(sign)) {
                        AdminLoginUser user = AdminLoginUser.parse(context);
                        return user;
                    }
                } catch (Exception e) {
                    logger.error("加载cookie错误 | contextEncrypted: "
                            + contextEncrypted + ", sign: " + sign, e);
                }
            }

        }
        return null;
    }


}
