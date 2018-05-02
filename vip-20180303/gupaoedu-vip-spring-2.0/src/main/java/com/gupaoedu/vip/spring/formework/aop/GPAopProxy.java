package com.gupaoedu.vip.spring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Tom on 2018/5/2.
 */
//默认就用JDK动态代理
public class GPAopProxy implements InvocationHandler{

    private GPAopConfig config;
    private Object target;

    //把原生的对象传进来
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    public void setConfig(GPAopConfig config){
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method m = this.target.getClass().getMethod(method.getName(),method.getParameterTypes());



        //在原始方法调用以前要执行增强的代码
        //这里需要通过原生方法去找，通过代理方法去Map中是找不到的
        if(config.contains(method)){
           GPAopConfig.GPAspect aspect = config.get(method);
           aspect.getPoints()[0].invoke(aspect);
        }

        //反射调用原始的方法
        Object obj = method.invoke(this.target,args);

        //在原始方法调用以后要执行增强的代码
        if(config.contains(method)){
            GPAopConfig.GPAspect aspect = config.get(method);
            aspect.getPoints()[1].invoke(aspect);
        }

        //将最原始的返回值返回出去
        return obj;
    }
}
