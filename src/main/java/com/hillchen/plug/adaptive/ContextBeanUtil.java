package com.hillchen.plug.adaptive;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by hillchen on 2016/11/22.
 */
public class ContextBeanUtil implements ClassBeanUtil {
    @Override
    public <T> T createBean(Class<T> clazz) {
        return null;
    }

    /*private static ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public  <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    public  Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public  <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    @Override
    public <T> T createBean(Class<T> clazz) {
        return getBean(clazz);
    }*/
}
