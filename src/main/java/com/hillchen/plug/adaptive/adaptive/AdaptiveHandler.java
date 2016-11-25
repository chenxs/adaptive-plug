package com.hillchen.plug.adaptive.adaptive;

import java.util.List;

/**
 * Created by hillchen on 2016/11/24.
 */
public class AdaptiveHandler {
    public String adaptiveTest(String name,String id){
        return name+","+id;
    }

    public void voidHandlerTest(int c,int num){
        return;
    }

    public String arrayTestHandler(String[] args){
        if(args == null){
            return "";
        }else{
            StringBuilder stringBuilder = new StringBuilder();
            for(String arg : args){
                stringBuilder.append(arg).append("|");
            }
            return stringBuilder.toString();
        }
    }

    public List<String> listTestHandler(List<String> args){
        return args;
    }
}
