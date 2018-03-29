package com.ibeiliao.deployment.common.util;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cfg.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：模块工具类
 * 详细：
 *
 * @author linyi, 2017/2/16.
 */
public class ModuleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ModuleUtil.class);

    /**
     * 重启脚本填写 Main 类的 class name 的正则表达式。
     * 实际上 class name 可以用 unicode 符号，比如中文，但我们限制为英文、数字、下划线、$等
     */
    private static final String MAIN_CLASS_PATTERN = "([A-Za-z0-9_$]{1,40}\\.?)+";

    /**
     * 获得缩短后的模块名.
     * 比如模块名 可能是 /services/xxx-impl，截取 xxx-impl 返回
     *
     * @param moduleName 模块名称
     * @return
     */
    public static String getShortModuleName(String moduleName) {
        int pos = moduleName.lastIndexOf("/");
        if (pos >= 0) {
            return moduleName.substring(pos + 1);
        }
        return moduleName;
    }

    public static boolean isMainClass(String restartShell) {
        return (restartShell != null && restartShell.trim().matches(MAIN_CLASS_PATTERN));
    }

    public static String getProjectDir(String projectNo) {
        return Configuration.getAnsibleUploadFileDir() + projectNo + "/";
    }

    /**
     * 返回模块在业务服务器上的发布目录
     *
     * @param projectNo       项目代号
     * @param shortModuleName 模块名
     * @return
     */
    public static String getModuleDir(String projectNo, String shortModuleName) {
        return Configuration.getAnsibleUploadFileDir() + projectNo + "/" + shortModuleName + "/";
    }

    /**
     * 返回模块在业务服务器上的配置文件所在目录
     *
     * @param projectNo       项目代号
     * @param shortModuleName 模块名
     * @return
     */
    public static String getModuleConfDir(String projectNo, String shortModuleName) {
        return Configuration.getAnsibleUploadConfDir() + projectNo + "/" + shortModuleName + "/";
    }

    /**
     * GC log文件
     *
     * @param projectNo       项目代号
     * @param shortModuleName 模块名
     * @return
     */
    public static String getGcLogFile(String projectNo, String shortModuleName) {
        return Configuration.getGcLogDir() + projectNo + "/" + shortModuleName + "/gc.log";
    }

    /**
     * resin access log 文件路径
     *
     * @param projectNo       项目代号
     * @param shortModuleName 模块名
     * @return
     */
    public static String getAccessLogFile(String projectNo, String shortModuleName) {
        return Configuration.getAccessLogDir() + projectNo + "/" + shortModuleName + "/access.log";
    }

    /**
     * 把域名替换成指定环境的域名
     *
     * @param domain 域名
     * @param env    环境，比如dev/test
     * @return
     */
    public static String getDomainForEnv(String domain, String env) {
        String domainRegx = Configuration.getDomainRegx();
        String domainStyle = Configuration.getDomainStyle(env);
        if (StringUtils.isBlank(domainRegx) || StringUtils.isBlank(domainStyle)) {
            logger.warn("域名配置有误, env: {}, domainRegx: {}, domainStyle: {}", env, domainRegx, domainStyle);
            return domain;
        }
        Pattern pattern = Pattern.compile(domainRegx);
        Matcher matcher = pattern.matcher(domain);
        if (matcher.find()) {
            String name = matcher.group(1);
            String newName = domainStyle.replace("${name}", name).replace("${env}", env);
            return newName + domain.substring(name.length());
        }
        return domain;
    }

    /**
     * 获取解析得到制定环境的别名域名
     * @param aliasDomain
     * @param env
     * @return
     */
    public static String getAliasDomainForEnv(String aliasDomain, String env) {
        if (StringUtils.isBlank(aliasDomain)) {
            return "";
        }
        String[] domains = aliasDomain.split("\\s{1,}");
        ArrayList<Object> domainList = Lists.newArrayList();
        for (String domain : domains) {
            domainList.add(getDomainForEnv(domain, env));
        }
        return StringUtils.join(domainList, " ");
    }
}
