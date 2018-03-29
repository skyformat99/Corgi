package com.ibeiliao.deployment.admin.service.project.impl;

import com.ibeiliao.deployment.admin.dao.project.ModuleJvmDao;
import com.ibeiliao.deployment.admin.po.project.ModuleJvmPO;
import com.ibeiliao.deployment.admin.service.project.ModuleJvmService;
import com.ibeiliao.deployment.admin.vo.project.ModuleJvm;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 详情 :
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/24
 */
@Service
public class ModuleJvmServiceImpl implements ModuleJvmService {

    @Autowired
    private ModuleJvmDao moduleJvmDao;

    @Override
    public void saveModuleJvm(List<ModuleJvm> moduleJvmList) {
        if (CollectionUtils.isEmpty(moduleJvmList)) {
            return;
        }
        List<ModuleJvmPO> moduleJvmPOs = VOUtil.fromList(moduleJvmList, ModuleJvmPO.class);
        moduleJvmDao.batchInsert(moduleJvmPOs);
    }

    @Override
    public void updateModuleJvm(List<ModuleJvm> moduleJvmList) {
        if (CollectionUtils.isEmpty(moduleJvmList)) {
            return;
        }
        List<ModuleJvmPO> moduleJvmPOs = VOUtil.fromList(moduleJvmList, ModuleJvmPO.class);
        for (ModuleJvmPO po : moduleJvmPOs) {
            moduleJvmDao.update(po);
        }
    }

    @Override
    public List<ModuleJvm> queryByModuleId(int moduleId) {
        if (moduleId <= 0) {
            return Collections.emptyList();
        }
        List<ModuleJvmPO> moduleJvmPOs = moduleJvmDao.getByModuleId(moduleId);
        if (CollectionUtils.isEmpty(moduleJvmPOs)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(moduleJvmPOs, ModuleJvm.class);
    }

    @Override
    public void deleteByModuleId(int moduleId) {
        moduleJvmDao.deleteByModuleId(moduleId);
    }

    @Override
    public List<ModuleJvm> queryByModuleIds(List<Integer> moduleIds) {
        if (CollectionUtils.isEmpty(moduleIds)) {
            return Collections.emptyList();
        }
        List<ModuleJvmPO> moduleJvmPOs = moduleJvmDao.getByModuleIds(moduleIds);
        if (CollectionUtils.isEmpty(moduleJvmPOs)) {
            return Collections.emptyList();
        }
        return VOUtil.fromList(moduleJvmPOs, ModuleJvm.class);
    }
}
