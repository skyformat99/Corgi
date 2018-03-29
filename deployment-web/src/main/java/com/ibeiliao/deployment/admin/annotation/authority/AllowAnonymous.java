package com.ibeiliao.deployment.admin.annotation.authority;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个 URL 允许匿名访问，不进行权限验证。
 * 和 {@link org.springframework.web.bind.annotation.RequestMapping},
 * {@link com.ibeiliao.deployment.admin.utils.resource.MenuResource} 一起使用
 * @author ten 2015/10/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AllowAnonymous {
}
