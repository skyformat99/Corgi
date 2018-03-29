# -*-  encoding:utf8 -*-
# !/usr/bin/env  python
#
# author: lihaiwen
# date  : 2017/2/4
#
import time
import os
import urllib2
import urllib
import threading
import socket
from sys import argv
import re

## 发送内容
content_arr = []
exit_flag = '===ibeiliao_deployment_script_execute_complete==='



def tail(file_name, no_content_timeout):
    '''tail 读取文件内容, 超过一定时间时会检测进程是否存在，若不存在则退出'''

    ### 5秒钟 ####
    time_count = 0
    no_read = False
    with open(file_name, 'r') as file:
        is_continue = True

        #file.seek(0, 2)

        while is_continue:
            line = file.readline()
            if not line:

                # print '今日。。。。。len(content_arr):%s' %(len(content_arr))
                sleep_time = 0.5
                time_count = time_count + sleep_time
                time.sleep(sleep_time)
                # print 'time_count:%s'  %(time_count)
                if ( time_count >= no_content_timeout  and len(content_arr) ==0 ):
                    # print '退出 python'
                    is_continue = False
                    exit(0)

                continue
            time_count = 0
            if line == exit_flag:
                no_read = True
                exit(0)

            #### 判断已有结束标志不再读取文档内容
            if not no_read:
                content_arr.append(line)


def check_process_exists(pid):
    '''检测进程是否存在'''
    pattern = re.compile("^\d+$")
    for line in os.popen('ps xa'):
        fields = line.split()
        if (pattern.match(fields[0])):
            if int(fields[0]) == pid:
                return True

    return False


def send_data_to_log_server(server_url, server_deploy_id, logType):
    '''发送内容至日志服务器'''
    time_count = 0  #
    timeout = 2  # 超时
    max_send_line_size = 10
    ip_address = get_local_ip_address()
    while True:
        size = len(content_arr)
        if size >= max_send_line_size \
                or (size > 0 and time_count >= timeout):
            log_str = ''

            for i in range(0, size):
                log_str = log_str + (content_arr.pop(0))
                # print 'log_str:%s' % (log_str)
            params = urllib.urlencode(dict(
                id=server_deploy_id,
                logType=logType,
                content=urllib.quote(log_str)
            ))
            time_count = 0
            req = urllib2.Request(server_url, data=params)
            resp = urllib2.urlopen(req)
            data = resp.read()
            if data != 'SUCCESS':
                print '请求 %s 失败, 返回内容:%s' %(server_url, data)



        else:
            time_count = time_count + 0.5
            time.sleep(0.5)




class SendDataThread(threading.Thread):
    '''后台发送日志线程'''

    def __init__(self, server_url, server_deploy_id, logType):
        threading.Thread.__init__(self)
        self.server_url = server_url
        self.server_deploy_id = server_deploy_id
        self.logType = logType

    def run(self):
        send_data_to_log_server(self.server_url, self.server_deploy_id, self.logType)


def send_data_with_daemon(server_url, server_deploy_id, logType):
    '''后台启动线程发送日志'''
    thread = SendDataThread(server_url, server_deploy_id, logType)
    thread.setDaemon(True)
    thread.start()


def get_local_ip_address():
    '''获取本地的ip地址'''
    local_ip = socket.gethostbyname(socket.gethostname())
    return local_ip


if __name__ == '__main__':
    if len(argv) < 6:
        print '参数不正确, 正确参数: [file_name/文件全路径]  [no_content_time_out/空内容超时时间] [type/日志类型:(shell|compile)] [server_deploy_id/服务器发布id(当为type类型为compile时为项目的module id)] [server_url/接口地址]'
        exit(1)

    file_name = argv[1]
    no_content_timeout = int(argv[2])
    type = argv[3]
    server_deploy_id = argv[4]
    server_url = argv[5]


    logType = 0
    if type != 'shell' and type != 'compile':
        print 'type 类型不正确, 值只能为 shell | compile'

    if type == 'shell':
        logType = 1
    elif type == 'compile':
        logType = 2

    send_data_with_daemon(server_url, server_deploy_id, logType)

    tail(file_name, no_content_timeout)

