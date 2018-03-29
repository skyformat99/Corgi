package com.ibeiliao.deployment.admin.dao.project;

import com.ibeiliao.deployment.admin.po.project.ProjectAccountRelationPO;
import com.ibeiliao.deployment.admin.vo.project.ProjectAccountRelation;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_project_account_relation 表的操作接口
 * 
 * @author liuhaihui 2017-01-12
 */
@MyBatisDao
public interface ProjectAccountRelationDao {

	/**
	 * 保存数据
	 * 
	 * @param po 要保存的对象
	 */
	void insert(ProjectAccountRelationPO po);


	/**
	 * 修改数据，以主键更新
	 * 
	 * @param po - 要更新的数据
	 * @return 更新的行数
	 */
	int update(ProjectAccountRelationPO po);

	/**
	 * 根据主键读取记录
	 */
	ProjectAccountRelationPO get(@Param("accountId") long accountId, @Param("projectId") int projectId);

	/**
	 * 批量insert
     */
	int batchInsertRelation(@Param("relations")List<ProjectAccountRelation> relations);

	/**
	 * 根据项目id进行删除
     */
	void deleteByProjectId(@Param("projectId")int projectId);

	/**
	 * 根据projectId 获取关系
	 * @param projectId
	 * @return
     */
	List<ProjectAccountRelationPO> getByProjectId(@Param("projectId")int projectId);

	/**
	 * 根据项目ids 查找
     */
	List<ProjectAccountRelationPO> getByProjectIds(@Param("projectIds")List<Integer> projectIds);

	/**
	 * 根据账户id 和 项目id 查找
     */
	List<ProjectAccountRelationPO> getByProjectIdAndAccountId(@Param("projectId")int projectId, @Param("accountId")int accountId);

	/**
	 * 根据账户id 查找
     */
	List<ProjectAccountRelationPO> getByAccountId(@Param("accountId")long accountId);


	List<Long> getAllAdminId();
}
