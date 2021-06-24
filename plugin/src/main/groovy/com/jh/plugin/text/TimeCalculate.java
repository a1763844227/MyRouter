package com.jh.plugin.text;

/**
 * create by jh on 2021/6/11.
 */
public class TimeCalculate {

    public static ThreadLocal<Long> t=new ThreadLocal<>();

    public static void start(){
        t.set(System.currentTimeMillis());
    }

    public static void end(){
        System.out.println(Thread.currentThread().getStackTrace()[2]+"haoshi:"+(System.currentTimeMillis()-t.get()));
    }
}
