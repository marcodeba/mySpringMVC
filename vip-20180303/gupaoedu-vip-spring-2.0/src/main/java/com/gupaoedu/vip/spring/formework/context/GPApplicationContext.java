package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.annotation.GPAutowired;
import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPService;
import com.gupaoedu.vip.spring.formework.aop.GPAopConfig;
import com.gupaoedu.vip.spring.formework.beans.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.beans.GPBeanPostProcessor;
import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.context.support.GPBeanDefinitionReader;
import com.gupaoedu.vip.spring.formework.core.GPBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {
    private String[] configLocations;
    private GPBeanDefinitionReader reader;
    //用来保证注册式单例的容器
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>();
    //用来存储所有的被代理过的对象
    private Map<String, GPBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, GPBeanWrapper>();

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    // beanName->beanDefinition->beanInstance->beanwrapper
    @Override
    public void refresh() {
        // 读取配置信息到内存
        reader = new GPBeanDefinitionReader(configLocations);

        //加载beanNames
        List<String> beanDefinitionNames = reader.loadBeanDefinitions();

        //注册,把beanName组装成BeanDefinition，并放入BeanDefinitionMap
        doRegistry(beanDefinitionNames);

        //依赖注入（lazy-init = false），要是执行依赖注入
        //在这里自动调用getBean方法
        doAutowrited();
    }

    //开始执行自动化的依赖注入
    private void doAutowrited() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                this.getBean(beanDefinitionEntry.getKey());
            }
        }

        for (Map.Entry<String, GPBeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()) {
            this.populateBean(beanWrapperEntry.getValue().getOriginalInstance());
        }
    }

    public void populateBean(Object instance) {
        Class clazz = instance.getClass();

        //不是所有牛奶都叫特仑苏，对@Service和@Controller注释下有@AutoWired注释的变量进行DI
        if (!(clazz.isAnnotationPresent(GPController.class) ||
                clazz.isAnnotationPresent(GPService.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }

            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //真正的将BeanDefinitions注册到beanDefinitionMap中
    private void doRegistry(List<String> beanDefinitionNames) {
        try {
            for (String beanDefinitionName : beanDefinitionNames) {
                Class<?> clazz = Class.forName(beanDefinitionName);
                if (clazz.isInterface()) {
                    continue;
                }
                GPBeanDefinition beanDefinition = reader.registerBeanDefinition(beanDefinitionName);
                if (beanDefinition != null) {
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                for (Class<?> i : clazz.getInterfaces()) {
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) {
        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        try {
            //生成通知事件
            Object instance = instantionBean(beanDefinition);

            if (null == instance) {
                return null;
            }

            GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private GPAopConfig instantionAopConfig(GPBeanDefinition beanDefinition) throws Exception {
        GPAopConfig config = new GPAopConfig();
        String expression = reader.getConfigProperties().getProperty("pointCut");
        String[] before = reader.getConfigProperties().getProperty("aspectBefore").split("\\s");
        String[] after = reader.getConfigProperties().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);
        Class aspectClass = Class.forName(before[0]);
        //在这里得到的方法都是原生的方法
        for (Method m : clazz.getMethods()) {
            //public .* com\.gupaoedu\.vip\.spring\.demo\.service\..*Service\..*\(.*\)
            //public java.lang.String com.gupaoedu.vip.spring.demo.service.impl.ModifyService.add(java.lang.String,java.lang.String)
            Matcher matcher = pattern.matcher(m.toString());
            if (matcher.matches()) {
                //能满足切面规则的类，添加的AOP配置中
                config.put(m, aspectClass.newInstance(), new Method[]{aspectClass.getMethod(before[1]), aspectClass.getMethod(after[1])});
            }
        }

        return config;
    }

    //传一个BeanDefinition，就返回一个实例Bean
    private Object instantionBean(GPBeanDefinition beanDefinition) {
        Object instance;
        String beanName = beanDefinition.getBeanClassName();
        try {
            if (this.beanCacheMap.containsKey(beanName)) {
                instance = beanCacheMap.get(beanName);
            } else {
                Class<?> clazz = Class.forName(beanName);
                instance = clazz.newInstance();
                this.beanCacheMap.put(beanName, instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfigProperties();
    }
}
