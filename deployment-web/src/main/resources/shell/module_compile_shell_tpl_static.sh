#!/bin/bash
#
# name:   纯静态项目，没有模块概念
# author: 梁广龙
# date:   2017/02/26
#
. /etc/profile

LOG_FILE=${LOG_FILE_DIR}
rm -f ${LOG_FILE}
mkdir -p ${PROJECT_LOG_DIR}

checkoutBranch() {
    rm -rf ${BRANCH_DIR}
    mkdir -p ${BRANCH_DIR}
    ${CHECKOUT_SHELL}
    checkoutResult=$?
    if [ $checkoutResult -ne 0 ]; then
        echo 'checkout代码出错，退出'
        rm -rf ${BRANCH_DIR}
        exit 1
    fi
}

generateTarAndMd5() {
    # tar编译后的整个分支 和 生成md5
    cd ${BRANCH_DIR}
    tar -cf ../${BRANCH_TAR_FILE} *
    cd ..
    md5sum ${BRANCH_TAR_FILE} > ${BRANCH_MD5_FILE}
    # 存放到storage目录
    cp ${BRANCH_TAR_FILE} ${LOCAL_STORAGE_DIR}/${SAVE_FILE_NAME}
}


checkoutBranch
generateTarAndMd5

echo '编译结束，shell退出' >> ${LOG_FILE}
rm -rf ${BRANCH_DIR}
exit 0
