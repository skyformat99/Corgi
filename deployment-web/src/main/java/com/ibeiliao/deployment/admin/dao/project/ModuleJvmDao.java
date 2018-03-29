package com.ibeiliao.deployment.admin.dao.project;

import com.ibeiliao.deployment.admin.po.project.ModuleJvmPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * t_module_jvm 表的操作接口
 * 
 * @author liangguanglong 2017-02-24
 */
@MyBatisDao
public interface ModuleJvmDao {

	/**
	 * 保存数据
	 * 
	 * @param po 要保存的对象
	 */
	void insert(ModuleJvmPO po);


	/**
	 * 修改数据，以主键更新
	 * 
	 * @param po - 要更新的数据
	 * @return 更新的行数
	 */
	int update(ModuleJvmPO po);

	/**
	 * 根据主键读取记录
	 */
	ModuleJvmPO get(@Param("moduleJvmId") int moduleJvmId);

	/**
	 * 批量写入
     */
	int batchInsert(@Param("moduleJvmPOs")List<ModuleJvmPO> moduleJvmPOs);

	/**
	 *  根据moduleid 查询
     */
	List<ModuleJvmPO> getByModuleId(@Param("moduleId")int moduleId);


	void deleteByModuleId(@Param("moduleId")int moduleId);

	/**
	 *  根据moduleids 查询
     */
	List<ModuleJvmPO> getByModuleIds(@Param("moduleIds")List<Integer> moduleIds);
}
