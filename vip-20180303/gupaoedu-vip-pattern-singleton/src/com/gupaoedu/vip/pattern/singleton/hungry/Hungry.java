package com.gupaoedu.vip.pattern.singleton.hungry;

/**
 * Created by Tom on 2018/3/7.
 */
public class Hungry {

    private Hungry(){}
    //先静态、后动态
    //先属性、后方法
    //先上后下
    private static final Hungry hungry = new Hungry();

    public static Hungry getInstance(){
//        Hungry hungry;

        return  hungry;
    }

}
