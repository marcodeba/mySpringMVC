package com.gupaoedu.vip.pattern.singleton.lazy;

/**
 * Created by Tom on 2018/3/7.
 */
public class LazyThree {

    private static  boolean initialized = false;

    //默认使用LazyThree的时候，会先初始化内部类
    //如果没使用的话，内部类是不加载的

    private LazyThree(){

        synchronized (LazyThree.class){
            if(initialized == false){
                initialized = !initialized;
            }else{
                throw  new RuntimeException("单例已被侵犯");
            }
        }

    }

    public static final LazyThree getInstance(){
        return LazyHolder.LAZY;
    }


    private static class LazyHolder{
        private static final LazyThree LAZY = new LazyThree();
    }


}
