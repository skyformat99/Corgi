package com.ibeiliao.deployment.admin.service.account.impl;

import com.ibeiliao.deployment.admin.dao.account.AppDefineDao;
import com.ibeiliao.deployment.admin.po.account.AppDefinePO;
import com.ibeiliao.deployment.admin.service.account.AppDefineService;
import com.ibeiliao.deployment.admin.vo.account.AppDefine;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AppDefineService 的实现类
 * 
 * @author ten 2015-08-05
 */
@Service
public class AppDefineServiceImpl implements AppDefineService {

	@Autowired
	private AppDefineDao dao;

	@Override
	public void save(AppDefine dto) {
		AppDefinePO po = dto2po(dto);
		dao.save(po);
	}

	@Override
	public int update(AppDefine dto) {
		AppDefinePO po = dto2po(dto);
		return dao.update(po);
	}

	@Override
	public AppDefine getById(int appId) {
		AppDefinePO po = dao.getById(appId);
		if (null == po)
			return null;
		return po2dto(po);
	}

	@Override
	public List<AppDefine> listAll() {
		List<AppDefinePO> list = dao.listAll();
		List<AppDefine> result = new ArrayList<AppDefine>();
		if (null != list)
			for (AppDefinePO po : list)
				result.add(po2dto(po));
		return result;
	}

	// ####
	// ## private func
	// ####

	public AppDefine po2dto(AppDefinePO po) {
		return VOUtil.from(po, AppDefine.class);
	}

	public AppDefinePO dto2po(AppDefine dto) {
		return VOUtil.from(dto, AppDefinePO.class);
	}

}
