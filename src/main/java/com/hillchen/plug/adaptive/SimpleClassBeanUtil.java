package com.hillchen.plug.adaptive;

/**
 * Created by hillchen on 2016/11/25.
 */
public class SimpleClassBeanUtil implements ClassBeanUtil {
    @Override
    public <T> T createBean(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            return null;
        }
    }
}
