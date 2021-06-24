package com.jh.plugin.text;


/**
 * create by jh on 2021/6/10.
 */
public class AsmText {

    private static final String TAG = "AsmText";

    void textAsm(){
        TimeCalculate.start();
        long start=System.currentTimeMillis();
        System.out.println("AsmText");
        long end=System.currentTimeMillis();
        System.out.println("time"+(end-start));
        TimeCalculate.end();

    }
}
