package com.ibeiliao.deployment.admin.dao.global;

import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import org.apache.ibatis.annotations.Param;

import com.ibeiliao.deployment.admin.po.global.ProjectEnvPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;

import java.util.List;

/**
 * t_project_env 表的操作接口
 * 
 * @author liuhaihui 2017-01-13
 */
@MyBatisDao
public interface ProjectEnvDao {

	/**
	 * 保存数据
	 * 
	 * @param po 要保存的对象
	 */
	void insert(ProjectEnvPO po);


	/**
	 * 修改数据，以主键更新
	 * 
	 * @param po - 要更新的数据
	 * @return 更新的行数
	 */
	int update(ProjectEnvPO po);

	/**
	 * 根据主键读取记录
	 */
	ProjectEnvPO get(@Param("envId") int envId);

	/**
	 * 获取所有
     */
	List<ProjectPO> listEnv(@Param("envName") String envName,
							@Param("startIndex") int startIndex, @Param("limit") int limit);

	/**
	 * 根据查询条件查询总数量
     */
	int findTotalCount(@Param("envName") String envName);

	int delete(@Param("envId") int envId);

	/**
	 *  查找所有的环境
     */
	List<ProjectPO> findAllEnv();
}
