package com.gaoweiqiao.promise.schduler;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by patrick on 16/8/11.
 */
public class IoScheduler implements Scheduler {
    private static IoScheduler INSTANCE = null;
    private static ExecutorService cachedThreadPool = null;
    public static IoScheduler getInstance(){
        if(null == INSTANCE){
            synchronized (IoScheduler.class){
                if(null == INSTANCE){
                    INSTANCE = new IoScheduler();
                    cachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void handle(Runnable runnable) {
        cachedThreadPool.submit(runnable);
    }
}
