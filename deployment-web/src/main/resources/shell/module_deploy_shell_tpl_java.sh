#!/bin/bash
# 
# name:java项目发布到服务器启动脚本，包含备份、解压、启动三个过程
# author：梁广龙
# date: 2017.02.26
# 如果需要改变启动方式，需要传一个参数，restart | stop
. /etc/profile

deployType=${DEPLOY_TYPE}
deployId=${HISTORY_ID}
rollBackDeployId=${ROLL_BACK_ID}
# 在手动重启的时候，通过shell传参数将服务重启完成 （目前只能传restart stop参数）
if [ ! -z $1 ]; then
   deployType=$1
fi

LOG_FILE=${LOG_FILE_DIR}
touch ${LOG_FILE}
echo "" > ${LOG_FILE}

mkdir -p ${GC_LOG_DIR}
webProjectFlag=${WEB_PROJECT_FLAG}
if [ $webProjectFlag -eq 1 ]; then
    mkdir -p ${RESIN_ACCESS_LOG_DIR}
fi

#内网地址
inner_ip=$(/sbin/ifconfig -a |egrep -A 1 'enp3s0|eth0' |tail -n 1|grep  -Po "(\d+\.){3}\d+"|head -n 1)

if [ "$deployType" != "restart" ];then
    ${PYTHON_COLLECT_LOG}
fi

# 错误日志文件
touch ${MODULE_ERR_LOG}
cat /dev/null >${MODULE_ERR_LOG}

