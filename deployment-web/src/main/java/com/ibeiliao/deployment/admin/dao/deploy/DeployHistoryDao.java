package com.ibeiliao.deployment.admin.dao.deploy;

import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatProjectResult;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import com.ibeiliao.deployment.admin.po.deploy.DeployHistoryPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * t_deploy_history 表的操作接口
 *
 * @author liuhaihui 2017-01-16
 */
@MyBatisDao
public interface DeployHistoryDao {

    /**
     * 【发布历史】页面审核
     */
    int auditDeploy(@Param("historyId") int historyId, @Param("newStatus") short newStatus,
                    @Param("oldStatus") short oldStatus,
                    @Param("accountId") long accountId, @Param("auditTime") Date auditTime);

    /**
     * 保存数据
     *
     * @param po 要保存的对象
     */
    void insert(DeployHistoryPO po);

    /**
     * 根据historyId查询记录
     */
    DeployHistoryPO get(@Param("historyId") int historyId);

    /**
     * 查询当前用户可以看的发布记录，按创建时间倒序排列
     *
     * @param accountId  用户ID
     * @param envId      环境，可以为 0，则查询所有环境
     * @param startIndex
     * @param limit
     * @return
     */
    List<DeployHistoryPO> queryByAccountId(@Param("accountId") long accountId, @Param("envId") int envId, @Param("projectId") int projectId,@Param("moduleId") int moduleId, @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 查询发布记录，按创建时间倒序排列
     *
     * @param envId      环境，可以为 0，则查询所有环境
     * @param startIndex
     * @param limit
     * @return
     */
    List<DeployHistoryPO> query(@Param("envId") int envId, @Param("projectId") int projectId, @Param("moduleId") int moduleId, @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 查找模块发布历史
     * @param envId
     * @param moduleId
     * @param startIndex
     * @param limit
     * @return
     */
    List<DeployHistoryPO> queryByModuleId(@Param("envId") int envId, @Param("moduleId") int moduleId, @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 更新状态
     *
     * @param historyId
     * @param newStatus
     * @param oldStatus
     * @return
     */
    int updateStatus(@Param("historyId") int historyId, @Param("newStatus") short newStatus,
                     @Param("oldStatus") short oldStatus);
//
//	/**
//	 * 更新发布的结果
//	 * @param historyId
//	 * @param result 结果，见 {@link DeployResult}
//	 * @param deployServers 需要发布的服务器数量
//	 * @param successCount 发布成功的服务器数量
//     */
//	int updateResult(@Param("historyId") int historyId, @Param("result") short result,
//					 @Param("deployServers") int deployServers, @Param("successCount") int successCount);

    /**
     * 更新发布的结果和状态，会更新 deploy_time
     *
     * @param historyId     发布记录ID
     * @param result        结果，见 {@link DeployResult}
     * @param deployServers 需要发布的服务器数量
     * @param successCount  发布成功的服务器数量
     * @param newStatus     新状态，见 {@link DeployStatus}
     * @param oldStatus     旧状态
     */
    int updateResultAndStatus(@Param("historyId") int historyId, @Param("result") short result,
                              @Param("deployServers") int deployServers, @Param("successCount") int successCount,
                              @Param("newStatus") short newStatus,
                              @Param("oldStatus") short oldStatus);

    /**
     * 根据创建时间 [startTime, endTime] 和状态查询记录，结果按createTime desc排列
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param status     状态
     * @param startIndex
     * @param limit
     * @return
     */
    List<DeployHistoryPO> queryByTimeAndStatus(@Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime,
                                               @Param("status") short status,
                                               @Param("startIndex") int startIndex, @Param("limit") int limit);

    /**
     * 统计每个 project 在每个 env 的结果
     *
     * @param startTime 开始时间
     * @param endTime   结束时间，范围 [startTime, endTime]
     * @param status    统计指定状态，应该等于 {@link DeployStatus#DEPLOYED}
     * @return
     */
    List<StatProjectResult> statProject(@Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime,
                                        @Param("status") short status);

    /**
     * 统计低质量模块，最多返回100条数据，按 deployTimes 倒序
     *
     * @param startTime      开始时间
     * @param endTime        结束时间，范围 [startTime, endTime]
     * @param env            环境ID列表，应该只等于生成环境 ProjectEnv.onlineFlag=TRUE 的，用英文逗号隔开
     * @param status         统计指定状态，应该等于 {@link DeployStatus#DEPLOYED}
     * @param result         结果，应该等于 {@link DeployResult#SUCCESS}
     * @param minDeployTimes 在 [startTime, endTime] 内发布次数大于等于这个数值的为质量差的
     * @return
     */
    List<LowQualityRank> statLowQualityModule(@Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime,
                                              @Param("env") String env,
                                              @Param("status") short status,
                                              @Param("result") short result,
                                              @Param("minDeployTimes") int minDeployTimes);
}
