package com.jh.router;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create by jh on 2021/3/26.
 */
public class DefaultPoolExecutor extends ThreadPoolExecutor {

    private static DefaultPoolExecutor defaultPoolExecutor;

    private static final int MIN_THREAD_COUNT=Runtime.getRuntime().availableProcessors()+1;
    private static final int MAX_THREAD_COUNT=MIN_THREAD_COUNT+1;



    public static DefaultPoolExecutor getInstance(){
        if (defaultPoolExecutor==null) {
            synchronized (DefaultPoolExecutor.class){
                if (defaultPoolExecutor==null) {
                    defaultPoolExecutor=new DefaultPoolExecutor(MIN_THREAD_COUNT,MAX_THREAD_COUNT,
                            30,TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));

                }
            }
        }

        return defaultPoolExecutor;
    }


    private DefaultPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
}
