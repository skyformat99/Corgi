package com.ibeiliao.deployment.storage;

import com.ibeiliao.deployment.storage.impl.LocalFileStorage;

/**
 * 功能：工厂类
 * 详细：
 *
 * @author linyi, 2017/2/20.
 */
public class ProjectFileStorageFactory {

    public static ProjectFileStorage getInstance() {
        return new LocalFileStorage();
    }
}
