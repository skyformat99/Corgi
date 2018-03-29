package com.ibeiliao.deployment.admin.dao.deploy;

import com.ibeiliao.deployment.admin.po.deploy.ServerDeployLogPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_server_deploy_log 表的操作接口
 *
 * @author jingyesi 2017-01-25
 */
@MyBatisDao
public interface ServerDeployLogDao {

    /**
     * 保存数据
     *
     * @param po 要保存的对象
     */
    void insert(ServerDeployLogPO po);


    /**
     * 修改数据，以主键更新
     *
     * @param po - 要更新的数据
     * @return 更新的行数
     */
    int update(ServerDeployLogPO po);

    /**
     * 根据主键读取记录
     */
    ServerDeployLogPO get(@Param("logId") int logId);

    /**
     * 批量插入日志
     *
     * @param list 入职列表
     * @return 返回插入行数
     */
    int batchInsert(@Param("list") List<ServerDeployLogPO> list);

    /**
     * 根据 serverDeployId 读取服务器的发布日志记录，按发布时间顺序排列
     * @param serverDeployId
     * @return
     */
    List<ServerDeployLogPO> getByServerDeployId(@Param("serverDeployId") int serverDeployId);

    /**
     * 根据serverDeployIds 获取对应的日志
     */
    List<ServerDeployLogPO> getByServerDeployIds(@Param("serverDeployIds") List<Integer> serverDeployIds);
}
