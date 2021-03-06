package com.ibeiliao.deployment.admin.dao.stat;

import com.ibeiliao.deployment.admin.po.stat.LowQualityRankPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * t_low_quality_rank 表的操作接口
 * 
 * @author linyi 2017-02-27
 */
@MyBatisDao
public interface LowQualityRankDao {

	/**
	 * 批量插入记录
	 * @param list
	 */
	void batchInsertOrUpdate(Collection<LowQualityRankPO> list);

	/**
	 * 按日期范围 [startDate, endDate] 查询统计发布次数的总和，结果按 deployTimes 倒序排列
	 * @param startDate  开始日期
	 * @param endDate    结束日期
	 * @return
	 */
	List<LowQualityRankPO> queryByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * 删除指定日期的所有数据
	 * @param date
	 * @return
	 */
	int deleteByDate(@Param("date") Date date);
}
