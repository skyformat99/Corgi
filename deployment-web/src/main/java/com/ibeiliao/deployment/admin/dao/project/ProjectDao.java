package com.ibeiliao.deployment.admin.dao.project;

import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_project 表的操作接口
 *
 * @author liuhaihui 2017-01-12
 */
@MyBatisDao
public interface ProjectDao {

    /**
     * 保存数据
     *
     * @param po 要保存的对象
     */
    int insert(ProjectPO po);


    /**
     * 修改数据，以主键更新
     *
     * @param po - 要更新的数据
     * @return 更新的行数
     */
    int update(ProjectPO po);

    /**
     * 根据主键读取记录
     */
    ProjectPO get(@Param("projectId") int projectId);

    /**
     * @return
     */
    List<ProjectPO> queryProject(@Param("projectName") String projectName,
                                 @Param("programLanguage") String projectLanguage,
                                 @Param("managerId") Long projectManagerId,
                                 @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 查找负责人的所有项目（超级管理员视角）
     * @return
     */
    List<ProjectPO> queryProjectWithManager(@Param("projectName") String projectName,
                                 @Param("programLanguage") String projectLanguage,
                                 @Param("managerId") Long projectManagerId,
                                 @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 查询管理员能发布的所有项目列表
     *
     * @param accountId   管理员ID，必传
     * @param projectName 要查询的项目，可选
     * @param startIndex
     * @param limit       最多返回多少数据
     * @return 返回可以访问的项目列表，如果没有数据，返回size=0的List
     * @author linyi 2017/1/20
     */
    List<ProjectPO> queryAdminProjects(@Param("accountId") long accountId,
                                       @Param("projectName") String projectName,
                                       @Param("programLanguage") String projectLanguage,
                                       @Param("managerId") Long projectManagerId,
                                       @Param("startIndex") int startIndex,
                                       @Param("limit") int limit);

    /**
     * 根据项目代号查询项目信息
     *
     * @param projectNo
     * @return
     */
    ProjectPO getByProjectNo(@Param("projectNo") String projectNo);

    /**
     * 根据projectIds 查询
     */
    List<ProjectPO> getByProjectIds(@Param("projectIds") List<Integer> projectIds);

    /**
     * 获取所有的负责人id
     */
    List<Long> getAllManagerIds();

    /**
     * 根据用户id查找参与的所有项目
     * @param accountId
     * @return
     */
    List<ProjectPO> queryByAccountId(@Param("accountId") long accountId);
}
