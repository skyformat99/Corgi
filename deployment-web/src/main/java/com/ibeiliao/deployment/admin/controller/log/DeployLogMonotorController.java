package com.ibeiliao.deployment.admin.controller.log;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能:
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
@Controller
public class DeployLogMonotorController {

    @RequestMapping("/admin/test.xhtml")
    public String testDemo(HttpServletRequest request, HttpServletResponse response){
        return "test";
    }

}
