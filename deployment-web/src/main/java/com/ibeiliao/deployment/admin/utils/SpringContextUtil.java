package com.ibeiliao.deployment.admin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring context util 获得 Spring context 的 context
 *
 * @author tendy
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    private static ApplicationContext context = null;

    public static ApplicationContext getContext() {
        return context;
    }

    public static Object getBean(String beanId) {
        try {
            return context.getBean(beanId);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextUtil.context = context;
        logger.info("context 设置成功");
    }

    public static void autowireBeanPropertiesByName(Object existingBean) {
        getContext().getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
    }

    public static void autowireBeanPropertiesByType(Object existingBean) {
        getContext().getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    }
}