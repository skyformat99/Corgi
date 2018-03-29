package com.ibeiliao.deployment.admin.service.account.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibeiliao.deployment.admin.annotation.log.AdminLogger;
import com.ibeiliao.deployment.admin.dao.account.AdminOperationLogDao;
import com.ibeiliao.deployment.admin.vo.account.AdminOperationLog;
import com.ibeiliao.deployment.admin.po.account.AdminOperationLogPO;
import com.ibeiliao.deployment.admin.service.account.AdminOperationLogService;
import com.ibeiliao.deployment.common.util.ParameterUtil;
import com.ibeiliao.deployment.common.util.VOUtil;

/**
 * AdminOperationLogService 的实现类
 * 
 * @author ten 2015-08-05
 */
@Service
public class AdminOperationLogServiceImpl implements AdminOperationLogService, AdminLogger {

	@Autowired
	private AdminOperationLogDao dao;

	@Override
	public void save(AdminOperationLog dto) {
		ParameterUtil.assertNotNull(dto, "参数不能为null");
		ParameterUtil.assertNotBlank(dto.getOperationType(), "操作类型不能为空");
		// ParameterUtil.assertNotBlank(dto.getArguments(), "参数不能为空");
		// 参数 dto.arguments 是可以为空的
		ParameterUtil.assertNotNull(dto.getResult(), "返回值不能为null");
		AdminOperationLogPO po = dto2po(dto);

		po.setCreateTime(new Date());
		if (po.getExt1() == null)
			po.setExt1("");
		if (po.getExt2() == null)
			po.setExt2("");
		if (po.getIp() == null) {
			po.setIp("未知IP");
		}
		if (po.getArguments() == null) {
			po.setArguments("");
		}
		if (po.getArguments().length() > 512) {
			po.setArguments(po.getArguments().substring(0, 512));
		}
		if (po.getResult().length() > 255) {
			po.setResult(po.getResult().substring(0, 255));
		}
		if (po.getOperationType().length() > 30) {
			po.setOperationType(po.getOperationType().substring(0, 30));
		}
		dao.save(po);
	}

	@Override
	public void saveAdminLog(AdminOperationLog log) {
		save(log);
	}

	// ####
	// ## private func
	// ####

	public AdminOperationLog po2dto(AdminOperationLogPO po) {
		return VOUtil.from(po, AdminOperationLog.class);
	}

	public AdminOperationLogPO dto2po(AdminOperationLog dto) {
		return VOUtil.from(dto, AdminOperationLogPO.class);
	}

}
