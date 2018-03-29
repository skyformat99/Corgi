package com.ibeiliao.deployment.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie 操作，不涉及任何加密的内容
 * @author ten 2015/11/16.
 */
public class WebCookie {

    private static final Logger logger = LoggerFactory.getLogger(WebCookie.class);

    /**
     * cookie存放的域名
     */
    private String domain;

    private boolean secure = false;

    private boolean httpOnly = false;

    /**
     * 构造函数
     * @param domain 域名，比如 .ibeiliao.com，.admin.ibeiliao.com
     * @param secure 是否只能使用 HTTPS
     */
    public WebCookie(String domain, boolean secure) {
        /*if (StringUtils.isBlank(domain) ) {
            throw new IllegalArgumentException("'domain' cannot be empty.");
        }*/
        this.domain = domain;
        this.secure = secure;
    }

    /**
     * 设置cookie，加密处理，域名为：.ibeiliao.com
     * @param response - HttpServletResponse
     * @param name
     *            - cookie名字
     * @param value
     *            - cookie值
     */
    public void setCookie(HttpServletResponse response,String name, String value) {
        setCookie(response,name, value, -1);
    }

    /**
     * 设置 cookie
     *
     * @param name
     *            - 名称
     * @param value
     *            - 值
     * @param maxAge
     *            - 有效时间
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value == null ? "" : value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        //cookie.setDomain(domain);
        cookie.setHttpOnly(httpOnly); // 是否禁止js读取cookie
        cookie.setSecure(secure); // 如果为true，https下才传输cookie，解决cookie劫持的问题
        response.addCookie(cookie);
    }

    /**
     * 读取cookie的值，自动解密
     *
     * @param name
     *            - cookie名字
     * @return - cookie值，不存在时为null
     */
    public String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || name == null || name.length() == 0) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (name.equals(cookies[i].getName())) {
                String value = cookies[i].getValue();

                return value;
            }
        }
        return null;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
