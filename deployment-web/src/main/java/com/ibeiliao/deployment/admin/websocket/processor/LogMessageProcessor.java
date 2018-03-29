package com.ibeiliao.deployment.admin.websocket.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibeiliao.deployment.admin.context.AdminLoginUser;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.project.ProjectAccountRelationService;
import com.ibeiliao.deployment.admin.service.server.DeployLogService;
import com.ibeiliao.deployment.admin.utils.RedisKey;
import com.ibeiliao.deployment.admin.utils.SpringContextUtil;
import com.ibeiliao.deployment.admin.vo.deploy.DeployHistory;
import com.ibeiliao.deployment.admin.vo.project.Project;
import com.ibeiliao.deployment.admin.vo.server.ServerDeployLog;
import com.ibeiliao.deployment.admin.websocket.context.LogSessionHolder;
import com.ibeiliao.deployment.admin.websocket.context.UserIdentitySessionHolder;
import com.ibeiliao.deployment.admin.websocket.request.ShellLogRequestMessage;
import com.ibeiliao.deployment.admin.websocket.request.ShellLogResponseMessage;
import com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType;
import com.ibeiliao.deployment.common.util.redis.Redis;
import com.ibeiliao.deployment.base.ApiCode;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 功能：客户端请求发送日志
 * 详细：
 *
 * @author linyi, 2017/2/9.
 */
public class LogMessageProcessor extends MessageProcessor {

    @Override
    public void process(WebSocketSession webSocketSession, String message) {
        ShellLogRequestMessage request = JSONObject.parseObject(message, ShellLogRequestMessage.class);
        if (request != null && !CollectionUtils.isEmpty(request.getServerDeployIdList())) {
            if (hasPermission(webSocketSession, request)) {
                List<Integer> ids = request.getServerDeployIdList();
                logger.info("处理读取日志的消息, ids: " + JSON.toJSONString(ids));

                // 保存链接的session,  监控的服务器发布id 采用并集模式
                Set<Integer> allIds = mergeId(webSocketSession, request);

                ShellLogResponseMessage responseMessage = new ShellLogResponseMessage();
                DeployHistoryService deployHistoryService = SpringContextUtil.getBean(DeployHistoryService.class);
                Redis redis = SpringContextUtil.getBean(Redis.class);

                // 获取模块完成的发布步骤
                DeployHistory deployHistory = deployHistoryService.getByServerDeployHistoryId(ids.get(0));
                if (deployHistory != null) {
                    String stepLogKey = RedisKey.getProjectHistoryLogKey(deployHistory.getHistoryId());
                    Long stepCount = redis.llen(stepLogKey);
                    if (stepCount != null && stepCount > 0) {
                        responseMessage.setStepLogs(redis.lrange(stepLogKey, 0, stepCount));
                    }

                    // 一次性读取历史记录至客户端
                    List<ShellLogResponseMessage.ServerShellLog> shellLogList = readLogs(request);
                    responseMessage.setCode(ApiCode.SUCCESS);
                    responseMessage.setType(WebSocketRequestType.DEPLOY_SHELL_LOG.getName());
                    responseMessage.setServerLogs(shellLogList);

                    try {
                        webSocketSession.sendMessage(new TextMessage(JSONObject.toJSONString(responseMessage)));
                    } catch (IOException e) {
                        logger.error("发送消息失败", e);
                    }

                    LogSessionHolder.getInstance().save(webSocketSession, allIds);
                    logger.info("日志处理完毕 ...");
                } else {
                    logger.info("错误的ID: " + ids.get(0));
                }
            }
        }
    }

    private boolean hasPermission(WebSocketSession webSocketSession, ShellLogRequestMessage message) {

        List<Integer> ids = message.getServerDeployIdList();
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        DeployHistoryService deployHistoryService = SpringContextUtil.getBean(DeployHistoryService.class);
        List<Project> projects = deployHistoryService.getProjectByServerDeployIds(ids);
        if (CollectionUtils.isEmpty(projects)) {
            logger.error("要读取的数据不属于任何project");
        }

        if (projects.size() != 1) {
            logger.error("不能同时读取多个项目的日志, size: " + projects.size());
        }

        logger.info("读取log, ids=" + JSON.toJSONString(ids));

        AdminLoginUser adminLoginUser = UserIdentitySessionHolder.getInstance().getSessionValue(webSocketSession);

        if (adminLoginUser == null) {
            logger.error("找不到登录用户的信息, session: " + webSocketSession.getId());
            return false;
        }

        long accountId = adminLoginUser.getAccountId();
        ProjectAccountRelationService projectAccountRelationService = SpringContextUtil.getBean(ProjectAccountRelationService.class);
        for (Project p : projects) {
            if (!projectAccountRelationService.hasRelation(accountId, p.getProjectId())) {
                logger.error("用户:{} 没有项目:{} 的权限", accountId, p.getProjectId());
//                return false;
            }
        }
        return true;
    }

    private Set<Integer> mergeId(WebSocketSession webSocketSession, ShellLogRequestMessage request) {
        LogSessionHolder logSessionHolder = LogSessionHolder.getInstance();
        Set<Integer> allIds = new HashSet<>();
        if (logSessionHolder.exists(webSocketSession)) {
            Set<Integer> originalIdList = logSessionHolder.getSessionValue(webSocketSession);
            if (!CollectionUtils.isEmpty(originalIdList)) {
                allIds.addAll(originalIdList);
            }
        }
        allIds.addAll(request.getServerDeployIdList());
        return allIds;
    }

    private List<ShellLogResponseMessage.ServerShellLog> readLogs(ShellLogRequestMessage request) {
        List<ShellLogResponseMessage.ServerShellLog> shellLogList = new ArrayList<>();
        DeployLogService deployLogService = SpringContextUtil.getBean(DeployLogService.class);

        StringBuilder sb = new StringBuilder(1024);
        for (Integer id : request.getServerDeployIdList()) {
            List<ServerDeployLog> list = deployLogService.getServerDeployLog(id);
            if (!CollectionUtils.isEmpty(list)) {
                sb.setLength(0);
                ShellLogResponseMessage.ServerShellLog log = new ShellLogResponseMessage.ServerShellLog();
                log.setServerDeployId(id);

                for (ServerDeployLog temp : list) {
                    sb.append(temp.getShellLog()).append("\n");
                }
                log.setLog(sb.toString());
                shellLogList.add(log);
            }
        }
        return shellLogList;
    }
}
