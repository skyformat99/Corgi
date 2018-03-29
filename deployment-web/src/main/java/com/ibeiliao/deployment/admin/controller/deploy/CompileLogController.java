package com.ibeiliao.deployment.admin.controller.deploy;

import com.ibeiliao.deployment.admin.annotation.authority.AllowAnonymous;
import com.ibeiliao.deployment.admin.vo.deploy.CompileLogVO;
import com.ibeiliao.deployment.common.util.RedisLogKey;
import com.ibeiliao.deployment.common.util.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能：显示编译日志
 * 详细：
 *
 * @author linyi, 2017/2/20.
 */
@Controller
@RequestMapping("/admin/deploy/")
public class CompileLogController {
    private static final Logger logger = LoggerFactory.getLogger(CompileLogController.class);


    @Autowired
    private Redis redis;

    /**
     * 读取编译日志主页，xhtml 仅用于展示页面，ajax 调用接口获取参数
     *
     * @return
     */
    @RequestMapping("compileLog.xhtml")
    @AllowAnonymous
    public String index(HttpServletRequest request) {
        return "/deploy/compile_log";
    }


    @RequestMapping("getCompileLog")
    @AllowAnonymous
    @ResponseBody
    public CompileLogVO getCompileLog(int historyId, int offset) {
        CompileLogVO logVO = new CompileLogVO();
        logVO.setOffset(offset);

        String key = RedisLogKey.getModuleCompileKey(historyId);
        Long count = redis.llen(key);
        //logger.info("编译日志，总数:{}, 当前偏移量:{}", count, offset);
        if (offset == count) {
            return logVO;
        }
        if (count > 0) {
            List<String> logs = redis.lrange(key, offset, count);
            logVO.setLogs(logs);
            logVO.setOffset(count.intValue());
            return logVO;
        }
        return logVO;
    }
}
