package com.ibeiliao.deployment.admin.dao.stat;

import com.ibeiliao.deployment.admin.po.stat.LowQualityRankPO;
import com.ibeiliao.deployment.common.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 功能：测试 LowQualityRankDao
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class LowQualityRankDaoTest {

    @Autowired
    private LowQualityRankDao lowQualityRankDao;

    private Random random = new Random();

    @Test
    public void testBatchInsertAndQuery() {
        Date today = new Date();
        int module1Id = 6;
        List<LowQualityRankPO> list = new ArrayList<>();
        list.add(create(today, module1Id));
        list.add(create(today, 2));
        list.add(create(today, 3));
        list.add(create(today, 4));
        lowQualityRankDao.batchInsertOrUpdate(list);

        Date startDate = DateUtil.currentStartDate();
        Date endDate = DateUtil.currentEndDate();
        List<LowQualityRankPO> queryResult = lowQualityRankDao.queryByDate(startDate, endDate);
        assertNotNull(queryResult);
        assertTrue(queryResult.size() == 1);

        // 检查 on duplicate
        list = new ArrayList<>();

        LowQualityRankPO module1 = create(today, module1Id);
        list.add(module1);
        list.add(create(today, 2));
        lowQualityRankDao.batchInsertOrUpdate(list);
        queryResult = lowQualityRankDao.queryByDate(startDate, endDate);
        boolean found = false;
        for (LowQualityRankPO tmp : queryResult) {
            if (tmp.getModuleId() == module1Id) {
                assertTrue(tmp.getDeployTimes() == module1.getDeployTimes());
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    private LowQualityRankPO create(Date date, int moduleId) {
        LowQualityRankPO po = new LowQualityRankPO();
        po.setStatDate(date);
        po.setDeployTimes(random.nextInt(1000000));
        po.setModuleId(moduleId);
        return po;
    }
}