backupModule() {

    logDeploy "开始对当前版本进行备份"

    backupTime=`date '+%Y%m%d-%H%M%S'`
    mkdir -p ${BACKUP_DIR}/$backupTime-$deployId/code
    mkdir -p ${BACKUP_DIR}/$backupTime-$deployId/conf
    # jar/war文件
    if [ -d ${MODULE_DIR} ];then
        hasFile=`ls ${MODULE_DIR} | wc -l`
        if [ $hasFile -gt 0 ];then
           cp -rf ${MODULE_DIR}/* ${BACKUP_DIR}/$backupTime-$deployId/code
           #删除pid文件，避免回滚的时候失败
           rm -f ${BACKUP_DIR}/$backupTime-$deployId/code/${PID_FILE}
        fi
    fi
    # 配置文件
    if [ $webProjectFlag -eq 1 ];then
        hasFile=`ls ${RESIN_CONF_DIR} | wc -l`
        if [ $hasFile -gt 0 ];then
            cp -r ${RESIN_CONF_DIR}/* ${BACKUP_DIR}/$backupTime-$deployId/conf
        fi
    fi
    cd ${BACKUP_DIR}
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
        exit 1
    fi
    logDeploy "备份完成"
}

decompressModule() {
    mkdir -p ${MODULE_DIR}
    cd ${MODULE_DIR}
    ls |grep -v ${PID_FILE} | xargs rm -rf
    cd ..
    tar zxvf ${MODULE_TAR_FILE}
    decompressResult=$?
    if [ $decompressResult -ne 0 ]; then
        logDeploy "解压失败"
        exit 1
    fi
    logDeploy "解压完成"
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

execRestartShell() {
    backupGCFile
    cd ${MODULE_DIR}
    deployResult=0
    if [ $webProjectFlag -eq 1 ]; then
            ${RESTART_SHELL}
            deployResult=$?
        else
            # 获取 dubbo服务 执行脚本过程的信息，看重启是否用到了kill -9
            cat /dev/null >/tmp/${PROJECT_NAME}_${MODULE_NAME}_restartInfo.log
            `${RESTART_SHELL} >/tmp/${PROJECT_NAME}_${MODULE_NAME}_restartInfo.log 2>&1`
            deployResult=$?
            cat /tmp/${PROJECT_NAME}_${MODULE_NAME}_restartInfo.log |  while read restartInfo;
            do
                logDeploy "$restartInfo"
            done
    fi

    if [ $deployResult -ne 0 ]; then
        logDeploy "重启失败"
        echo -e "重启失败"
        exit 1
      else
        logDeploy "重启完成"
    fi
}

# 检测是否是120S内有重复操作，逻辑:检测shell脚本的pid是否存在，存在的话，判断是不是120内，不是则执行重启，是则放弃重启
# 发布系统发布的时候 ，不管拉起脚本是否在拉起，直接覆盖，避免拉起脚本的代码不是最新的
checkIsRestarting() {
    # shell脚本执行所属的pid
    PID="/tmp/${PROJECT_NAME}_${MODULE_NAME}_pull.pid"
    if [[ -f "$PID" ]]; then
            PIDS=`cat /tmp/${PROJECT_NAME}_${MODULE_NAME}_pull.pid`
            Timestamp=`date +%s`
            ps -e|grep -vsE "grep"|grep -sE "^${PIDS} | ${PIDS} " 2>&1 >/dev/null
            if [ $? != 0 ]; then
                rm -f /tmp/${PROJECT_NAME}_${MODULE_NAME}_pull.pid
                echo "`date +"%F %T"` Process Find ${PID} not exist,rm -f ${PID},create $$" >> ${MODULE_ERR_LOG} >/dev/null 2>&1
                echo "$$" > "$PID"
            else
                echo "`date +"%F %T"` Process Find ${PID} exist,EXIT" >> ${MODULE_ERR_LOG} >/dev/null 2>&1
                exit -1
            fi
            PidFileModTime=`stat -c %Y ${PID}`
            if (($Timestamp > $PidFileModTime + 120)); then
                echo -e "`date +"%F %T"` $0 is running Over 120s" >> ${MODULE_ERR_LOG}
            fi
    else
            echo "$$" > "$PID"
    fi
}

execStopShell() {
    ${STOP_SHELL}
    stopResult=$?
    if [ $stopResult -ne 0 ]; then
        logDeploy "stop失败"
      else
        logDeploy "stop完成"
    fi
}

execPostShell() {
    # 判断是否有 postShell, 1 代表有, 0 没有
    hasPostShell=${HAS_POSTSHELL}
    if [ $hasPostShell -eq 1 ]; then
       ${POST_SHELL}
       if [ $? -ne 0 ]; then
            logDeploy "执行启动后脚本失败"
          else
            logDeploy "执行启动后脚本完成"
       fi
    fi
}

# 如果是web项目,将含有 oss文件 + resin配置文件的tar包解压,将配置文件放到对应目录
prepareModuleFile() {
    if [ $webProjectFlag -eq 1 ]; then
        mkdir -p ${MODULE_DIR}
        cd ${MODULE_DIR}
        mv ${MODULE_TAR_FILE} ../
        # 移动resin配置文件
        mkdir -p  ${RESIN_CONF_DIR}
        mv ${CONFIG_FILE_LIST} -t ${RESIN_CONF_DIR}
    fi
}

startupModule() {
    backupGCFile

    logDeploy "开始重启"
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

      decompressModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      startupModule

      checkProcessOn 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo
       #等待3秒后disconf等配置文件被删除后再备份
      sleep 3

      # 这里针对当前版本进行备份
      backupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo
}

# 备份 GC 文件
backupGCFile() {
    if [ -f "${GC_LOG_DIR}/gc.log" ]; then
        CURTIME=$(date +%Y-%m-%d-%H%M%S)
        BAK_GCFILE="${GC_LOG_DIR}/gc.log.${CURTIME}"
        cp ${GC_LOG_DIR}/gc.log $BAK_GCFILE
    fi
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

rollBackModuleFile() {
    cd ${BACKUP_DIR}
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
    # 恢复 jar/war , dubbo服务的需要保留pid，
    if [ $webProjectFlag -eq 1 ];then
            rm -rf ${MODULE_DIR}/*
        else
            ls | grep -v '${PID_FILE}' | xargs rm -rf
    fi

    \cp -rf ${BACKUP_DIR}/$rollBackVersion/code/* ${MODULE_DIR}
    # 恢复resin配置文件
     if [ $webProjectFlag -eq 1 ];then
        \cp -rf ${BACKUP_DIR}/$rollBackVersion/conf/* ${RESIN_CONF_DIR}
     fi
}

checkProcessOn() {


    logDeploy "开始检测进程是否启动"
    # 避免服务启动延迟
    sleep 2
    # dubbo服务监测进程是否存在
    if [ $webProjectFlag -eq 2 ];then
        dubboPid=`ps -ef | grep java | grep ${BASE_PROJECT_DIR}${PROJECT_NAME} | grep '/${MODULE_NAME}/*' | awk '{print $2}'`
        if [ ! -n "$dubboPid" ]; then
            logDeploy "服务进程启动失败"
            exit 1
          else
            logDeploy "检测到进程已启动"
        fi
    fi
    # web项目检测子进程是否起来
    if [ $webProjectFlag -eq 1 ];then
        resinPid=`ps -ef | grep java | grep resin | grep ${RESIN_XML_FILE} | grep -v 'watchdog=${MODULE_NAME}' | awk '{print $2}'`
        if [ ! -n "$resinPid" ]; then
            logDeploy "进程启动失败"
            exit 1
          else
            logDeploy "检测到进程已启动"
        fi
    fi
}


case "$deployType" in
deploy)
      logDeploy "开始发布"
      deployModule
;;
restart)
      #检测是否同一个项目同时在启动
      checkIsRestarting
      logDeploy "开始重启"

      startupModule 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      checkProcessOn 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo
;;
stop)
    execStopShell 1> /dev/null 2>${MODULE_ERR_LOG}
    logErrorInfo
;;
rollBack)
      logDeploy "开始回滚"

      logDeploy "开始回滚代码和配置"
      rollBackModuleFile

      logDeploy "开始重启"
      execRestartShell

      checkProcessOn 1> /dev/null 2>${MODULE_ERR_LOG}
      logErrorInfo

      logDeploy "回滚完成"
;;
*)
      exit 1
;;
esac
