package com.gaoweiqiao.promise.schduler;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by patrick on 16/8/11.
 */
public class MainThreadScheduler implements Scheduler {
    private static MainThreadScheduler INSTANCE = null;
    private static Handler mainHandler = null;
    public static MainThreadScheduler getInstance(){
        if(null == INSTANCE){
            synchronized (MainThreadScheduler.class){
                if(null == INSTANCE){
                    INSTANCE = new MainThreadScheduler();
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
