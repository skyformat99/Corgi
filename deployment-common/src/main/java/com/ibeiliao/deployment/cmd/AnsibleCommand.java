package com.ibeiliao.deployment.cmd;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibeiliao.deployment.common.enums.DeployResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：通过 ansible 执行命令
 * 详细：
 *
 * @author linyi, 2017/2/15.
 */
public class AnsibleCommand extends LocalCommand {

      protected static final Logger logger = LoggerFactory.getLogger(AnsibleCommand.class);


    /**
     * 并行数
     */
    private int fork = 5;

    /**
     * timeout时间，秒
     */
    private int timeout = MAX_TIMEOUT;

    /**
     * 用户
     */
    private String user;

    public static final String ANSIBLE = "ansible";

    private static final String SUCCESS_FLAG = "SUCCESS";

    private static final String ANSIBLE_CONFIG = "export ANSIBLE_SSH_ARGS='-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o CheckHostIP=false';";

    public AnsibleCommand() {}

    @Override
    public CommandResult exec(String[] cmdArray) {

        //logger.info("ansible 命令：" + StringUtils.join(cmdArray, " "));
        return super.exec(addAnsibleArguments(cmdArray));
    }

    public int getFork() {
        return fork;
    }

    public void setFork(int fork) {
        this.fork = fork;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String[] addAnsibleArguments(String[] cmdArray) {
        boolean hasFork = false;
        boolean hasTimeout = false;
        boolean hasUser = false;
        boolean hasAnsible = false;
        List<String> cmds = new LinkedList<>();
        for (String str : cmdArray) {
            if ("-f".equals(str)) {
                hasFork = true;
            } else if ("-T".equals(str)) {
                hasTimeout = true;
            } else if ("-u".equals(str)) {
                hasUser = true;
            } else if (ANSIBLE.equalsIgnoreCase(str)) {
                hasAnsible = true;
            }
            cmds.add(str);
        }

        if (!hasAnsible) {
            cmds.add(0, ANSIBLE);
        }
//        cmds.add(0, ANSIBLE_CONFIG);
        if (!hasFork && fork > 0) {
            cmds.add("-f");
            cmds.add(fork + "");
        }
        if (!hasTimeout && timeout > 0) {
            cmds.add("-T");
            cmds.add(timeout + "");
        }
        if (!hasUser && StringUtils.isNotEmpty(user)) {
            cmds.add("-u");
            cmds.add(user);
        }
        if (cmds.size() == cmdArray.length) {
            return cmdArray;
        }
        return cmds.toArray(new String[cmds.size()]);
    }

    /**
     * 将command result 经过分析返回日志 转化为 ansible result
     *
     * @param result 执行的结果
     * @param hosts  执行关联的host
     * @return
     */
    public static AnsibleCommandResult parseToAnsibleResult(CommandResult result, List<String> hosts) {
        AnsibleCommandResult ansibleResult = new AnsibleCommandResult();
        ansibleResult.setExitValue(result.getExitValue());
        ansibleResult.setHost(result.getHost());
        ansibleResult.setMessage(result.getMessage());
        ansibleResult.setErrorMessage(result.getErrorMessage());

        // 分析输出结果
        analyseAnsibleMessage(ansibleResult, hosts);

        if (ansibleResult.getIp2FailLogMap().isEmpty()) {
            ansibleResult.setSuccessType(DeployResult.SUCCESS);
        } else {
            if (ansibleResult.getIp2FailLogMap().size() == hosts.size()) {
                ansibleResult.setSuccessType(DeployResult.FAILURE);
            } else {
                ansibleResult.setSuccessType(DeployResult.PARTIAL_SUCCESS);
            }
        }

        return ansibleResult;
    }

    private static void analyseAnsibleMessage(AnsibleCommandResult result, List<String> hosts) {
        HashMap<String, Boolean> ip2ResultMap = Maps.newHashMap();
        HashMap<String, String> ip2FailLogMap = Maps.newHashMap();

        Map<String, String> ip2MessageMap = analyseMessage(result, hosts);

        for (String host : hosts) {
            if (ip2MessageMap.get(host) != null) {
                String message = ip2MessageMap.get(host);
                ip2ResultMap.put(host, checkServerSuccess(message));
                if (!checkServerSuccess(message)) {
                    ip2FailLogMap.put(host, message);
                }
            } else {
                ip2ResultMap.put(host, Boolean.FALSE);
            }
        }
        result.setIp2FailLogMap(ip2FailLogMap);
        result.setIp2ResultMap(ip2ResultMap);
    }

    private static boolean checkServerSuccess(String message) {
        return message.contains("success") || message.contains("SUCCESS");
    }

    private static Map<String, String> analyseMessage(AnsibleCommandResult result, List<String> ips) {
        if (StringUtils.isBlank(result.getErrorMessage()) && StringUtils.isBlank(result.getMessage())) {
            return Collections.emptyMap();
        }
        String allMessage = result.getErrorMessage() + result.getMessage();
        Map<String, String> ip2MessageMap = Maps.newHashMap();
        for (String currentIp : ips) {
            String replacedMessage = allMessage.replaceFirst(currentIp, currentIp + "_first");
            String[] split = replacedMessage.split(currentIp + "_first");

            List<String> otherIps = Lists.newArrayList();
            for (String ip : ips) {
                if (!Objects.equals(currentIp,ip)) {
                    otherIps.add(ip);
                }
            }
            String[] split1 = split[1].split(StringUtils.join(otherIps, "|"));

            ip2MessageMap.put(currentIp, split1[0]);

        }
        return ip2MessageMap;
    }

    public static AnsibleCommandResult parse(String message, List<String> hosts) {
        String regExp = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) \\| (SUCCESS|FAILED|UNREACHABLE)([\\s\\S].*?)";
        message = StringUtils.trimToEmpty(message);
        Map<String, Boolean> ipResultMap = new HashMap<>();
        Map<String, String> failIpToReasonMap = new HashMap<>();
        List<String> ipList = new ArrayList<>();

        int successCount = 0;
        if (message.isEmpty()) {
            throw new IllegalArgumentException("message is empty");
        } else if (message.contains("ERROR")) {
            for (String ip : hosts) {
                failIpToReasonMap.put(ip, "执行命令错误，请检查命令或用户是否正确");
                ipResultMap.put(ip, Boolean.FALSE);
            }
        } else {
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String ip = matcher.group(1);
                String flag = matcher.group(2); // SUCCESS/FAIL
                if (SUCCESS_FLAG.equalsIgnoreCase(flag)) {
                    ipResultMap.put(ip, Boolean.TRUE);
                    successCount++;
                } else {
                    ipResultMap.put(ip, Boolean.FALSE);
                    failIpToReasonMap.put(ip, flag);
                }
                ipList.add(ip);
            }

        } // end if

