package com.hillchen.plug.adaptive;
import com.hillchen.plug.adaptive.adaptive.AdaptiveConfig;
import com.hillchen.plug.adaptive.compiler.SimpleJdkCompiler;
import com.hillchen.plug.adaptive.convertor.PramsConvertor;
import com.hillchen.plug.adaptive.convertor.ResultTypeConvertor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/23.
 */
public class AdaptiveServiceFactory {

    private final String adaptiveName = "adaptiver";
    private final String adaptiveCaseName = "adaptiverCase";
    private final String adaptiveReturn = "adaptiveReturn";
    private final String interfacePramsName = "interfacePrams";
    private final String adaptivePramsName = "adaptivePrams";
    private final String convertorReturn = "convertorReturn";

    private static final Map<Class,Class> primitive2BoxClassMap = new HashMap<Class, Class>();
    static{
        primitive2BoxClassMap.put(boolean.class,Boolean.class);
        primitive2BoxClassMap.put(char.class,Character.class);
        primitive2BoxClassMap.put(short.class,Short.class);
        primitive2BoxClassMap.put(int.class,Integer.class);
        primitive2BoxClassMap.put(long.class,Long.class);
        primitive2BoxClassMap.put(float.class,Float.class);
        primitive2BoxClassMap.put(double.class,Double.class);
    }

    private SimpleJdkCompiler compiler = new SimpleJdkCompiler();

    private ClassBeanUtil beanUtil;

    public ClassBeanUtil getBeanUtil() {
        return beanUtil;
    }

    public void setBeanUtil(ClassBeanUtil beanUtil) {
        this.beanUtil = beanUtil;
    }

    public <T>T createAdaptive(Class<? extends T> configClazz){
        try {
            AdaptiveInit adaptiveInit = compilerAdaptiver(configClazz);
            Object adaptiver = getAdaptiveRef(configClazz.getAnnotation(Adaptiver.class));
            adaptiveInit.setAdaptiver(adaptiver);
            return (T)adaptiveInit;
        } catch (Exception e) {
            return null;
        }
    }

    private Object getAdaptiveRef(Adaptiver adaptiver){
        try {
            return getBeanUtil().createBean(adaptiver.value());
        } catch (Throwable e) {
            throw new RuntimeException("get adaptive ref error !",e);
        }
    }

    private AdaptiveInit compilerAdaptiver(Class configClazz) throws Exception {
        Class configAdaptiveClazz = compiler.compile(codeAdaptiver(configClazz),AdaptiveServiceFactory.class.getClassLoader());
        return (AdaptiveInit)configAdaptiveClazz.newInstance();
    }

    private String codeAdaptiver(Class configClazz) throws Exception {
        if(configClazz.isAnnotationPresent(Adaptiver.class)){
            Adaptiver adaptiver = (Adaptiver) configClazz.getAnnotation(Adaptiver.class);
            Class adaptiveClazz = adaptiver.value();
            StringBuilder codeBuilder = new StringBuilder();
            codeAdaptivePackage(configClazz,codeBuilder);
            codeClassHandle(configClazz,codeBuilder);
            codeBuilder.append("{\n");
            codeConstructor(codeBuilder,configClazz);
            codeAdaptiverPram(codeBuilder);
            codeAdaptiverMethods(adaptiveClazz,configClazz,codeBuilder);
            codeBuilder.append("\n}");
            return codeBuilder.toString();
        }else{
            return "";
        }

    }

    private void codeAdaptivePackage(Class configClazz, StringBuilder codeBuilder){
        codeBuilder.append("package ").append(configClazz.getPackage().getName()).append(";");
    }

    private void codeClassHandle(Class configClazz,StringBuilder codeBuilder){
        codeBuilder.append("\npublic class ").append(configClazz.getSimpleName()).append("$Adaptive").append(" ");
        if(configClazz.isInterface()){
            codeBuilder.append("implements ").append(configClazz.getName()).append(",").append(AdaptiveInit.class.getName());
        }else{
            codeBuilder.append("extends ").append(configClazz.getName()).append(" implements ").append(AdaptiveInit.class.getName());
        }
    }

