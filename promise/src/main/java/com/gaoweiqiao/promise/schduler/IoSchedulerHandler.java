package com.gaoweiqiao.promise.schduler;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by patrick on 16/8/11.
 */
public class IoSchedulerHandler implements SchedulerHandler {
    private static IoSchedulerHandler INSTANCE = null;
    private static ExecutorService cachedThreadPool = null;
    public static IoSchedulerHandler getInstance(){
        if(null == INSTANCE){
            synchronized (IoSchedulerHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new IoSchedulerHandler();
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
