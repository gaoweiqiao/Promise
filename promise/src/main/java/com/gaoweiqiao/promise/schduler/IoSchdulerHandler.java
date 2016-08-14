package com.gaoweiqiao.promise.schduler;

import com.gaoweiqiao.quiff.promise.PromiseHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by patrick on 16/8/11.
 */
public class IoSchdulerHandler implements SchdulerHandler{
    private static IoSchdulerHandler INSTANCE = null;
    private static ExecutorService cachedThreadPool = null;
    public static IoSchdulerHandler getInstance(){
        if(null == INSTANCE){
            synchronized (IoSchdulerHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new IoSchdulerHandler();
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
