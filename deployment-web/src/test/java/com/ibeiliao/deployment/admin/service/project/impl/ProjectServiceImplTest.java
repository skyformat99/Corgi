package com.ibeiliao.deployment.admin.service.project.impl;

import com.ibeiliao.deployment.admin.service.project.ProjectModuleService;
import com.ibeiliao.deployment.admin.service.project.ProjectService;
import com.ibeiliao.deployment.admin.vo.project.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 功能：测试 ProjectService
 * 详细：
 *
 * @author linyi, 2017/2/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
@Profile("dev")
public class ProjectServiceImplTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectModuleService projectModuleService;

    /**
     * 仅测试语法
     */
    @Test
    public void testQueryAdminProjects() {
        long accountId = 100;
        List<Project> projects = projectService.queryAdminProjects(accountId,null, 0L, null, 1, 100);
        assertNotNull(projects);
    }

    @Test
    public void testDeleteModule() {
        projectModuleService.deleteByModuleId(13);
    }
}