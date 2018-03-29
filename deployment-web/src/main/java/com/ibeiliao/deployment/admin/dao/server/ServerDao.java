package com.ibeiliao.deployment.admin.dao.server;

import com.ibeiliao.deployment.admin.po.server.ServerPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_server 表的操作接口
 * 
 * @author liangguanglong 2017-01-18
 */
@MyBatisDao
public interface ServerDao {

	/**
	 * 保存数据
	 * @param po 要保存的对象
	 */
	void insert(ServerPO po);

	/**
	 * 修改数据，以主键更新
	 * @param server - 要更新的数据
	 * @return 更新的行数
	 */
	int update(@Param("server") ServerPO server);

	/**
	 * 根据serverId查询记录
	 */
	ServerPO get(@Param("serverId") int serverId);

	/**
	 * 根据serverId列表批量查询记录
	 */
	List<ServerPO> getByIds(@Param("serverIds") int ... serverIds);

	/**
	 * 批量insert
     */
	void batchInsert(@Param("servers")List<ServerPO> serverPOs);

	/**
	 * 根据所属的服务器组和服务器id 来删除被客户端手动删除的服务器
	 * 细节: server_id not in (服务器id) and group_id in (服务器组id)
     */
	void deleteByServerGroupIdsAndServerIds(@Param("groupIds") List<Integer> groupIds, @Param("serverIds") List<Integer> serverIds);

	List<ServerPO> getByGroupIds(@Param("groupIds")List<Integer> groupIds);

	/**
	 * 根据所属的服务器组id 来删除服务器
     */
	void deleteByServerGroupIds(@Param("groupIds") List<Integer> groupIds);

}
