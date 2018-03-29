package com.ibeiliao.deployment.cmd;

/**
 * 功能：执行命令接口
 * 详细：
 *
 * @author linyi, 2017/2/15.
 */
public interface Command {

    CommandResult exec(String[] cmdArray);
}
