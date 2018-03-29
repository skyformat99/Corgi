package com.ibeiliao.deployment.admin.service.account;

import com.ibeiliao.deployment.admin.vo.account.AppDefine;

import java.util.List;

/**
 * 应用系统信息 <br>
 * 的操作接口
 * 
 * @author ten 2015-08-05
 */
public interface AppDefineService {

	/**
	 * 保存数据
	 * 
	 * @param dto
	 *            对象
	 */
	public void save(AppDefine dto);

	/**
	 * 修改数据，以主键更新
	 * 
	 * @param dto
	 *            - 要更新的数据
	 * @return 更新的行数
	 */
	public int update(AppDefine dto);

	/**
	 * 根据主键读取记录
	 */
	public AppDefine getById(int appId);

	/**
	 * 查询全部记录
	 * 
	 * @return 返回所有的数据，如果没有数据，返回emtpy list
	 */
	public List<AppDefine> listAll();

}
