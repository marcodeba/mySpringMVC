package com.gupaoedu.vip.spring.formework.context;

public abstract class GPAbstractApplicationContext {

    //提供给子类重写
    protected void refresh() {
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();

}
