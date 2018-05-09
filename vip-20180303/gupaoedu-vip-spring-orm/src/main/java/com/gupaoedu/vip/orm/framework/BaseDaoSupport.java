package com.gupaoedu.vip.orm.framework;

import javax.sql.DataSource;

/**
 * 抽象类里面可以写一些默认的功能
 * 但是不能被实例化，被实例化之前必须给我把动态参数配置好
 * 通过子类继承父类，然后new子类，就会先new，子类把配信息传送父类
 */
public abstract class BaseDaoSupport {

    protected abstract void setDataSource(DataSource dataSource);

}
