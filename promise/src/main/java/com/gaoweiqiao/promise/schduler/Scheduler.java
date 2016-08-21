package com.gaoweiqiao.promise.schduler;


/**
 * Created by patrick on 16/8/11.
 */
public interface Scheduler {
    void handle(Runnable runnable);
}
