package com.gaoweiqiao.promise.schduler;


import com.gaoweiqiao.promise.Promise;

/**
 * Created by patrick on 16/8/11.
 */
public class PromiseScheduler implements Scheduler {
    private static PromiseScheduler INSTANCE = null;
    public static PromiseScheduler getInstance(){
        if(null == INSTANCE){
            synchronized (PromiseScheduler.class){
                if(null == INSTANCE){
                    INSTANCE = new PromiseScheduler();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void handle(Runnable runnable) {
        Promise.getPromiseThreadHandler().post(runnable);
    }
}
