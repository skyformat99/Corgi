# -*-  encoding:utf8 -*-
# !/usr/bin/env  python
#
# author: lihaiwen
# date  : 2017/2/7
#
#需要安装 oss2

import oss2
from sys import argv


def upload_to_oss(file_path, save_name, access_key_id, access_key_secret, endpoint, bucket):
    '''上传文件至oss'''
    auth = oss2.Auth(access_key_id, access_key_secret)
    bucket_opt = oss2.Bucket(auth, endpoint, bucket)

    result = bucket_opt.put_object_from_file(key=save_name, filename=file_path)
    return result.status == 200




if __name__ == '__main__':

    if len(argv) < 7:
        print '参数不正确, 正确参数: [file_path/文件全路径]  [save_name/保存至oss的key名称] [access_key_id] [access_key_secret] [endpoint] [bucket]'
        exit(1)

    file_path = argv[1]
    save_name = argv[2]
    access_key_id = argv[3]
    access_key_secret = argv[4]
    endpoint = argv[5]
    bucket = argv[6]

    result = upload_to_oss(file_path, save_name, access_key_id, access_key_secret, endpoint, bucket)

    print result








