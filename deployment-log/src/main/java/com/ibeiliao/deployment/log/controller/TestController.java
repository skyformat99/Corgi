package com.ibeiliao.deployment.log.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/17
 */
@Controller
@RequestMapping("/admin/test")
public class TestController {

    @RequestMapping("/getHosts")
    @ResponseBody
    public Object getHosts(HttpServletRequest request, HttpServletResponse response){
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        array.add("192.168.33.11");
        array.add("192.168.33.12");
        result.put("dev_groups", array);

        return result;
    }

    @RequestMapping("/receiveLogData")
    @ResponseBody
    public String receiveLogData(HttpServletRequest request, HttpServletResponse response,
                                 String server, String data) throws Exception{
        System.out.println("收到服务器:" + server + " 发送的日志, 内容:[" + URLDecoder.decode(data, "utf-8") + "]");
        return "success";
    }
}