        dealFailMessage(message, ipList, ipResultMap, failIpToReasonMap);

        AnsibleCommandResult result = new AnsibleCommandResult();
        result.setSuccess(successCount > 0);
        result.setSuccessType((successCount == 0 ? DeployResult.FAILURE : (successCount == hosts.size() ? DeployResult.SUCCESS : DeployResult.PARTIAL_SUCCESS)));
        result.setIp2ResultMap(ipResultMap);
        result.setIp2FailLogMap(failIpToReasonMap);
        return result;
    }

    /**
     * 对于失败的结果，采取笨方法处理…………：
     * 截取当前失败IP和下一个IP之间的错误信息
     * @param message
     * @param ipList
     * @param ipResultMap
     * @param failIpToReasonMap
     */
    private static void dealFailMessage(String message, List<String> ipList, Map<String, Boolean> ipResultMap, Map<String, String> failIpToReasonMap) {
        int size = ipList.size();
        for (int i=0; i < size; i++) {
            String ip = ipList.get(i);
            Boolean result = ipResultMap.get(ip);
            if (!result) {
                String flag = failIpToReasonMap.get(ip);
                int pos1 = message.indexOf(ip);
                int pos2 = message.indexOf(flag, pos1 + 1);
                int pos3 = 0;
                if (i == size - 1) {
                    pos3 = message.length();
                } else {
                    pos3 = message.indexOf(ipList.get(i + 1), pos2 + flag.length());
                }
                if (pos1 >= 0 && pos2 > 0 && pos3 > 0) {
                    String reason = message.substring(pos2 + flag.length(), pos3);
                    failIpToReasonMap.put(ip, reason);
                }
            }
        }
    }

    public static void main(String[] args) {
        String message = "10.51.34.26 | SUCCESS => {\n" +
                "    \"changed\": false,\n" +
                "    \"checksum\": \"adc83b19e793491b1c6ea0fd8b46cd9f32e592fc\",\n" +
                "    \"dest\": \"/data/test/cpfile.log\",\n" +
                "    \"gid\": 100,\n" +
                "    \"group\": \"users\",\n" +
                "    \"mode\": \"0755\",\n" +
                "    \"owner\": \"web\",\n" +
                "    \"path\": \"/data/test/cpfile.log\",\n" +
                "    \"size\": 1,\n" +
                "    \"state\": \"file\",\n" +
                "    \"uid\": 1008\n" +
                "}\n" +
                "10.117.67.177 | FAILED! => {\n" +
                "    \"changed\": false,\n" +
                "    \"checksum\": \"adc83b19e793491b1c6ea0fd8b46cd9f32e592fc\",\n" +
                "    \"failed\": true,\n" +
                "    \"msg\": \"Destination /data/test not writable\"\n" +
                "}\n" +
                "10.168.166.3 | FAILED! => {\n" +
                "    \"changed\": false,\n" +
                "    \"checksum\": \"adc83b19e793491b1c6ea0fd8b46cd9f32e592fc\",\n" +
                "    \"failed\": true,\n" +
                "    \"msg\": \"Destination /data not writable\"\n" +
                "}\n" +
                "100.117.67.178 | UNREACHABLE! => {\n" +
                "    \"changed\": false,\n" +
                "    \"msg\": \"Failed to connect to the host via ssh: ssh: connect to host 100.117.67.177 port 32200: Connection timed out\\r\\n\",\n" +
                "    \"unreachable\": true\n" +
                "}";
        List<String> hosts = new ArrayList<>();
        hosts.add("10.51.34.26");
        hosts.add("10.117.67.177");
        hosts.add("10.168.166.3");
        hosts.add("100.117.67.178");

        AnsibleCommandResult result = parse(message, hosts);
        System.out.println(JSON.toJSONString(result));
//
//        System.out.println("---");
//        message = "10.51.34.26 | SUCCESS => {\n" +
//                "    \"changed\": false,\n" +
//                "    \"checksum\": \"adc83b19e793491b1c6ea0fd8b46cd9f32e592fc\",\n" +
//                "    \"dest\": \"/data/test/cpfile.log\",\n" +
//                "    \"gid\": 100,\n" +
//                "    \"group\": \"users\",\n" +
//                "    \"mode\": \"0755\",\n" +
//                "    \"owner\": \"web\",\n" +
//                "    \"path\": \"/data/test/cpfile.log\",\n" +
//                "    \"size\": 1,\n" +
//                "    \"state\": \"file\",\n" +
//                "    \"uid\": 1008\n" +
//                "}";
//        message = "10.51.34.26 | SUCCESS => {\n";
//        parse(message);
//        String message = "100.51.34.29 | UNREACHABLE! => {    \"changed\": false,     \"msg\": \"Failed to connect to the host via ssh: ssh: connect to host 100.51.34.29 port 32200: Connection timed out\\r\\n\",     \"unreachable\": true}100.51.34.30 | UNREACHABLE! => {    \"changed\": false,     \"msg\": \"Failed to connect to the host via ssh: ssh: connect to host 100.51.34.30 port 32200: Connection timed out\\r\\n\",     \"unreachable\": true}";
//        List<String> hosts = new ArrayList<>();
//        hosts.add("100.51.34.29");
//        hosts.add("100.51.34.30");
//        AnsibleCommandResult result = parse(message, hosts);
//        System.out.println(result.getIp2ResultMap().size());

//        String message = "10.51.34.26 | SUCCESS | rc=0 >>/data/project/coupon//pf-coupon-impl.:/home/jdk/lib/dt.jar:/home/jdk/lib/tools.jar:/home/jdk/jre/lib/rt.jar:/data/project/coupon//pf-coupon-impl/*stop pf-coupon-impl ......pf-coupon-impl 已经停止starting pf-coupon-impl.........pf-coupon-impl started. see /data/logs/coupon/pf-coupon-impl//app.log0";
//        List<String> hosts = new ArrayList<>();
//        hosts.add("10.51.34.26");
//        AnsibleCommandResult result = parse(message, hosts);
//        System.out.println(result.getSuccessType());
//        System.out.println(result.getIp2ResultMap().size());
    }
}
