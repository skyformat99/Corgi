package com.ibeiliao.deployment.admin.dao.deploy;

import com.ibeiliao.deployment.admin.po.deploy.ServerDeployHistoryPO;
import com.ibeiliao.deployment.common.enums.ServerDeployResult;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * t_server_deploy_history 表的操作接口
 * 
 * @author linyi 2017-01-17
 */
@MyBatisDao
public interface ServerDeployHistoryDao {


	/**
	 * 保存数据
	 * @param po 要保存的对象
	 */
	void insert(ServerDeployHistoryPO po);


	/**
	 * 根据id查询记录
	 */
	ServerDeployHistoryPO get(@Param("id") int id);

	/**
	 * 根据historyId查询记录
	 */
	List<ServerDeployHistoryPO> getByHistoryId(@Param("historyId") int historyId);

	/**
	 * 更新状态
	 * @param id
	 * @param status      见 {@link ServerDeployResult}
	 * @param startupTime 服务操作时间
	 * @return
	 */
	int updateStatus(@Param("id") int id, @Param("status") short status, @Param("startupTime") Date startupTime);

	/**
	 * 更新发布记录的所有服务器状态
	 * @param historyId   发布记录ID
	 * @param status      状态
	 * @param startupTime 服务操作时间
	 * @return
	 */
	int updateAllStatus(@Param("historyId") int historyId, @Param("status") short status, @Param("startupTime") Date startupTime);

	/**
	 * 根据historyId 和服务器ip查询记录
	 */
	List<ServerDeployHistoryPO> getByHistoryIdsAndIps(@Param("historyIds") List<Integer> historyIds, @Param("serverIps")List<String> serverIps);

}