    private void codeConstructor(StringBuilder codeBuilder,Class configClazz){
        codeBuilder.append("\npublic ").append(configClazz.getSimpleName()).append("$Adaptive").append("(){}");
    }

    private void codeAdaptiverPram(StringBuilder codeBuilder){
        codeBuilder.append("\nprivate Object ").append(adaptiveName).append(" = null;");
        codeBuilder.append("\npublic void setAdaptiver(Object arg){\nthis.").append(adaptiveName).append(" = arg;\n}");
    }

    private void codeAdaptiverMethods(Class adaptiveClazz,Class configClazz,StringBuilder codeBuilder) throws Exception {
        Method[] methods = configClazz.getMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(AdaptiveMethod.class)){
                codeBuilder.append("\n");
                AdaptiveMethod adaptiveMethod = method.getAnnotation(AdaptiveMethod.class);
                codeAdaptiveMethod(adaptiveMethod,method,adaptiveClazz,codeBuilder);
            }
        }
    }

    private void codeAdaptiveMethod(AdaptiveMethod adaptiveMethod, Method interfaceMethod,Class adaptiveClazz,StringBuilder codeBuilder)throws Exception{
        Class pramsConvertorClazz = adaptiveMethod.pramsConvertor();
        Class returnConvertorClazz = adaptiveMethod.resultTypeConvertor();
        String adaptiveMethodName = adaptiveMethod.method();
        Class[] adaptivePramClazzs = adaptiveMethod.pramClazzs();
        Method adaMethod = adaptiveClazz.getMethod(adaptiveMethodName,adaptivePramClazzs);
        List<String> pramNames = codeMethodHandle(interfaceMethod, codeBuilder);
        codeBuilder.append("{\n").append("try{");
        codeInitPrams(pramNames,codeBuilder);
        codePramsConvertor(pramsConvertorClazz,codeBuilder);
        codeAdaptiveInvok(adaptiveClazz,adaMethod,codeBuilder);
        codeReturnConvertor(interfaceMethod.getReturnType(),returnConvertorClazz,codeBuilder);
        codeBuilder.append("\n}catch(Throwable e){\nthrow new RuntimeException(\"adaptive handle error!\",e);\n}").append("\n}");
    }

    private List<String> codeMethodHandle(Method interfaceMethod, StringBuilder codeBuilder) {
        Class returnType = interfaceMethod.getReturnType();
        String methodName = interfaceMethod.getName();
        Class[] pramsClazzs = interfaceMethod.getParameterTypes();
        Class[] throwClazzs = interfaceMethod.getExceptionTypes();
        List<String> pramNames = new ArrayList<String>();
        codeBuilder.append("public ")
                .append(returnType.getCanonicalName()).append(" ")
                .append(methodName).append("(");
        int i=0;
        for(Class pramClazz : pramsClazzs){
            if(i!=0){
                codeBuilder.append(",")  ;
            }
            String pramName ="arg"+i;
            pramNames.add(pramName);
            codeBuilder.append(pramClazz.getCanonicalName()).append(" ").append(pramName);
            i++;
        }
        codeBuilder.append(")");
        if(throwClazzs!=null&&throwClazzs.length>0){
            i=0;
            codeBuilder.append("throws ");
            for(Class throwClazz : throwClazzs){
                if(i!=0){
                    codeBuilder.append(",")  ;
                }
                codeBuilder.append(throwClazz.getCanonicalName());
                i++;
            }
        }
        return pramNames;
    }
    private void codeInitPrams(List<String> pramNames,StringBuilder codeBuilder){
        codeBuilder.append("\nObject[] ").append(interfacePramsName).append(" = new Object[]{");
        int i = 0;
        for(String pramName : pramNames) {
            if(i>0){codeBuilder.append(",");}
            codeBuilder.append(pramName);
            i++;
        }
        codeBuilder.append("};");
    }
    private void codePramsConvertor(Class<? extends PramsConvertor> pramsConvertorClazz, StringBuilder codeBuilder){
        codeBuilder.append("\nObject[] ").append(adaptivePramsName).append(" = new ")
                .append(pramsConvertorClazz.getCanonicalName()).append("().convertor")
                .append("(").append(interfacePramsName).append(");");
    }
    private void codeAdaptiveInvok(Class<?> adaptiveClazz,Method adaptiveMathod, StringBuilder codeBuilder){

        Class[] adaptiveParameterTypes = adaptiveMathod.getParameterTypes();
        codeBuilder.append("\nif(adaptivePrams.length != ").append(adaptiveParameterTypes.length).append("){throw new RuntimeException(\"convertor adaptive method args error!\");}");

        String adaptiveClazzName = adaptiveClazz.getName();
        codeBuilder.append("\n").append(adaptiveClazzName).append(" ").append(adaptiveCaseName).append(" = ")
                .append("(").append(adaptiveClazzName).append(")").append(adaptiveName).append(";");

        Class adaptiveReturnClazz = adaptiveMathod.getReturnType();
        String adaptiveMethodName = adaptiveMathod.getName();

        codeBuilder.append("\n");
        if(!adaptiveReturnClazz.equals(void.class)){
            codeBuilder.append(adaptiveReturnClazz.getName()).append(" ").append(adaptiveReturn).append(" = ");
        }
        codeBuilder.append(adaptiveCaseName).append(".").append(adaptiveMethodName).append("(");

        int i = 0;
        for(Class parameterTypeClazz : adaptiveParameterTypes) {
            if(i>0){codeBuilder.append(",");}
            codeCasePram(parameterTypeClazz,adaptivePramsName,i,codeBuilder);
            i++;
        }
        codeBuilder.append(");");
    }

    private void codeReturnConvertor(Class returnClazz, Class<? extends ResultTypeConvertor> pramsConvertorClazz, StringBuilder codeBuilder){
        if(!returnClazz.equals(void.class)){
            codeBuilder.append("\nObject ").append(convertorReturn).append(" = new ")
                    .append(pramsConvertorClazz.getName()).append("().convertor(")
                    .append(adaptiveReturn).append(");");
            codeBuilder.append("\nreturn ( ").append(returnClazz.getName()).append(")")
                    .append(convertorReturn).append(";");
        }

    }

    private void codeCasePram(Class parameterTypeClazz,String paramsName,int paramIndex,StringBuilder codeBuilder){
        String paramName = paramsName+"["+paramIndex+"]";
        if(parameterTypeClazz.isPrimitive()){
            codeBuilder.append("(").append(primitive2BoxClassMap.get(parameterTypeClazz).getName()).append(")")
              .append(paramName);
        }else if(parameterTypeClazz.isArray()){
            String arrayName = getArrayClassName(parameterTypeClazz);
            codeBuilder.append("(").append(arrayName).append(")")
                    .append(paramName);
        }else{
            codeBuilder.append("(").append(parameterTypeClazz.getName()).append(")")
                    .append(paramName);
        }
    }

    private String getArrayClassName(Class arrayClazz){
        String arrayName = arrayClazz.getName();
        return arrayName.replace("[L","").replace(";","")+"[]";
    }

    public static void main(String[] args) throws Exception {
        AdaptiveServiceFactory factory = new AdaptiveServiceFactory();
        factory.setBeanUtil(new SimpleClassBeanUtil());
        AdaptiveConfig adaptiver = factory.createAdaptive(AdaptiveConfig.class);
        Assert.isTrue(adaptiver.compileTest("hill","chen").equals(adaptiver.otherMehod("hill","chen")));
        System.out.println(adaptiver.compileTest("hill","chen"));
        System.out.println(adaptiver.arrayTest(new String[]{"hill","chen"}));
        List<String> arrs = new ArrayList<String>();
        arrs.add("hill");
        arrs.add("chen");
        System.out.println(adaptiver.listTest(arrs));
    }


}
