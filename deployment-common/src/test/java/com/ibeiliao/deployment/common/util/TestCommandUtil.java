package com.ibeiliao.deployment.common.util;

import com.google.common.collect.Lists;
import com.ibeiliao.deployment.cmd.AnsibleCommand;
import com.ibeiliao.deployment.cmd.AnsibleCommandResult;
import com.ibeiliao.deployment.cmd.CommandResult;
import com.ibeiliao.deployment.cmd.CommandUtil;
import org.junit.Test;

/**
 * 详情 : 命令工具类
 * <p>
 * 详细 :
 *
 * @author liangguanglong 17/2/17
 */
public class TestCommandUtil {

    /**
     * 测试执行ansible命令
     */
    @Test
    public void testExecAnsible() {
        String[] args = {"-i", "/etc/ansible/hosts.inventory", "all", "-m", "shell", "-a", "echo \"123\"", "-T", "20"};
        AnsibleCommand ansibleCommand = new AnsibleCommand();
        CommandResult exec = ansibleCommand.exec(args);
        assert exec.getExitValue() == 0;
        AnsibleCommandResult commandResult = AnsibleCommand.parseToAnsibleResult(exec, Lists.newArrayList("123.56.158.175", "1.1.1.1"));
        assert commandResult.isSuccess();
    }

    /**
     * 测试ansible ping的功能
     */
    @Test
    public void testPing() {
        AnsibleCommandResult result = CommandUtil.ansiblePing(Lists.newArrayList("123.56.158.175", "1.1.1.1"));
        assert result.isSuccess();
    }
}
