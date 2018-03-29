package com.ibeiliao.deployment.admin.service.stat.impl;

import com.ibeiliao.deployment.admin.service.stat.StatService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * 功能：测试 StatService
 * 详细：
 *
 * @author linyi, 2017/2/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class StatServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private StatService statService;

    @Test
    public void testStatDate() {
        Date date = new Date();
        statService.statDate(date);

        Date yesterday = DateUtils.addDays(date, -1);
        statService.statDate(yesterday);

        // 测试重复统计
        statService.statDate(yesterday);
    }
}