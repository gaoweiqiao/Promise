package com.gaoweiqiao.promise.schduler;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by patrick on 16/8/11.
 */
public class MainThreadSchduler implements SchdulerHandler {
    private static MainThreadSchduler INSTANCE = null;
    private static Handler mainHandler = null;
    public static MainThreadSchduler getInstance(){
        if(null == INSTANCE){
            synchronized (MainThreadSchduler.class){
                if(null == INSTANCE){
                    INSTANCE = new MainThreadSchduler();
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
