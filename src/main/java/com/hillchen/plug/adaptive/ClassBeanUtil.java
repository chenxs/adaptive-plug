package com.hillchen.plug.adaptive;

/**
 * Created by hillchen on 2016/11/25.
 */
public interface ClassBeanUtil  {
    <T> T createBean(Class<T> clazz);
}
