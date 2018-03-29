package com.ibeiliao.deployment.admin.service.global.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.ibeiliao.deployment.admin.dao.global.GlobalSettingDao;
import com.ibeiliao.deployment.admin.vo.global.GlobalSetting;
import com.ibeiliao.deployment.admin.po.global.GlobalSettingPO;
import com.ibeiliao.deployment.admin.service.global.GlobalSettingService;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 详情 :  GlobalSettingService 实现类
 *
 * @author liangguanglong
 */
@Service
public class GlobalSettingServiceImpl implements GlobalSettingService {

    @Autowired
    private GlobalSettingDao globalSettingDao;

    @Override
    public GlobalSetting getGlobalSetting() {
        List<GlobalSettingPO> globalSettingPOs = globalSettingDao.findAll();
        if (CollectionUtils.isEmpty(globalSettingPOs)) {
            return new GlobalSetting();
        }
        return VOUtil.from(globalSettingPOs.get(0), GlobalSetting.class);
    }

    @Override
    public void saveGlobalSetting(GlobalSettingPO setting) {
        globalSettingDao.insert(setting);
    }

    @Override
    public int updateGlobalSetting(GlobalSettingPO setting) {
        return globalSettingDao.update(setting);
    }
}
