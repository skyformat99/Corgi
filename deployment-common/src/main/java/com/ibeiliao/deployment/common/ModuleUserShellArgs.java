package com.ibeiliao.deployment.common;

/**
 * 详情 : 模块脚本填写 给用户提供的参数
 * <p>
 * 详细 : 包括编译和启动
 *
 * @author liangguanglong 17/3/20
 */
public interface ModuleUserShellArgs {

    // 提供给用户 调用的模块目录参数(用于复制该目录的target下的 jar/war 到一个文件夹中)
    String MODULE_DIR = "moduleDir";

    // 提供给用户 调用的复制文件的目的目录参数
    String TARGET_DIR = "targetDir";

    // 提供给用户 调用的复制文件的目的目录参数
    String ENV = "env";

    // 提供给用户 在编写resin启动脚本 对应 -conf 参数的值
    String CONF = "conf";
}
