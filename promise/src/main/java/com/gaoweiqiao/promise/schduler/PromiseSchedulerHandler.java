package com.gaoweiqiao.promise.schduler;


import com.gaoweiqiao.promise.Promise;

/**
 * Created by patrick on 16/8/11.
 */
public class PromiseSchedulerHandler implements SchedulerHandler {
    private static PromiseSchedulerHandler INSTANCE = null;
    public static PromiseSchedulerHandler getInstance(){
        if(null == INSTANCE){
            synchronized (PromiseSchedulerHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new PromiseSchedulerHandler();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void handle(Runnable runnable) {
        Promise.getPromiseHandler().post(runnable);
    }
}
