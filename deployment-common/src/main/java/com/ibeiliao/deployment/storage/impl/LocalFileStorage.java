package com.ibeiliao.deployment.storage.impl;

import com.ibeiliao.deployment.cfg.Configuration;
import com.ibeiliao.deployment.cmd.CommandResult;
import com.ibeiliao.deployment.cmd.LocalCommand;
import com.ibeiliao.deployment.storage.FileStorageUtil;
import com.ibeiliao.deployment.storage.ProjectFileStorage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 功能：本地存储，针对编译服务器是 <strong>1台</strong> 的情况！<br/>
 * 详细：存储文件的位置见 {@link FileStorageUtil#getLocalFileStorageName(String)}
 *
 * @author linyi, 2017/5/8.
 */
public class LocalFileStorage implements ProjectFileStorage {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorage.class);

    @Override
    public boolean exists(String filename) {
        
        return false;
        // 1. 判断本地有没有文件
        /*if (!isFileExist(filename)) {
            // 2. 如果没有，scp 编译服务器 /data/storage/filename 到本地
            //doDownload(filename, new File(FileStorageUtil.getLocalFileStorageName(filename)));
        }
        // 3. 判断本地有没有文件
        return isFileExist(filename);*/
    }

    @Override
    public void download(String filename, File file) {
        // 1. 判断本地有没有文件
        if (!isFileExist(filename)) {
            // 2. 如果没有，scp 编译服务器 /data/storage/filename 到本地
            doDownload(filename, file);
        }
    }

    @Override
    public boolean save(String source, String shell, String filename) {
        // 0. 编译脚本执行 cp 到 /data/storage/文件名
        // 1. scp 到本地
        // 2. 返回
        return doDownload(filename, new File(FileStorageUtil.getLocalFileStorageName(filename)));
    }

    private boolean isFileExist(String filename) {
        File file = new File(FileStorageUtil.getLocalFileStorageName(filename));
        logger.info("检测文件是否存在：{}", file.getAbsolutePath());
        return file.exists();
    }

    private boolean doDownload(String filename, File file) {
        String destFile = FileStorageUtil.getLocalFileStorageName(filename);

        String[] scpShell = {"scp", "-P" + Configuration.getCompileServerSshPort(),
                "web@" + Configuration.getCompileServerIp() + ":" + destFile, file.getAbsolutePath()};

        LocalCommand localCommand  = new LocalCommand();
        CommandResult result = localCommand.exec(scpShell);
        logger.info("SCP命令是：" + StringUtils.join(scpShell, " "));
        logger.info("scp结果: " + result.isSuccess());
        return result.isSuccess();
    }
}
