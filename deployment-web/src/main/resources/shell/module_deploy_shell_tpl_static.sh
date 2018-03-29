#!/bin/bash
# 
# name: 静态项目发布到服务器启动脚本，包含备份、解压、启动三个过程
# author：梁广龙
# date: 2017.02.26
. /etc/profile

deployType=${DEPLOY_TYPE}
deployId=${HISTORY_ID}
rollBackDeployId=${ROLL_BACK_ID}

LOG_FILE=${LOG_FILE_DIR}
touch ${LOG_FILE}
echo "" > ${LOG_FILE}

#内网地址
inner_ip=$(/sbin/ifconfig -a |egrep -A 1 'enp3s0|eth0' |tail -n 1|grep  -Po "(\d+\.){3}\d+"|head -n 1)


${PYTHON_COLLECT_LOG}

# 错误日志文件
touch ${MODULE_ERR_LOG}
cat /dev/null >${MODULE_ERR_LOG}

backupModule() {
    backupTime=`date '+%Y%m%d-%H%M%S'`
    mkdir -p ${PRO_BACKUP_DIR}$backupTime-$deployId
    if [ -d ${PROJECT_DIR}/release-current ];then
        hasFile=`ls ${PROJECT_DIR}/release-current | wc -l`
        if [ $hasFile  -gt 0 ];then
            cp -rf ${PROJECT_DIR}/release-current/* ${PRO_BACKUP_DIR}$backupTime-$deployId
        fi
    fi
    cd ${PRO_BACKUP_DIR}
    # 删除备份文件，只保留20个版本
    dirNum=`ls -l |grep "^d" | wc -l`
    for (( j=1; j<$dirNum;j++))
    do
         currentDirNum=`ls -l |grep "^d" | wc -l`
         if [ $currentDirNum -gt 20 ]; then
                rm -rf `ls | awk 'NR==1{print $1}'`
          else
               break
          fi
    done
    backupResult=$?
    if [ $backupResult -ne 0 ]; then
        logDeploy "备份失败"
    fi
    logDeploy "备份完成"
}

decompressModule() {
    mkdir -p ${PROJECT_DIR}/release-current
    mkdir -p ${PROJECT_DIR}/new-version

    cd ${PROJECT_DIR}
    tar xf ${MODULE_TAR_FILE} -C ${PROJECT_DIR}/new-version
    decompressResult=$?
    if [ $decompressResult -ne 0 ]; then
        logDeploy "解压失败"
    fi
        logDeploy "解压完成"
    # 切换
    mv  release-current old-version
    mv  new-version release-current
    rm -rf old-version
}

execPreShell() {
    # 判断是否有 preshell, 1 代表有, 0 没有
    hasPreShell=${HAS_PRESHELL}
    if [ $hasPreShell -eq 1 ]; then
       ${PRE_SHELL}
       if [ $? -ne 0 ]; then
              logDeploy "执行启动前脚本失败"
           else
              logDeploy "执行启动前脚本完成"
       fi
     fi
}

execPostShell() {
    # 判断是否有 postShell, 1 代表有, 0 没有
    hasPostShell=${HAS_POSTSHELL}
    if [ $hasPostShell -eq 1 ]; then
       # 后执行的默认进入当前部署的目录
       cd ${PROJECT_DIR}/release-current
       ${POST_SHELL}
       if [ $? -ne 0 ]; then
            logDeploy "执行启动后脚本失败"
          else
            logDeploy "执行启动后脚本完成"
       fi
    fi
}


deployModule() {

      decompressModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      execPreShell 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      execPostShell 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      backupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      # 解压后就完成
      logDeploy "部署完成"
}

logErrorInfo() {
  errorInfo=`cat ${MODULE_ERR_LOG}`
  if [ -n "$errorInfo" ]; then
     logDeploy "启动失败错误信息 : ""$errorInfo"
     sleep 1
     exit 1
  fi
}

logDeploy() {
    echo $inner_ip" "$(date +%H:%M:%S)" $1" >> ${LOG_FILE}
}

rollBackProjectFile() {
    cd ${PRO_BACKUP_DIR}
    deployBackupNum=`ls |grep "\-$rollBackDeployId\$" | wc -l`
    if [ $deployBackupNum -gt 1 ]; then
        echo "找到多个发布版本，请检查备份文件是否正常: $rollBackDeployId" >&2
        exit 1
    fi
    if [ $deployBackupNum -eq 0 ]; then
        echo "版本备份不存在，无法回滚" >&2
        exit 1
    fi
    rollBackVersion=`ls |grep "\-$rollBackDeployId\$"`
    # 恢复 jar/war
    rm -rf ${PROJECT_DIR}/release-current/*
    \cp -rf ${PRO_BACKUP_DIR}/$rollBackVersion/* ${PROJECT_DIR}/release-current
}


case "$deployType" in
deploy)
      logDeploy "开始发布"
      deployModule
;;
rollBack)

      logDeploy "开始回滚"

      rollBackProjectFile 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      execPreShell 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      execPostShell 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      logDeploy "回滚完成"
;;
*)
      exit 1
;;
esac
