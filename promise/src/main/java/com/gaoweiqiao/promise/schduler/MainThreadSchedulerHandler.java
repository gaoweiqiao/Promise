package com.gaoweiqiao.promise.schduler;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by patrick on 16/8/11.
 */
public class MainThreadSchedulerHandler implements SchedulerHandler {
    private static MainThreadSchedulerHandler INSTANCE = null;
    private static Handler mainHandler = null;
    public static MainThreadSchedulerHandler getInstance(){
        if(null == INSTANCE){
            synchronized (MainThreadSchedulerHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new MainThreadSchedulerHandler();
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return INSTANCE;
    }
    @Override
    public void handle(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
