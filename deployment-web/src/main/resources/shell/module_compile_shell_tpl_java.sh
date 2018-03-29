#!/bin/bash
#
# name:   模块编译脚本,包括checkout、编译、压缩
# author: 梁广龙
# date:   2017/02/26
#
. /etc/profile

LOG_FILE=${LOG_FILE_DIR}
rm -f ${LOG_FILE}

# 编译日志 路径
mkdir -p ${PROJECT_LOG_DIR}
touch ${COMPILE_LOG}
cat /dev/null > ${COMPILE_LOG}
${PYTHON_COLLECT_LOG}
# 是否需要编译, 1是, 0不是
needCompile=1
checkNeedCompile() {
    mkdir -p ${PROJECT_DIR}${BRANCH_TYPE_NAME}
    cd ${PROJECT_DIR}${BRANCH_TYPE_NAME}
    # 1. 检测 tar文件和MD5文件 branch/tags_分支名_版本号.tar 和 branch/tags_分支名_版本号.md5 ()
    if [ -f ${BRANCH_TAR_FILE} -a  -f ${BRANCH_MD5_FILE} ]; then
        md5sum -c ${BRANCH_MD5_FILE}
        if [ $? == 0 ]; then
            # 解压
            mkdir -p ${BRANCH_DIR}
            tar -xf ${BRANCH_TAR_FILE} -C ./
            copyCompiledFile
            needCompile=0
        else
            rm -f ${BRANCH_TAR_FILE} ${BRANCH_MD5_FILE}
        fi
    fi
}

checkoutBranch() {
    rm -rf ${BRANCH_DIR}
    mkdir -p ${BRANCH_DIR}
    ${CHECKOUT_SHELL}
    checkoutResult=$?
    if [ $checkoutResult -ne 0 ]; then
        echo 'checkout代码出错，退出'>> ${COMPILE_LOG}
        rm -rf ${BRANCH_DIR}
        exit 1
    fi
}

compileModule() {

    mkdir -p ${BRANCH_DIR}/${MODULE_NAME}
    mkdir -p ${COMPILED_FILE_DIR}
    # 判断是否是单个模块的项目, 是则 进入 分支名/模块名/
    if [ -e ${BRANCH_DIR}/pom.xml ]; then
       cd ${BRANCH_DIR}
    else
       cd ${BRANCH_DIR}/${MODULE_NAME}
       if [ ! -e ${BRANCH_DIR}/${MODULE_NAME}/pom.xml ]; then
          exit 1
       fi
    fi

    mkdir -p ${PROJECT_LOG_DIR}
    echo '创建compileLogDir/env/project 目录成功'>> ${LOG_FILE}

    # 如果不是静态项目就编译
    echo '准备开始编译 ...'>> ${LOG_FILE}
    ${MVN_SHELL} > ${COMPILE_LOG}
    mvnCompileResult=$?
    if [ $mvnCompileResult -ne 0 ]; then
        echo 'mvn编译出错，退出'>> ${LOG_FILE}
        rm -rf ${BRANCH_DIR}
        exit 1
    fi
    echo '编译结束 ...'>> ${LOG_FILE}
    copyCompiledFile
}

copyCompiledFile() {
    mkdir -p ${COMPILED_FILE_DIR}
    ${MVN_CP_SHELL}
    copyResult=$?
    if [ $copyResult -ne 0 ]; then
        echo 'mvn编译出错，退出'>> ${LOG_FILE}
        rm -rf ${BRANCH_DIR}
        exit 1
    fi

}
generateTarAndMd5() {
    # tar编译后的整个分支 和 生成md5
    cd ${BRANCH_DIR}
    cd ..
    rm -f ${BRANCH_TYPE_NAME}_${SHORT_BRANCH_NAME}_*.tar ${BRANCH_TYPE_NAME}_${SHORT_BRANCH_NAME}_*.md5
    tar -cf ${BRANCH_TAR_FILE} ${SHORT_BRANCH_NAME}
    md5sum ${BRANCH_TAR_FILE} > ${BRANCH_MD5_FILE}
}

tarCompiledFile() {
    echo '准备执行tar ...'>> ${LOG_FILE}
    mkdir -p ${COMPILE_PROJECT_DIR}
    cd ${COMPILE_PROJECT_DIR}
    tar zcvf ${MODULE_TAR_FILE} ${MODULE_NAME}/ > /dev/null
    tarFileResult=$?
    echo 'tar打包完成'>> ${LOG_FILE}
    rm -rf ${MODULE_NAME}
    if [ $tarFileResult -ne 0 ]; then
        echo "tar打包失败: ${tarFileResult}，退出">> ${LOG_FILE}
        rm -rf ${BRANCH_DIR}
        exit 1
    fi
    # 将编译好的打包文件保存到存储的地方 /data/storage/{name}, 名字是跟oss上传的名字一样
    mkdir -p ${LOCAL_STORAGE_DIR}
    mv ${MODULE_TAR_FILE} ${LOCAL_STORAGE_DIR}/${SAVE_FILE_NAME}
}

forceCompile=${FORCE_COMPILE}
checkNeedCompile
if [ $needCompile == 1 ] || [ $forceCompile == 1 ]; then
        checkoutBranch
        compileModule
        generateTarAndMd5
    else
       echo "检测到已经存在编译好的文件,直接跳过编译" > ${COMPILE_LOG}
fi
tarCompiledFile

echo '编译结束，shell退出' >> ${LOG_FILE}
rm -rf ${BRANCH_DIR}
exit 0
