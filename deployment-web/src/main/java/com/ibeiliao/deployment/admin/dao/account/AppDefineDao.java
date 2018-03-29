package com.ibeiliao.deployment.admin.dao.account;

import com.ibeiliao.deployment.admin.po.account.AppDefinePO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * mz_app_define 表的操作接口
 * 
 * @author ten 2015-08-05
 */
@MyBatisDao
public interface AppDefineDao {

	/**
	 * 保存数据
	 * 
	 * @param po 对象
	 */
	 void save(AppDefinePO po);

	/**
	 * 根据主键删除数据
	 *  int deleteById(@Param("appId") int appId);
	 */

	/**
	 * 修改数据，以主键更新
	 * 
	 * @param po - 要更新的数据
	 * @return 更新的行数
	 */
	 int update(AppDefinePO po);

	/**
	 * 根据主键读取记录
	 */
	 AppDefinePO getById(@Param("appId") int appId);

	/**
	 * 查询全部记录
	 * 
	 * @return 返回所有的数据，如果没有数据，返回emtpy list
	 */
	 List<AppDefinePO> listAll();

}
