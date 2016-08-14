package com.gaoweiqiao.promise.schduler;

import com.gaoweiqiao.quiff.promise.Promise;

/**
 * Created by patrick on 16/8/11.
 */
public class PromiseSchdulerHandler implements SchdulerHandler{
    private static PromiseSchdulerHandler INSTANCE = null;
    public static PromiseSchdulerHandler getInstance(){
        if(null == INSTANCE){
            synchronized (PromiseSchdulerHandler.class){
                if(null == INSTANCE){
                    INSTANCE = new PromiseSchdulerHandler();
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
