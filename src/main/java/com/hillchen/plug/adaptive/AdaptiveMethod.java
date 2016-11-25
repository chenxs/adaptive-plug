package com.hillchen.plug.adaptive;


import com.hillchen.plug.adaptive.convertor.SimplePramsConvertor;
import com.hillchen.plug.adaptive.convertor.SimpleResultTypeConvertor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2016/11/23.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdaptiveMethod {
    String method() default "";
    Class[] pramClazzs() default {};
    Class pramsConvertor() default SimplePramsConvertor.class;
    Class resultTypeConvertor() default SimpleResultTypeConvertor.class;

}
