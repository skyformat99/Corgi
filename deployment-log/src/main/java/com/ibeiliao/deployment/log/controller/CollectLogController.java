package com.ibeiliao.deployment.log.controller;

import com.ibeiliao.deployment.common.enums.LogType;
import com.ibeiliao.deployment.log.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * 功能: 日志收集
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/24
 */
@Controller
public class CollectLogController {

    private static final Logger logger = LoggerFactory.getLogger(CollectLogController.class);

    @Autowired
    private LogService logService;


    @RequestMapping("/log/collect.do")
    @ResponseBody
    public String collect(HttpServletRequest request, HttpServletResponse response,
                          int id, short logType, String content) {
        logger.info("收到服务器:{} 的日志, 日志类型:{}, id:{}", request.getRemoteAddr(), logType, id);
        if (content == null) {
            return "FAIL";
        }
        try {
            if (logService.collectDeployLog(id, logType, URLDecoder.decode(content, "UTF-8"))) {
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        }catch (Exception e){
            logger.error("收集日志失败 | server:{}, logType:{}, id:{}, msg:{}", request.getRemoteAddr(), logType, id, e.getMessage(), e);
            return "FAIL";
        }

    }


}
