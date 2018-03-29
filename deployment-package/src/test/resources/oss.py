# -*-  encoding:utf8 -*-


# !/usr/bin/env  python
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

def tail(file_name, pid):
    '''tail 读取文件内容, 超过一定时间时会检测进程是否存在，若不存在则退出'''

    ### 5秒钟 ####
    check_pid_time = 10
    time_count = 0
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
                if ( time_count >= check_pid_time  and (not check_process_exists(pid) ) and len(content_arr) ==0 ):
                    # print '退出 python'
                    is_continue = False
                    exit(0)

                continue
            time_count = 0
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


def send_data_to_log_server(server_url, server_deploy_id, type):
    '''发送内容至日志服务器'''
    time_count = 0  #
    timeout = 2  # 超时
    max_send_line_size = 10
    ip_address = get_local_ip_address()
    while True:

        if len(content_arr) >= max_send_line_size \
                or (len(content_arr) > 0 and time_count >= timeout):
            size = len(content_arr)
            log_str = ''

            for i in range(0, size):
                log_str = log_str + (content_arr.pop(0))
                # print 'log_str:%s' % (log_str)
            params = urllib.urlencode(dict(
                serverDeployId=server_deploy_id,
                type=type,
                content=urllib.quote(log_str)
            ))
            time_count = 0
            req = urllib2.Request(server_url, data=params)
            resp = urllib2.urlopen(req)
            data = resp.read()
            if data != 'success':
                print '请求 %s 失败, 返回内容:%s' %(server_url, data)



        else:
            time_count = time_count + 0.5
            time.sleep(0.5)


def read_exec_log_with_daemon(file_name, pid):
    '''后台启动线程读取日志'''

    def tmp(file_name, pid):
        c = tail(file_name, pid)
        if not c:
            content_arr.append(c)

    thread = threading.Thread(target=tmp, args=(file_name, pid))
    thread.setDaemon(True)
    thread.start()


class SendDataThread(threading.Thread):
    '''后台发送日志线程'''

    def __init__(self, server_url, server_deploy_id, type):
        threading.Thread.__init__(self)
        self.server_url = server_url
        self.server_deploy_id = server_deploy_id
        self.type = type

    def run(self):
        send_data_to_log_server(self.server_url, self.server_deploy_id, self.type)


def send_data_with_daemon(server_url, server_deploy_id, type):
    '''后台启动线程发送日志'''
    thread = SendDataThread(server_url, server_deploy_id, type)
    thread.setDaemon(True)
    thread.start()


def get_local_ip_address():
    '''获取本地的ip地址'''
    local_ip = socket.gethostbyname(socket.gethostname())
    return local_ip


if __name__ == '__main__':
    if len(argv) < 6:
        print '参数不正确, 正确参数: [file_name/文件全路径]  [pid/监控的脚本pid] [type/日志类型:(shell|compile)] [server_deploy_id/服务器发布id(当为type类型为compile时为0)] [server_url/接口地址]'
        exit(1)

    file_name = argv[1]
    pid = int(argv[2])
    type = argv[3]
    server_deploy_id = argv[4]
    server_url = argv[5]

    if type != 'shell' and type != 'compile':
        print 'type 类型不正确, 值只能为 shell | compile'


    # print "file_name:%s, pdi:%s, server_url:%s" %(file_name, pid, server_url)
    send_data_with_daemon(server_url)

    tail(file_name, pid)


        # read_exec_log_with_daemon(file_name, int(pid))
