package com.ibeiliao.deployment.log;

/**
 * 功能：LOG不同的消息消息处理
 * 详细：
 *
 * @author linyi, 2017/3/16.
 */
public class LogMsgUtil {

    public static String getSuccMsg(String msg) {
        return "<span class=\"text-green\">" + msg  + "</span>";
    }

    public static String getFailMsg(String msg) {
        return "<span class=\"text-danger\">" + msg  + "</span>";
    }

    public static String getFatalMsg(String msg) {
        return "<strong><span class=\"text-danger\">" + msg  + "</span></strong>";
    }
}
