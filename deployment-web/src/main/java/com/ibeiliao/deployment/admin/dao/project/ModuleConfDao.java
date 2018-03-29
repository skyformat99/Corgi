package com.ibeiliao.deployment.admin.dao.project;

import com.ibeiliao.deployment.admin.po.project.ModuleConfPO;
import com.ibeiliao.deployment.datasource.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能：t_module_conf 模块配置表的操作
 * 详细：
 *
 * @author linyi, 2017/3/14.
 */
@MyBatisDao
public interface ModuleConfDao {

    /**
     * 保存数据
     *
     * @param po 要保存的对象
     */
    void insert(ModuleConfPO po);


    /**
     * 修改数据，以主键更新
     *
     * @param po - 要更新的数据
     * @return 更新的行数
     */
    int update(ModuleConfPO po);

    /**
     * 根据主键读取记录
     */
    ModuleConfPO get(@Param("confType") int confType, @Param("moduleId") int moduleId);

    /**
     * 根据类型和 匹配 http端口查找
     */
    List<ModuleConfPO> getByTypeAndValue(@Param("confType") int confType, @Param("httpPort") String httpPort, @Param("serverPort") String serverPort, @Param("watchDogPort") String watchDogPort);
}
