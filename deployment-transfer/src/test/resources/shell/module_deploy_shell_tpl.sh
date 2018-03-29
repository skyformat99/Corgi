#!/bin/bash
# 
# name:项目发布到服务器启动脚本，包含备份、解压、启动三个过程
# author：梁广龙
# date: 2017.02.26

deployType=${DEPLOY_TYPE}

backupModule() {
    mkdir -p ${BACKUP_DIR}
    if [ -e ${MODULE_DIR} ];then
       cp -r ${MODULE_DIR} ${BACKUP_DIR}
    fi
    backupResult=$?
    if [ $backupResult -ne 0 ]; then
       exit 1
    fi
}

decompressModule() {
    mkdir -p ${MODULE_DIR}
    cd ${MODULE_DIR}
    ls |grep -v ${PID_FILE} | xargs rm -f
    cd ..
    tar zxvf ${MODULE_TAR_FILE}
    decompressResult=$?
    if [ $decompressResult -ne 0 ]; then
      exit 1
    fi
}

startupModule() {
    ${PRE_SHELL}
    ${RESTART_SHELL}
    deployResult=$?
    if [ $deployResult -ne 0 ]; then
      exit 1
    fi
    ${POST_SHELL}
}

case "$deployType" in
deploy)
      backupModule
      decompressModule
      startupModule
;;
restart)
      startupModule
;;
stop)
      startupModule
;;
*)
      exit 1
;;
esac