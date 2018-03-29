package com.ibeiliao.deployment.admin.service.deploy;

import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.deploy.DeploymentOrder;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatProjectResult;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.enums.DeployStatus;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 发布上线单的业务接口
 *
 * @author linyi 2017/1/17
 * @version 1.0
 */
public interface DeployHistoryService {

    /**
     * 创建上线单。
     * 如果参数错误，或创建失败，抛出异常
     *
     * @param order 上线单，包括要发布的服务器信息
     */
    void createDeploymentOrder(DeploymentOrder order);

    /**
     * @param historyId
     * @param accountId
     * @param isRollBack 是否是回滚（可能是重发）
     */
    void createRollbackOrder(int historyId, long accountId, boolean isRollBack);

    /**
     * 创建重启服务的发布记录
     *
     * @param accountId 操作者
     * @param serverId  服务器id
     * @return DeployHistory
     */
    DeployHistory createRestartHistory(long accountId, int serverId);

    /**
     * 创建停止服务的发布记录
     *
     * @param accountId 操作者
     * @param serverId  服务器id
     * @return DeployHistory
     */
    DeployHistory createStopHistory(long accountId, int serverId);

    /**
     * 完成 stop 或 restart 的操作
     *
     * @param historyId
     * @param deployResult
     */
    void finishStopRestart(int historyId, DeployResult deployResult);

    /**
     * 查询发布记录，包括发布完成、发布中、审核中的记录，按创建时间倒序排列。
     * <p>
     * 返回的记录包含当前使用者有权限操作的所有项目的历史记录，
     * 使用者可以做审核、拒绝等操作。
     *
     * @param accountId 当前使用者
     * @param envId     环境，可以为0，则查询所有环境
     * @param page      第几页，最小是1
     * @param pageSize  每页数据量，范围：[1,1000]
     * @return 返回符合条件的记录，如果没有数据返回size=0的List
     */
    List<DeployHistory> queryDeployHistory(long accountId, int envId, int projectId, int moduleId, int page, int pageSize);

    /**
     * 查询发布记录，和使用者无关。
     * 返回结果不包含权限的内容。
     *
     * @param envId    环境，可以为0，则查询所有环境
     * @param moduleId 模块ID
     * @param page     第几页，最小是1
     * @param pageSize 每页数据量，范围：[1,1000]
     * @return
     */
    List<DeployHistory> queryDeployHistory(int envId, int moduleId, int page, int pageSize);

    /**
     * 读取发布记录，包括所有服务器的发布详情。
     * 但是不包括 shellLog
     *
     * @param historyId
     * @return
     */
    DeployHistory getByHistoryId(int historyId);

    /**
     * 审核项目发布：
     * 用户必须是项目的管理员或超级管理员才可以审核。
     *
     * @param accountId 操作者ID
     * @param historyId 发布记录ID
     */
    void audit(long accountId, int historyId);

    /**
     * 取消项目发布，发布记录申请者可以取消
     *
     * @param accountId 操作者ID
     * @param historyId 发布记录ID
     */
    void cancel(long accountId, int historyId);

    /**
     * 取消项目发布，只有项目负责人、超级管理员可以拒绝
     *
     * @param accountId 操作者ID
     * @param historyId 发布记录ID
     */
    void reject(long accountId, int historyId);

    /**
     * 读取发布记录，
     * 但是不包括 shellLog、服务器列表等信息
     *
     * @param serverDeployHistoryId 发布的服务器记录
     * @return
     */
    DeployHistory getByServerDeployHistoryId(int serverDeployHistoryId);

    /**
     * 开始发布
     *
     * @param historyId 发布记录ID
     * @param accountId 管理员ID
     */
    void startDeploy(int historyId, long accountId);

    /**
     * 根据 服务器发布id 获取对应的项目
     *
     * @param serverDeployIdList 服务器发布id
     * @return 返回对应的项目列表，如果没有数据，返回 empty list
     */
    List<Project> getProjectByServerDeployIds(List<Integer> serverDeployIdList);

    /**
     * 查询未完成的发布，即状态为 {@link com.ibeiliao.deployment.common.enums.DeployStatus#DEPLOYING} 的任务，
     * 结果按创建时间顺序排列。
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param size      最多返回多少条记录
     * @return
     */
    List<DeployHistory> queryUnfinished(Date startTime, Date endTime, int size);

    /**
     * 系统自动取消超时的发布记录，
     * 一般是在系统出现 bug，或某些地方发生卡死无法继续执行，才会出现超时的行为。
     *
     * @param historyId 发布记录ID
     */
    void systemCancel(int historyId);

    /**
     * 统计每个 project 在每个 env 的结果，只统计 {@link DeployStatus#DEPLOYED} 的记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间，范围 [startTime, endTime]
     * @return
     */
    List<StatProjectResult> statProject(Date startTime,
                                        Date endTime);

    /**
     * 统计低质量模块，只统计env=生产环境，并且 status={@link DeployStatus#DEPLOYED},
     * result={@link DeployResult#SUCCESS}的记录。
     * 返回 moduleId 对应的数量，最多返回100条数据，按 deployTimes 倒序
     *
     * @param startTime 开始时间
     * @param endTime   结束时间，范围 [startTime, endTime]
     * @return
     */
    List<LowQualityRank> statLowQualityModule(@Param("startTime") Date startTime,
                                              @Param("endTime") Date endTime);
}


