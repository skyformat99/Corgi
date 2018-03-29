#!/bin/bash
#
# name:  dubbo服务启动脚本模板
# author: 梁广龙
# date:    2017/02/26
#
source /etc/profile
# BASE_DIR 获取的是脚本的执行路径
BASE_DIR=$(cd `dirname $0`; pwd)
RUN_DIR=
LOG_DIR=${MODULE_LOG_DIR}
profile=${PROJ_PROFILE}

#### 需要运行的 Main类全路径；以及参数
BASE_DIR=${PROJECT_DIR}
_RUNCLASS=${MAIN_CLASS}
JAVA_OPTS="${JVM_ARGS}"
RUN_DIR=${BASE_DIR}/${MODULE_NAME}
RUN_JAR=
RUN_LIBS=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib/rt.jar:$RUN_DIR/*
model=${MODULE_NAME}

#### 运行java的基本参数："java 启动参数 classpath"
CLASSPATH=$RUN_LIBS
_RUNJAVA="$JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASSPATH"
SERVICE_PID=${MODULE_PID_FILE}
SERVICE_OUT=${LOG_DIR}/app.log
mkdir -p ${LOG_DIR}
chmod a+w ${LOG_DIR}
touch ${SERVICE_PID}
chmod a+w ${SERVICE_PID}
# 启动脚本的方法
start() {
  #### 如果PID文件存在，那么尝试：
  #### 1：读取文件中的pid，并查看进程是否存在，存在则停止启动
  #### 2：进程不存在，则尝试删除或者清空文件内容，失败则停止启动
  if [ ! -z "$SERVICE_PID" ]; then
    if [ -f "$SERVICE_PID" ]; then
      if [ -s "$SERVICE_PID" ]; then
        if [ -r "$SERVICE_PID" ]; then
          PID=`cat "$SERVICE_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "$model appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            rm -f "$SERVICE_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$SERVICE_PID" ]; then
                cat /dev/null > "$SERVICE_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "pid文件不可读,启动终止"
          exit 1
        fi
      else
        rm -f "$SERVICE_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$SERVICE_PID" ]; then
            echo "删除 或者写不进这个 空的pid文件, 启动终止."
            exit 1
          fi
        fi
      fi
    fi
  fi
    echo "starting ${MODULE_NAME}........."
    exec $_RUNJAVA $_RUNCLASS $_RUNPARAM $* >> $SERVICE_OUT 2>&1 &
    if [ ! -z "$SERVICE_PID" ]; then
      echo $! > "$SERVICE_PID"
    fi
    echo "$model started. see ${SERVICE_OUT}"
}
####
#### 停止脚本方法
#### STOP_ERR_EXIT用于指示，stop过程中‘出现问题’时是否退出[0,1]（异常包括：没有>指定pid，或pid不存在 等等）
####
STOP_ERR_EXIT=1
FORCE=0
stop() {
  #### 检查PID任务是否正在运行
  if [ ! -z "$SERVICE_PID" ]; then
    if [ -f "$SERVICE_PID" ]; then
      if [ -s "$SERVICE_PID" ]; then
        kill -0 `cat "$SERVICE_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          if [ $STOP_ERR_EXIT -eq 0 ]; then
            return
          fi
          echo "pid文件存在但是找不到对应的进程,停止stop"
          exit 1
        fi
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      if [ $STOP_ERR_EXIT -eq 0 ]; then
        return
      fi
      echo "$model process does not exist. Stop aborted."
      exit 1
    fi
  fi
  echo "stop $model ......"
  kill -15 `cat $SERVICE_PID`
  SLEEP=3
  if [ ! -z "$SERVICE_PID" ]; then
    SLEEP=3
    if [ -f "$SERVICE_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$SERVICE_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$SERVICE_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$SERVICE_PID" ]; then
              cat /dev/null > "$SERVICE_PID"
            else
              echo "pid文件没有清空"
            fi
          fi
          echo "${MODULE_NAME} 已经停止"
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          if [ $FORCE -eq 0 ]; then
            PID=`cat "$SERVICE_PID"`
            echo "${MODULE_NAME} Can't stop, please handle. PID: $PID"
          fi
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi
}

case "$1" in
start)
      start
;;
stop)
      STOP_ERR_EXIT=1
      stop
;;
restart)
      STOP_ERR_EXIT=0
      stop
      sleep 3
      start
 ;;
*)
      exit 1
;;
esac
