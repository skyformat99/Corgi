package com.ibeiliao.deployment.admin.dao.stat;

import com.ibeiliao.deployment.admin.dao.project.ProjectDao;
import com.ibeiliao.deployment.admin.po.project.ProjectPO;
import com.ibeiliao.deployment.admin.po.stat.StatProjectPO;
import com.ibeiliao.deployment.common.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * 功能：测试 StatProjectDao
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class StatProjectDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private StatProjectDao statProjectDao;

    @Autowired
    private ProjectDao projectDao;

    private Random random = new Random();

    private int projectId = 0;

    @Test
    public void testBatchInsertAndQuery() {
        Date today = new Date();
        projectId = createProject();
        int env1Id = 1;
        List<StatProjectPO> list = new ArrayList<>();
        list.add(create(today, env1Id));
        list.add(create(today, 2));
        list.add(create(today, 3));
        list.add(create(today, 4));
        statProjectDao.batchInsertOrUpdate(list);

        Date startDate = DateUtil.currentStartDate();
        Date endDate = DateUtil.currentEndDate();
        List<StatProjectPO> queryResult = statProjectDao.queryByDate(env1Id, startDate, endDate);
        assertTrue(queryResult.size() == 1);

        // 检查 on duplicate
        list = new ArrayList<>();

        StatProjectPO env1 = create(today, env1Id);
        list.add(env1);
        list.add(create(today, 2));
        statProjectDao.batchInsertOrUpdate(list);
        queryResult = statProjectDao.queryByDate(env1.getEnvId(), startDate, endDate);
        assertTrue(queryResult.size() == 1);
        StatProjectPO tmp = queryResult.get(0);
        assertTrue(tmp.getDeployTimes() == env1.getDeployTimes());

    }

    private int createProject() {
        ProjectPO po = new ProjectPO();
        po.setProjectNo("#" + System.currentTimeMillis());
        po.setCreateTime(new Date());
        po.setProgramLanguage("java");
        po.setManagerEmail("a@b.com");
        po.setManagerName("Sam");
        po.setManagerPhone("13000001111");
        po.setProjectName("some project");
        projectDao.insert(po);
        return po.getProjectId();
    }

    private StatProjectPO create(Date date, int envId) {
        StatProjectPO po = new StatProjectPO();
        po.setStatDate(date);
        po.setDeployTimes(random.nextInt(1000000));
        po.setProjectId(projectId);
        po.setSuccess(1);
        po.setEnvId(envId);
        return po;
    }
}