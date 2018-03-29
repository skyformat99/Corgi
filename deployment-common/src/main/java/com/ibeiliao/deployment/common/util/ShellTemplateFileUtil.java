package com.ibeiliao.deployment.common.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 详情 : 读取shell脚本模板文件
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/27
 */
public class ShellTemplateFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShellTemplateFileUtil.class);

    /**
     * java编译模板内容
     */
    private static List<String> javaCompileShellTpl;

    /**
     * 纯静态文件编译模板内容
     */
    private static List<String> staticCompileShellTpl;

    /**
     * 静态文件在java项目里面的编译模板内容
     */
    private static List<String> staticInJavaCompileShellTpl;

    /**
     * java 发布脚本模板内容
     */
    private static List<String> javaDeployShellTpl;

    /**
     * 静态文件 发布脚本模板内容
     */
    private static List<String> staticDeployShellTpl;

    /**
     * java项目中的静态文件 发布脚本模板内容
     */
    private static List<String> staticInJavaDeployShellTpl;

    /**
     * 生成的启动模板内容
     */
    private static List<String> startupShellTpl;

    static {
        try {
            String shellDir = ShellTemplateFileUtil.class.getResource("/").getPath() + "shell/";

            javaCompileShellTpl = Files.readLines(new File(shellDir + "module_compile_shell_tpl_java.sh"), Charsets.UTF_8);

            staticCompileShellTpl = Files.readLines(new File(shellDir + "module_compile_shell_tpl_static.sh"), Charsets.UTF_8);

            staticInJavaCompileShellTpl = Files.readLines(new File(shellDir + "module_compile_shell_tpl_staticInJava.sh"), Charsets.UTF_8);

            javaDeployShellTpl = Files.readLines(new File(shellDir + "module_deploy_shell_tpl_java.sh"), Charsets.UTF_8);

            staticDeployShellTpl = Files.readLines(new File(shellDir + "module_deploy_shell_tpl_static.sh"), Charsets.UTF_8);

            staticInJavaDeployShellTpl = Files.readLines(new File(shellDir + "module_deploy_shell_tpl_staticJava.sh"), Charsets.UTF_8);

            startupShellTpl = Files.readLines(new File(shellDir + "module_startup_shell_tpl.sh"), Charsets.UTF_8);

        } catch (IOException e) {
            logger.error("初始化模板文件失败, {}", e);
        }
    }

    /**
     * 获取java编译脚本模板内容
     *
     * @return 模板内容
     */
    public static String getJavaCompileShellTpl() {
        return StringUtils.join(javaCompileShellTpl, "\n");
    }

    /**
     * 获取静态文件编译脚本模板内容
     *
     * @return 模板内容
     */
    public static String getStaticCompileShellTpl() {
        return StringUtils.join(staticCompileShellTpl, "\n");
    }

    /**
     * 获取在java项目的静态文件编译脚本模板内容
     *
     * @return 模板内容
     */
    public static String getStaticInJavaCompileShellTpl() {
        return StringUtils.join(staticInJavaCompileShellTpl, "\n");
    }

    /**
     * 获取java发布脚本模板内容
     *
     * @return 模板内容
     */
    public static String getJavaDeployShellTpl() {
        return StringUtils.join(javaDeployShellTpl, "\n");
    }

    /**
     * 获取静态文建发布脚本模板内容
     *
     * @return 模板内容
     */
    public static String getStaticDeployShellTpl() {
        return StringUtils.join(staticDeployShellTpl, "\n");
    }
    /**
     * 获取java项目中静态文建发布脚本模板内容
     *
     * @return 模板内容
     */
    public static String getStaticInJavaDeployShellTpl() {
        return StringUtils.join(staticInJavaDeployShellTpl, "\n");
    }


    /**
     * 获取启动脚本模板内容
     *
     * @return 模板内容
     */
    public static String getStartupShellTpl() {
        return StringUtils.join(startupShellTpl, "\n");
    }

}
