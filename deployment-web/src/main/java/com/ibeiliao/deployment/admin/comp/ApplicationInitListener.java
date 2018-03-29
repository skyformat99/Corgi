package com.ibeiliao.deployment.admin.comp;

import com.ibeiliao.deployment.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 功能: 应用程序初始化
 * <p>
 * 详细:
 *
 * @author jingyesi   17/2/6 modified by linyi
 */
@Component
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitListener.class);

    private SubscribeLogThread subscribeLogThread = null;

    private PushStepLogToClientThread pushStepLogToClientThread = null;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        logger.info("各线程初始化开始....");
        // 订阅信息会阻塞, 采用异步线程初始化
        subscribeLogThread = new SubscribeLogThread(event.getApplicationContext());
        subscribeLogThread.start();

        pushStepLogToClientThread = new PushStepLogToClientThread(event.getApplicationContext());
        pushStepLogToClientThread.start();


        logger.info("各线程初始化完成");
        // 触发配置加载
        logger.info("oss bucket: " + Configuration.getOssBucket() + ", endpoint: " + Configuration.getOssEndpoint());
    }

    @PreDestroy
    public void destroy() {
        if (subscribeLogThread != null) {
            try {
                subscribeLogThread.stopThread();  // 防止 unsubscribe() 可能出现异常
                logger.error("停止线程成功 " + subscribeLogThread.getName());
            } catch (Exception e) {
                logger.error("停止线程失败 " + subscribeLogThread.getName(), e);
            }
        }
        if (pushStepLogToClientThread != null) {
            pushStepLogToClientThread.stopThread();
        }
    }
}
