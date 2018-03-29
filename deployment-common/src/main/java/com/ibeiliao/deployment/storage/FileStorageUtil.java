package com.ibeiliao.deployment.storage;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.common.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 功能：项目文件保存工具类
 * 详细：
 *
 * @author linyi, 2017/2/20.
 */
public class FileStorageUtil {

    public static final String EXT = ".tar.gz";

    /**
     * 返回要保存的文件名。
     * 对于同一个模块的同一个 tag，返回的文件名永远一样
     * @param projectId 项目ID
     * @param moduleId  模块ID
     * @param tags      发布哪个分支
     * @param envName   环境名，比如 dev/test
     * @param version   版本号
     * @return
     */
    public static String getSaveFileName(int projectId, int moduleId, String tags, String envName, String version) {
        String filename = null;
        if (StringUtils.isEmpty(version)) {
            version = System.currentTimeMillis() + "";
        }
        /*
         * 说明：
         * （1）不可以根据 projectId，因为保存的是 module 的 jar/war；
         * （2）必须带上环境变量，因为不同的环境，profile 不一样；
         */
        filename = moduleId + "_" + EncryptUtil.getMD5(moduleId + "|" + envName + "|" + tags + "|" + version);
        return  filename + EXT;
    }

    public static String getLocalFileStorageName(String filename) {
        return Configuration.getCompileStorageDir() + "/" + filename;
    }
}
