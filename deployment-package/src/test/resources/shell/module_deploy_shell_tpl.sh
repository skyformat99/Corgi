#!/bin/bash
# 
# name:项目发布到服务器启动脚本，包含备份、解压、启动三个过程
# author：梁广龙
# date: 2017.02.26
source /etc/profile

deployType=${DEPLOY_TYPE}

LOG_FILE=${LOG_FILE_DIR}
touch ${LOG_FILE}
echo "" > ${LOG_FILE}

mkdir -p ${GC_LOG_DIR}
webProjectFlag=${WEB_PROJECT_FLAG}
if [ $webProjectFlag == 1 ]; then
    mkdir -p ${RESIN_ACCESS_LOG_DIR}
fi

#内网地址
inner_ip=$(/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk 'NR==1{print $2}'|tr -d "addr:")

${PYTHON_COLLECT_LOG}

# 错误日志文件
touch ${MODULE_ERR_LOG}
cat /dev/null >${MODULE_ERR_LOG}

echo $inner_ip" "$(date +%H:%M:%S)" 开始发布" >> ${LOG_FILE}
backupModule() {
    mkdir -p ${BACKUP_DIR}
    if [ -e ${MODULE_DIR} ];then
       cp -r ${MODULE_DIR} ${BACKUP_DIR}
    fi
    cd ${BACKUP_DIR}
    cd ..
    # 删除备份文件，只保留10个版本
    dirNum=`ls -l |grep "^d" | wc -l`
    for (( j=1; j<$dirNum;j++))
    do
         currentDirNum=`ls -l |grep "^d" | wc -l`
         if [ $currentDirNum -gt 10 ]; then
                rm -rf `ls | awk 'NR==1{print $1}'`
          else
               break
          fi
    done
    backupResult=$?
    if [ $backupResult -ne 0 ]; then
       echo $inner_ip" "$(date +%H:%M:%S)" 备份失败" >> ${LOG_FILE}
    fi
    echo $inner_ip" "$(date +%H:%M:%S)" 备份完成" >> ${LOG_FILE}
}

decompressModule() {
    mkdir -p ${MODULE_DIR}
    cd ${MODULE_DIR}
    ls |grep -v ${PID_FILE} | xargs rm -rf
    cd ..
    tar zxvf ${MODULE_TAR_FILE}
    decompressResult=$?
    if [ $decompressResult -ne 0 ]; then
      echo $inner_ip" "$(date +%H:%M:%S)" 解压失败" >> ${LOG_FILE}
    fi
    echo $inner_ip" "$(date +%H:%M:%S)" 解压完成" >> ${LOG_FILE}
}

execPreShell() {
    # 判断是否有 preshell, 1 代表有, 0 没有
    hasPreShell=${HAS_PRESHELL}
    if [ $hasPreShell -eq 1 ]; then
       ${PRE_SHELL}
       if [ $? -ne 0 ]; then
             echo $inner_ip" "$(date +%H:%M:%S)" 执行启动前脚本失败" >> ${LOG_FILE}
           else
             echo $inner_ip" "$(date +%H:%M:%S)" 执行启动前脚本完成" >> ${LOG_FILE}
       fi
     fi
}

execRestartShell() {
    ${RESTART_SHELL}
    deployResult=$?
    if [ $deployResult -ne 0 ]; then
        echo $inner_ip" "$(date +%H:%M:%S)" 重启失败" >> ${LOG_FILE}
      else
        echo $inner_ip" "$(date +%H:%M:%S)" 重启完成" >> ${LOG_FILE}
    fi
}

execPostShell() {
    # 判断是否有 postShell, 1 代表有, 0 没有
    hasPostShell=${HAS_POSTSHELL}
    if [ $hasPostShell -eq 1 ]; then
       ${POST_SHELL}
       if [ $? -ne 0 ]; then
             echo $inner_ip" "$(date +%H:%M:%S)" 执行启动后脚本失败" >> ${LOG_FILE}
          else
             echo $inner_ip" "$(date +%H:%M:%S)" 执行启动后脚本完成" >> ${LOG_FILE}
       fi
    fi
}

# 如果是web项目,将含有 oss文件 + resin配置文件的tar包解压,将配置文件放到对应目录
prepareModuleFile() {
    if [ $webProjectFlag == 1 ]; then
        mkdir -p ${MODULE_DIR}
        cd ${MODULE_DIR}
        mv ${MODULE_TAR_FILE} ../
        # 移动resin配置文件
        mkdir -p  ${RESIN_CONF_DIR}
        mv ${CONFIG_FILE_LIST} -t ${RESIN_CONF_DIR}
    fi
}

startupModule() {
    echo $inner_ip" "$(date +%H:%M:%S)" 开始重启" >> ${LOG_FILE}
    execPreShell 1> /dev/null 2>${MODULE_ERR_LOG}
    logErrorInfo

    execRestartShell 1> /dev/null 2>${MODULE_ERR_LOG}
    logErrorInfo

    execPostShell 1> /dev/null 2>${MODULE_ERR_LOG}
    logErrorInfo
}


deployModule() {
      prepareModuleFile 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      backupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      decompressModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      startupModule
}

logErrorInfo() {
  errorInfo=`cat ${MODULE_ERR_LOG}`
  if [ -n "$errorInfo" ]; then
     echo $inner_ip" "$(date +%H:%M:%S)" 启动失败错误信息 : "$errorInfo >> ${LOG_FILE}
     sleep 1
     exit 1
  fi
}

case "$deployType" in
deploy)
      deployModule
;;
restart)
      startupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo
;;
stop)
      startupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo
;;
*)
      exit 1
;;
esac
