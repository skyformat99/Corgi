package com.ibeiliao.deployment.storage;

import java.io.File;

/**
 * 功能：项目文件存储
 * 详细：
 *
 * @author linyi, 2017/2/20.
 */
public interface ProjectFileStorage {

    /**
     * 判断文件是否存在
     * @param filename 文件名，唯一
     * @return
     */
    boolean exists(String filename);

    /**
     * 下载文件到本地
     * @param filename 文件名，唯一
     * @param file     保存到本地哪个文件
     */
    void download(String filename, File file);

    /**
     * 保存到存储系统
     * @param source    本地的源文件
     * @param shell     执行保存的 shell 脚本名称，绝对路径
     * @param filename  要保存的文件名
     * @return true=成功
     */
    boolean save(String source, String shell, String filename);
}
