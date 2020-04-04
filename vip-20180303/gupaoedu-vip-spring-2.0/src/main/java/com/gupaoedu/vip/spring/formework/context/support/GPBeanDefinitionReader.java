package com.gupaoedu.vip.spring.formework.context.support;

import com.gupaoedu.vip.spring.formework.beans.GPBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//用对配置文件进行查找，读取、解析
public class GPBeanDefinitionReader {
    private final String SCAN_PACKAGE = "scanPackage";
    private Properties configProperties = new Properties();
    // 保存扫描类的名称
    private List<String> registryBeanClasses = new ArrayList<String>();

    public GPBeanDefinitionReader(String... locations) {
        //在Spring中是通过BeanDefinitionReader去查找和定位
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            configProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 扫描class
        this.doScanner(configProperties.getProperty(SCAN_PACKAGE));
    }

    public List<String> loadBeanDefinitions() {
        return this.registryBeanClasses;
    }

    //每注册一个className，就返回一个BeanDefinition，我自己包装
    //只是为了对配置信息进行一个包装
    public GPBeanDefinition registerBeanDefinition(String beanDefinitionName) {
        if (this.registryBeanClasses.contains(beanDefinitionName)) {
            GPBeanDefinition beanDefinition = new GPBeanDefinition();
            beanDefinition.setBeanClassName(beanDefinitionName);
            beanDefinition.setFactoryBeanName(lowerFirstCase(beanDefinitionName.substring(beanDefinitionName.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

    //递归扫描所有的相关联的class，并且保存到一个List中
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                this.doScanner(scanPackage + "." + file.getName());
            } else {
                registryBeanClasses.add(scanPackage + "." + file.getName().replace(".class", ""));
            }
        }
    }

    public Properties getConfigProperties() {
        return this.configProperties;
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
