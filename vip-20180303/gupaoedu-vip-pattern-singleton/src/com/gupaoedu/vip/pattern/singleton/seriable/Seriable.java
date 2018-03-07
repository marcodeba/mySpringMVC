package com.gupaoedu.vip.pattern.singleton.seriable;

import java.io.Serializable;

/**
 * Created by Tom on 2018/3/7.
 */
public class Seriable implements Serializable {

    public  final static Seriable INSTANCE = new Seriable();
    private Seriable(){}

    public static  Seriable getInstance(){
        return INSTANCE;
    }

    private  Object readResolve(){
        return  INSTANCE;
    }

}
