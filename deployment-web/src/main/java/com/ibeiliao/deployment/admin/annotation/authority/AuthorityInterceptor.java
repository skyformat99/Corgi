package com.ibeiliao.deployment.admin.annotation.authority;

import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.context.AppConstants;
import com.ibeiliao.deployment.admin.utils.SpringContextUtil;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.common.util.IpAddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * 管理员权限拦截器
 * @author ten 2015/12/28.
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorityInterceptor.class);

    private Authorizable authorizable = null;

    private static final String DENIED_MESSAGE = "{\"code\":" + ApiCode.UNAUTHORIZED_ACCESS + ",\"message\":\"您没有权限访问\"}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = getURI(request);
        String ip = IpAddressUtils.getClientIpAddr(request);
        logger.info("权限拦截器 start | uri: {}, ip: {}", uri, ip);

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod method = (HandlerMethod) handler;
            // 对只有 MenuResource 注解的方法做处理
            RequestMapping requestMapping = method.getMethodAnnotation(RequestMapping.class);
            if (method.getMethodAnnotation(MenuResource.class) != null
                    && requestMapping != null) {
                long accountId = AdminContext.getAccountId();
                AllowAnonymous allowAnonymous = method.getMethodAnnotation(AllowAnonymous.class);
                // 没有登录
                if (accountId == 0 && allowAnonymous == null){
                    response.sendRedirect(AppConstants.SSO_LOGIN_URL);
                    return false;
                }

                if (allowAnonymous == null) {
                    boolean pass = hasAuthorizable(AdminContext.getAccountId(), uri);
                    // 处理没有权限时的返回值
                     if (pass == false) {
                         // 没有权限
                         logger.error("没有权限访问 | uri: {}", uri);
                         if (method.getMethodAnnotation(ResponseBody.class) == null) {
                             // 这是一个页面
                             response.setCharacterEncoding("UTF-8");
                             response.sendRedirect("/admin/exception.xhtml?message=" + URLEncoder.encode("没有权限访问", "UTF-8"));
                         }
                         else {
                             response.setHeader("Content-Type", "application/json; charset=UTF-8");
                             response.getWriter().println(DENIED_MESSAGE);
                         }
                         return false;
                     }

                }
            }
        }
        logger.info("权限拦截器 end");
        return true;
    }

    protected Authorizable getAuthorizable() {
        if (authorizable == null) {
            authorizable = SpringContextUtil.getBean(Authorizable.class);
        }
        return authorizable;
    }

    /**
     * 判断是否有权限，如果没有任何类实现 Authorizable，默认是通过权限判断
     * @param accountId 管理员ID
     * @param uri url地址
     * @return true=有权限
     */
    private boolean hasAuthorizable(long accountId, String uri) {
        // 权限检查
        return getAuthorizable().hasAuthorization(accountId, uri);
    }

    /**
     * 获取系统资源地址
     *
     * @param request
     */
    private String getURI(HttpServletRequest request) {
        UrlPathHelper helper = new UrlPathHelper();
        String uri = helper.getOriginatingRequestUri(request);
        String ctxPath = helper.getOriginatingContextPath(request);

        return uri.replaceFirst(ctxPath, "");
    }
}
