#!/bin/bash
#
# name:   模块编译脚本,包括checkout、编译、压缩
# author: 梁广龙
# date:   2017/02/26
#
source /etc/profile
LOG_FILE=${LOG_FILE_DIR}
rm -f ${LOG_FILE}
rm -rf ${BRANCH_DIR}
mkdir -p ${BRANCH_DIR}
yes 2>/dev/null | svn co ${SVN_URL} ${BRANCH_DIR} --username ${SVN_ACCOUNT} --password ${SVN_PASSWORD}>${SVN_LOG_DIR}
svnCheckoutResult=$?
if [ $svnCheckoutResult -ne 0 ]; then
    echo 'checkout代码出错，退出'>> ${LOG_FILE}
    exit 1
fi

mkdir -p ${BRANCH_DIR}/${MODULE_NAME}
mkdir -p ${COMPILED_FILE_DIR}
cd ${BRANCH_DIR}
mkdir -p ${PROJECT_LOG_DIR}
echo '创建compileLogDir/env/project 目录成功'>> ${LOG_FILE}
${PYTHON_COLLECT_LOG}
echo '准备开始编译 ...'>> ${LOG_FILE}
${MVN_SHELL} > ${COMPILE_LOG}
mvnCompileResult=$?
if [ $mvnCompileResult -ne 0 ]; then
    echo 'mvn编译出错，退出'>> ${LOG_FILE}
    exit 1
fi
echo '编译结束 ...'>> ${LOG_FILE}

${MVN_CP_SHELL}
 copyResult=$?
if [ $copyResult -ne 0 ]; then
    echo 'mvn编译出错，退出'>> ${LOG_FILE}
    exit 1
fi

echo '准备执行tar ...'>> ${LOG_FILE}
mkdir -p ${COMPILE_PROJECT_DIR}
cd ${COMPILE_PROJECT_DIR}
tar zcvf ${MODULE_TAR_FILE} ${MODULE_NAME}/
tarFileResult=$?
echo 'tar打包完成'>> ${LOG_FILE}
rm -rf ${MODULE_NAME}
if [ $tarFileResult -ne 0 ]; then
    echo "tar打包失败: ${tarFileResult}，退出">> ${LOG_FILE}
    exit 1
fi

echo $?
echo '编译结束，shell退出' >> ${LOG_FILE}
exit 0
