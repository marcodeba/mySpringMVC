package com.gupaoedu.vip.spring.formework.beans;

//用做事件监听的
public class GPBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
