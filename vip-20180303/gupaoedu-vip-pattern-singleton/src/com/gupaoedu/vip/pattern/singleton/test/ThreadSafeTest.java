package com.gupaoedu.vip.pattern.singleton.test;

import com.gupaoedu.vip.pattern.singleton.hungry.Hungry;
import com.gupaoedu.vip.pattern.singleton.lazy.LazyOne;
import com.gupaoedu.vip.pattern.singleton.lazy.LazyThree;
import com.gupaoedu.vip.pattern.singleton.lazy.LazyTwo;
import com.gupaoedu.vip.pattern.singleton.register.RegiterEnum;
import com.gupaoedu.vip.pattern.singleton.seriable.Seriable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Tom on 2018/3/7.
 */
public class ThreadSafeTest {

    public static void main(String[] args) {

        /*
        int count = 200000;
//        CountDownLatch latch = new CountDownLatch(count);

        //final Set<Hungry> syncSet = Collections.synchronizedSet(new HashSet<Hungry>());

        long start = System.currentTimeMillis();
        for (int i = 0; i < count;i ++) {
//            Object obj = LazyTwo.getInstance();

//            new Thread(){
//                @Override
//                public void run() {
//                  // syncSet.add(Hungry.getInstance());
//
////                    Object obj = LazyOne.getInstance();
////                    System.out.println(System.currentTimeMillis() + ":" + obj);
//
//                    try{

                        Object obj = LazyThree.getInstance();
                        System.out.println(System.currentTimeMillis() + ":" + obj);
//                        //System.out.println();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }.start();

//            latch.countDown();

        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start));

        try {
//            latch.await();
//            Thread.sleep(1000L);
        }catch(Exception e){
            e.printStackTrace();
        }
    */



        try{

            Class<?> clazz = LazyThree.class;
            Constructor [] cs =  clazz.getDeclaredConstructors();
            for (Constructor c: cs) {
                //用反射强制访问
                c.setAccessible(true);
//                System.out.println(c);
                Object o =  c.newInstance();
            }

//           Constructor c = clazz.getConstructor();
//            c.setAccessible(true);
//            c.newInstance();

        }catch (Exception e){
            e.printStackTrace();
        }


       // RegiterEnum.INSTANCE.getInstance();


        Seriable s1 = null;
        Seriable s2 = Seriable.getInstance();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("Seriable.obj");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(s2);
            oos.flush();
            oos.close();


            FileInputStream fis = new FileInputStream("Seriable.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            s1 = (Seriable)ois.readObject();
            ois.close();

            System.out.println(s1);
            System.out.println(s2);
            System.out.println(s1 == s2);



        } catch (Exception e) {
            e.printStackTrace();
        }




    }

}
