package com.gupaoedu.vip.pattern.singleton.lazy;

/**
 * Created by Tom on 2018/3/7.
 */
public class LazyOne {
    private LazyOne(){}


    private static  LazyOne lazy = null;

    public static LazyOne getInstance(){

        if(lazy == null){
            lazy = new LazyOne();
        }
        return lazy;

    }

}
