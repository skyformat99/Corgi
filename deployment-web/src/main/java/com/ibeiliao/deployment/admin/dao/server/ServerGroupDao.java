package com.ibeiliao.deployment.admin.dao.server;

import com.ibeiliao.deployment.admin.po.server.ServerGroupPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_server_group 表的操作接口
 * 
 * @author liuhaihui 2017-01-16
 */
@MyBatisDao
public interface ServerGroupDao {

	

	/**
	 * 保存数据
	 * @param po 要保存的对象
	 */
	void insert(ServerGroupPO po);


	/**
	 * 修改数据，以主键更新
	 * @param group - 要更新的数据
	 * @return 更新的行数
	 */
	int update(@Param("group") ServerGroupPO group);


	/**
	 * 根据groupId查询记录
	 */
	ServerGroupPO get(@Param("groupId") int groupId);

	/**
	 * 批量写入
     */
	void batchInsert(List<ServerGroupPO> list);


	List<ServerGroupPO> getByModuleId(@Param("moduleId") int moduleId);

	/**
	 * 根据多个moduleId查找
     */
	List<ServerGroupPO> getByModuleIds(@Param("moduleIds") List<Integer> moduleIds);

	List<ServerGroupPO> getByModuleAndEnv(@Param("moduleId") int moduleId, @Param("envId") int envId);

	/**
	 * 根据moduleId 删除
     */
	void deleteByModuleId(@Param("moduleId")int moduleId);

	void deleteByModuleIdAndOldGroupIds(@Param("groupIds") List<Integer> groupIds, @Param("moduleId")int moduleId);
}
