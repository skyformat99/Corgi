package com.ibeiliao.deployment.common.enums;

/**
 * 功能: 日志类型定义
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/4
 */
public enum LogType {
    SERVER_SHELL_LOG((short) 1, "shell 脚本日志"),
    COMPILE_LOG((short) 2, "编译脚本日志"),
    MAIN_STEP_LOG((short)3, "发布步骤日志"),
    SERVER_DEPLOY_LOG((short)4, "每台服务器发布步骤日志"),;

    /**
     * 类型
     */
    private short type;

    /**
     * 名称
     */
    private String name;


    private LogType(short type, String name) {
        this.type = type;
        this.name = name;
    }

    public static LogType from (short type){
        for(LogType temp : LogType.values()){
            if(type == temp.getType()){
                return temp;
            }
        }
        return null;
    }



    public short getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
