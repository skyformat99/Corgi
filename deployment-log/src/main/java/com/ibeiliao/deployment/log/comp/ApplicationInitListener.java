package com.ibeiliao.deployment.log.comp;

import com.ibeiliao.deployment.log.service.LogSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 功能: 容器初始化
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/6
 */
@Component
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitListener.class);

    @Autowired
    private LogSyncService logSyncService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("===容器初始化===");
        logger.info("启动同步日志线程...");
        logSyncService.startupSyncShellLog();
    }
}
