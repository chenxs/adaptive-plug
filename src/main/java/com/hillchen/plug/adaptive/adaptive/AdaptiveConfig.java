package com.hillchen.plug.adaptive.adaptive;


import com.hillchen.plug.adaptive.AdaptiveMethod;
import com.hillchen.plug.adaptive.Adaptiver;

import java.util.List;

/**
 * Created by hillchen on 2016/11/24.
 */
@Adaptiver(AdaptiveHandler.class)
public abstract class AdaptiveConfig {

    @AdaptiveMethod(method = "adaptiveTest",pramClazzs = {String.class,String.class})
    public abstract String compileTest(String name,String id);

    @AdaptiveMethod(method = "voidHandlerTest",pramClazzs = {int.class,int.class})
    public abstract void voidMethodTest(int id,int num);

    public String otherMehod(String name,String id){
        return compileTest(name,id);
    }

    @AdaptiveMethod(method = "arrayTestHandler",pramClazzs = {String[].class})
    public abstract String arrayTest(String[] agrs);

    @AdaptiveMethod(method = "listTestHandler",pramClazzs = {List.class})
    public abstract List<String> listTest(List<String> args);


}
