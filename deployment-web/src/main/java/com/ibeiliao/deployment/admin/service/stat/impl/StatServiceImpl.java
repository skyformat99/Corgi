package com.ibeiliao.deployment.admin.service.stat.impl;

import com.ibeiliao.deployment.admin.dao.stat.LowQualityRankDao;
import com.ibeiliao.deployment.admin.dao.stat.StatAllDao;
import com.ibeiliao.deployment.admin.dao.stat.StatProjectDao;
import com.ibeiliao.deployment.admin.po.stat.LowQualityRankPO;
import com.ibeiliao.deployment.admin.po.stat.StatAllPO;
import com.ibeiliao.deployment.admin.po.stat.StatProjectPO;
import com.ibeiliao.deployment.admin.service.deploy.DeployHistoryService;
import com.ibeiliao.deployment.admin.service.stat.StatService;
import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatAll;
import com.ibeiliao.deployment.admin.vo.stat.StatProject;
import com.ibeiliao.deployment.admin.vo.stat.StatProjectResult;
import com.ibeiliao.deployment.common.enums.DeployResult;
import com.ibeiliao.deployment.common.util.DateUtil;
import com.ibeiliao.deployment.common.util.VOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能：StatService 的实现
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@Service
public class StatServiceImpl implements StatService {

    private static final Logger logger = LoggerFactory.getLogger(StatServiceImpl.class);

    @Autowired
    private DeployHistoryService deployHistoryService;

    @Autowired
    private StatProjectDao statProjectDao;

    @Autowired
    private StatAllDao statAllDao;

    @Autowired
    private LowQualityRankDao lowQualityRankDao;

    @Override
    public void statDate(Date date) {
        logger.info("统计指定日期的所有数据, date: {}", date);
        Date startTime = DateUtil.getStartDate(date);
        Date endTime = DateUtil.getEndDate(date);

        long start = System.currentTimeMillis();
        statProjectAndSave(startTime, endTime);
        statLowQualityModule(startTime, endTime);

        logger.info("统计完成, date: {}, costTime: {}", date, (System.currentTimeMillis() - start));
    }

    @Override
    public List<StatAll> queryStatAll(int envId, Date start, Date end) {
        return VOUtil.fromList(statAllDao.queryByDate(envId, start, end), StatAll.class);
    }

    @Override
    public List<StatProject> queryStatProject(int envId, Date start, Date end) {
        return VOUtil.fromList(statProjectDao.queryByDate(envId, start, end), StatProject.class);
    }

    @Override
    public List<LowQualityRank> queryLowQualityRank(Date start, Date end) {
        return VOUtil.fromList(lowQualityRankDao.queryByDate(start, end), LowQualityRank.class);
    }

    private void statProjectAndSave(Date startTime, Date endTime) {
        List<StatProjectResult> projectResults = deployHistoryService.statProject(startTime, endTime);

        Map<Integer, StatProjectPO> projectStatMap = new HashMap<>();
        for (StatProjectResult spr : projectResults) {
            StatProjectPO statProject = projectStatMap.get(spr.getProjectId());

            if (statProject == null) {
                statProject = new StatProjectPO();
                statProject.setEnvId(spr.getEnvId());
                statProject.setStatDate(startTime);
                statProject.setProjectId(spr.getProjectId());
                projectStatMap.put(spr.getProjectId(), statProject);
            }

            statProject.setDeployTimes(statProject.getDeployTimes() + spr.getNum());
            if (spr.getResult() == DeployResult.SUCCESS.getValue()) {
                statProject.setSuccess(statProject.getSuccess() + spr.getNum());
            } else {
                // 其他都算做失败，包括部分成功的
                statProject.setFailure(statProject.getFailure() + spr.getNum());
            }
        }

        Collection<StatProjectPO> statProjects = projectStatMap.values();
        if (statProjects.isEmpty()) {
            logger.warn("没有发布数据");
            return;
        }

        statProjectDao.deleteByDate(startTime);

        statProjectDao.batchInsertOrUpdate(statProjects);

        saveStatAll(startTime, statProjects);
    }

    private void saveStatAll(Date date, Collection<StatProjectPO> collection) {
        Map<Integer, StatAllPO> envStat = new HashMap<>();
        for (StatProjectPO sp : collection) {
            StatAllPO statAll = envStat.get(sp.getEnvId());
            if (statAll == null) {
                statAll = new StatAllPO();
                statAll.setEnvId(sp.getEnvId());
                statAll.setStatDate(sp.getStatDate());
                envStat.put(sp.getEnvId(), statAll);
            }
            statAll.setDeployTimes(statAll.getDeployTimes() + sp.getDeployTimes());
            statAll.setSuccess(statAll.getSuccess() + sp.getSuccess());
            statAll.setFailure(statAll.getFailure() + sp.getFailure());
        }
        statAllDao.deleteByDate(date);
        statAllDao.batchInsertOrUpdate(envStat.values());
    }

    private void statLowQualityModule(Date startTime, Date endTime) {
        List<LowQualityRank> lowQualityRanks = deployHistoryService.statLowQualityModule(startTime, endTime);

        if (lowQualityRanks.isEmpty()) {
            logger.warn("没有发布数据");
            return;
        }

        List<LowQualityRankPO> list = new ArrayList<>(lowQualityRanks.size());

        for (LowQualityRank rank : lowQualityRanks) {
            LowQualityRankPO po = new LowQualityRankPO();
            po.setModuleId(rank.getModuleId());
            po.setDeployTimes(rank.getDeployTimes());
            po.setStatDate(startTime);
        }

        lowQualityRankDao.deleteByDate(startTime);

        lowQualityRankDao.batchInsertOrUpdate(list);
    }
}
