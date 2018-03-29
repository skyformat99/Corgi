package com.ibeiliao.deployment.admin.controller.log;

import com.alibaba.fastjson.JSONObject;
import com.ibeiliao.deployment.admin.common.RestResult;
import com.ibeiliao.deployment.admin.service.server.DeployLogService;
import com.ibeiliao.deployment.common.vo.ServerCollectLog;
import com.ibeiliao.deployment.base.ApiCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能: 收集log 日志接口
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
@Controller
@RequestMapping("/log")
public class CollectLogController {


    private static final Logger logger = LoggerFactory.getLogger(CollectLogController.class);

    @Autowired
    private DeployLogService deployLogService;

    @RequestMapping("/shellLog/save.do")
    @ResponseBody
    public RestResult saveShellLogs(HttpServletRequest request, HttpServletResponse response,
                                    String data){
        if(StringUtils.isNotEmpty(data)){
            List<ServerCollectLog> list = JSONObject.parseArray(data, ServerCollectLog.class);
            logger.info("获取到的日志内容为:" + data);
            //批量保存日志
            deployLogService.batchSaveServerDeployLog(list);
            //批量发布日志增量推送信息
            deployLogService.publishSubscribeLogChangeMsg(list);
            
            return new RestResult(ApiCode.SUCCESS, "保存成功");
        }else{
            return new RestResult(ApiCode.FAILURE, "内容不能为空");
        }


    }



}
