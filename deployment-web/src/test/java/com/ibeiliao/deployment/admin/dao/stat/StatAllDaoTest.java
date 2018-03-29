package com.ibeiliao.deployment.admin.dao.stat;

import com.ibeiliao.deployment.admin.po.stat.StatAllPO;
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
 * 功能：测试 StatAllDao
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class StatAllDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private StatAllDao statAllDao;

    private Random random = new Random();

    @Test
    public void testBatchInsertAndQuery() {
        Date today = new Date();
        int env1Id = 1;
        List<StatAllPO> list = new ArrayList<>();
        list.add(create(today, env1Id));
        list.add(create(today, 2));
        list.add(create(today, 3));
        list.add(create(today, 4));
        statAllDao.batchInsertOrUpdate(list);

        Date startDate = DateUtil.currentStartDate();
        Date endDate = DateUtil.currentEndDate();
        List<StatAllPO> queryResult = statAllDao.queryByDate(env1Id, startDate, endDate);
        assertTrue(queryResult.size() == 1);

        // 检查 on duplicate
        list = new ArrayList<>();

        StatAllPO env1 = create(today, env1Id);
        list.add(env1);
        list.add(create(today, 2));
        statAllDao.batchInsertOrUpdate(list);
        queryResult = statAllDao.queryByDate(env1.getEnvId(), startDate, endDate);
        assertTrue(queryResult.size() == 1);

        StatAllPO tmp = queryResult.get(0);
        assertTrue(tmp.getDeployTimes() == env1.getDeployTimes());
    }

    private StatAllPO create(Date date, int envId) {
        StatAllPO po = new StatAllPO();
        po.setStatDate(date);
        po.setDeployTimes(random.nextInt(1000000));
        po.setSuccess(1);
        po.setEnvId(envId);
        return po;
    }
}