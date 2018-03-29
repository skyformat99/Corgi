package com.ibeiliao.deployment.admin.service.stat;

import com.ibeiliao.deployment.admin.vo.stat.LowQualityRank;
import com.ibeiliao.deployment.admin.vo.stat.StatAll;
import com.ibeiliao.deployment.admin.vo.stat.StatProject;

import java.util.Date;
import java.util.List;

/**
 * 功能：统计逻辑
 * 详细：
 *
 * @author linyi, 2017/2/27.
 */
public interface StatService {

    /**
     * 统计某一天的发布数据，并入库。
     *
     * 发布数据不包括：
     * （1）未开始发布的记录；
     * （2）restart/stop的操作；
     *
     * 统计内容：
     * （1）每个项目的发布在各环境的发布次数；
     * （2）所有项目的发布次数总和；
     * （3）低质量模块统计，只统计生产环境每天发布次数 >= 2 的模块；
     * 如果 date 已经统计过，覆盖之前的统计。
     *
     * @param date 统计日期
     */
    void statDate(Date date);

    /**
     * 查询统计数据，[start, end]，返回结果按日期顺序
     * @param envId 环境ID
     * @param start 开始日期
     * @param end   结束日期
     * @return
     */
    List<StatAll> queryStatAll(int envId, Date start, Date end);

    /**
     * 查询每个项目的统计数据，[start, end]，返回结果按日期顺序
     * @param envId 环境ID
     * @param start 开始日期
     * @param end   结束日期
     * @return
     */
    List<StatProject> queryStatProject(int envId, Date start, Date end);

    /**
     * 查询低质量的模块统计数据，[start, end]，返回结果按日期顺序
     * @param start 开始日期
     * @param end   结束日期
     * @return
     */
    List<LowQualityRank> queryLowQualityRank(Date start, Date end);
}
